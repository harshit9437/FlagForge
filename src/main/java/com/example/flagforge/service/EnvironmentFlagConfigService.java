package com.example.flagforge.service;

import com.example.flagforge.dto.EnvironmentFlagConfigRequest;
import com.example.flagforge.dto.EnvironmentFlagConfigResponse;
import com.example.flagforge.entity.*;
import com.example.flagforge.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EnvironmentFlagConfigService {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final FeatureFlagRepository featureFlagRepository;
    private final EnvironmentRepository environmentRepository;
    private final EnvironmentFlagConfigRepository configRepository;
    public EnvironmentFlagConfigService(
            UserRepository userRepository,
            ProjectRepository projectRepository,
            FeatureFlagRepository featureFlagRepository,
            EnvironmentRepository environmentRepository,
            EnvironmentFlagConfigRepository configRepository
    ) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.featureFlagRepository = featureFlagRepository;
        this.environmentRepository = environmentRepository;
        this.configRepository = configRepository;
    }

    @Transactional
    public EnvironmentFlagConfigResponse createConfig(
            Long projectId,
            Long flagId,
            Long environmentId,
            boolean enabled,
            int rolloutPercentage
    ) {
        // 1. Resolve authenticated user
        User user = getCurrentUser();

        // 2. Verify project ownership
        Project project = getOwnedProject(projectId, user.getId());

        // 3. Verify flag belongs to this project
        FeatureFlag flag = getProjectFlag(flagId, project.getId());

        // 4. Verify environment belongs to this project
        Environment environment =
                getProjectEnvironment(environmentId, project.getId());

        // 5. Validate rollout percentage
        if (rolloutPercentage < 0 || rolloutPercentage > 100) {
            throw new IllegalArgumentException(
                    "Rollout percentage must be between 0 and 100"
            );
        }

        // 6. Prevent duplicate configuration
        boolean exists = configRepository
                .existsByFeatureFlag_IdAndEnvironment_Id(
                        flag.getId(),
                        environment.getId()
                );

        if (exists) {
            throw new IllegalStateException(
                    "Configuration already exists for this flag and environment"
            );
        }

        // 7. Create and save configuration
        EnvironmentFlagConfig config = new EnvironmentFlagConfig(
                flag,
                environment,
                enabled,
                rolloutPercentage
        );

        EnvironmentFlagConfig savedConfig = configRepository.save(config);

        return new EnvironmentFlagConfigResponse(
                savedConfig.getId(),
                savedConfig.getFeatureFlag().getId(),
                savedConfig.getEnvironment().getId(),
                savedConfig.isEnabled(),
                savedConfig.getRolloutPercentage(),
                savedConfig.getCreatedAt(),
                savedConfig.getUpdatedAt()
        );
    }
    @Transactional(readOnly = true)
    public EnvironmentFlagConfigResponse getConfig(
            Long projectId,
            Long flagId,
            Long environmentId
    ){
        //Getting user
        User currenUser=getCurrentUser();
        //Getting project
        Project project=getOwnedProject(projectId, currenUser.getId());
        //Getting Flag
        FeatureFlag flag=featureFlagRepository.findByIdAndProject_Id(flagId,project.getId()).orElseThrow(
                ()->new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Flag is not found")
        );
        //Getting environment
        Environment environment =
                getProjectEnvironment(environmentId, project.getId());
        EnvironmentFlagConfig config=configRepository.findByFeatureFlag_IdAndEnvironment_Id(
                flag.getId(),
                environment.getId()
        ).orElseThrow(()->new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Configuration is not found"
        ));
        return new EnvironmentFlagConfigResponse(
                config.getId(),
                config.getFeatureFlag().getId(),
                config.getEnvironment().getId(),
                config.isEnabled(),
                config.getRolloutPercentage(),
                config.getCreatedAt(),
                config.getUpdatedAt()
        );
    }

    @Transactional
    public EnvironmentFlagConfigResponse updateConfig(
            Long projectId,
            Long flagId,
            Long environmentId,
            boolean enabled,
            int rolloutPercentage
    ) {
        User user = getCurrentUser();

        Project project = getOwnedProject(projectId, user.getId());

        FeatureFlag flag = getProjectFlag(flagId, project.getId());

        Environment environment =
                getProjectEnvironment(environmentId, project.getId());

        if (rolloutPercentage < 0 || rolloutPercentage > 100) {
            throw new IllegalArgumentException(
                    "Rollout percentage must be between 0 and 100"
            );
        }

        EnvironmentFlagConfig config = configRepository
                .findByFeatureFlag_IdAndEnvironment_Id(
                        flag.getId(),
                        environment.getId()
                )
                .orElseThrow(() ->
                        new RuntimeException("Configuration not found"));

        config.setEnabled(enabled);
        config.setRolloutPercentage(rolloutPercentage);

        EnvironmentFlagConfig updatedConfig =
                configRepository.save(config);

        return new EnvironmentFlagConfigResponse(
                updatedConfig.getId(),
                updatedConfig.getFeatureFlag().getId(),
                updatedConfig.getEnvironment().getId(),
                updatedConfig.isEnabled(),
                updatedConfig.getRolloutPercentage(),
                updatedConfig.getCreatedAt(),
                updatedConfig.getUpdatedAt()
        );
    }



    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

    private Project getOwnedProject(Long projectId, Long ownerId) {
        return projectRepository
                .findByIdAndOwner_Id(projectId, ownerId)
                .orElseThrow(() ->
                        new RuntimeException("Project not found"));
    }

    private FeatureFlag getProjectFlag(Long flagId, Long projectId) {
        return featureFlagRepository
                .findByIdAndProject_Id(flagId, projectId)
                .orElseThrow(() ->
                        new RuntimeException("Feature flag not found"));
    }

    private Environment getProjectEnvironment(
            Long environmentId,
            Long projectId
    ) {
        return environmentRepository
                .findByIdAndProject_Id(environmentId, projectId)
                .orElseThrow(() ->
                        new RuntimeException("Environment not found"));
    }


}
