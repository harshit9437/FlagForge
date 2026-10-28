package com.example.flagforge.service;

import com.example.flagforge.entity.EnvironmentFlagConfig;
import com.example.flagforge.repository.EnvironmentFlagConfigRepository;
import com.example.flagforge.repository.EnvironmentRepository;
import com.example.flagforge.repository.FeatureFlagRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FeatureFlagEvaluatorService {
    private final FeatureFlagRepository featureFlagRepository;
    private final EnvironmentRepository environmentRepository;
    private final EnvironmentFlagConfigRepository configRepository;

    public FeatureFlagEvaluatorService(
            FeatureFlagRepository featureFlagRepository,
            EnvironmentRepository environmentRepository,
            EnvironmentFlagConfigRepository configRepository
    ) {
        this.featureFlagRepository = featureFlagRepository;
        this.environmentRepository = environmentRepository;
        this.configRepository = configRepository;
    }
    @Transactional(readOnly = true)
    public boolean evaluate(
            Long projectId,
            String flagKey,
            Long environmentId,
            Long userId
           ){
        // 1. Find feature flag inside the given project
        var flag=featureFlagRepository.findByKeyAndProject_Id(flagKey,projectId).orElseThrow(
                ()->new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "flag not found"
                )
        );
        // 2. Verify environment belongs to the same project
        var environment=environmentRepository.findByIdAndProject_Id(environmentId,projectId)
                .orElseThrow(()->new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "environment not found "
                ));
        // 3. Fetch environment-specific configuration
        EnvironmentFlagConfig config=configRepository.findByFeatureFlag_IdAndEnvironment_Id(flag.getId(),
                environment.getId()).orElse(null);
        // 4. Apply existing rollout logic
        return isEnabledForUser(config,userId,flagKey);
    }


    public int calculateBucket(Long userId, String flagKey) {
        String input = userId + ":" + flagKey;

        return Math.floorMod(input.hashCode(), 100);
    }


    public boolean isEnabledForUser(
            EnvironmentFlagConfig config,
            Long userId,
            String flagKey
    ) {
        // Missing configuration: fail closed
        if (config == null) {
            return false;
        }

        // Disabled feature: never enable it
        if (!config.isEnabled()) {
            return false;
        }

        int rollout = config.getRolloutPercentage();

        // Nobody receives the feature
        if (rollout <= 0) {
            return false;
        }

        // Everyone receives the feature
        if (rollout >= 100) {
            return true;
        }
        if (userId == null || flagKey == null || flagKey.isBlank()) {
            throw new IllegalArgumentException(
                    "User ID and feature flag key are required"
            );
        }

        // Partial rollout: use a stable bucket
        int bucket = calculateBucket(userId, flagKey);

        return bucket < rollout;
    }

}
