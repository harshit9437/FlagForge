
package com.example.flagforge.service;

import com.example.flagforge.entity.Environment;
import com.example.flagforge.entity.EnvironmentFlagConfig;
import com.example.flagforge.entity.FeatureFlag;
import com.example.flagforge.repository.EnvironmentFlagConfigRepository;
import com.example.flagforge.repository.EnvironmentRepository;
import com.example.flagforge.repository.FeatureFlagRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeatureFlagEvaluatorServiceTest {

    @Mock
    private FeatureFlagRepository featureFlagRepository;

    @Mock
    private EnvironmentRepository environmentRepository;

    @Mock
    private EnvironmentFlagConfigRepository configRepository;

    @InjectMocks
    private FeatureFlagEvaluatorService evaluator;

    @Test
    void nullConfigShouldReturnFalse() {
        assertFalse(
                evaluator.isEnabledForUser(null, 1L, "new-dashboard")
        );
    }

    @Test
    void disabledFeatureShouldReturnFalse() {
        EnvironmentFlagConfig config =
                new EnvironmentFlagConfig(null, null, false, 100);

        assertFalse(
                evaluator.isEnabledForUser(config, 1L, "new-dashboard")
        );
    }

    @Test
    void zeroPercentRolloutShouldReturnFalse() {
        EnvironmentFlagConfig config =
                new EnvironmentFlagConfig(null, null, true, 0);

        assertFalse(
                evaluator.isEnabledForUser(config, 1L, "new-dashboard")
        );
    }

    @Test
    void hundredPercentRolloutShouldReturnTrue() {
        EnvironmentFlagConfig config =
                new EnvironmentFlagConfig(null, null, true, 100);

        assertTrue(
                evaluator.isEnabledForUser(config, 1L, "new-dashboard")
        );
    }

    @Test
    void sameUserShouldGetConsistentResult() {
        EnvironmentFlagConfig config =
                new EnvironmentFlagConfig(null, null, true, 30);

        boolean first = evaluator.isEnabledForUser(
                config, 42L, "new-dashboard"
        );

        boolean second = evaluator.isEnabledForUser(
                config, 42L, "new-dashboard"
        );

        assertEquals(first, second);
    }

    @Test
    void rolloutShouldMatchUserBucket() {
        Long userId = 42L;
        String flagKey = "new-dashboard";
        int rollout = 30;

        EnvironmentFlagConfig config =
                new EnvironmentFlagConfig(null, null, true, rollout);

        int bucket = evaluator.calculateBucket(userId, flagKey);

        assertEquals(
                bucket < rollout,
                evaluator.isEnabledForUser(config, userId, flagKey)
        );
    }

    @Test
    void evaluateShouldReturnFalseWhenConfigIsMissing() {
        FeatureFlag flag = mock(FeatureFlag.class);
        when(flag.getId()).thenReturn(2L);

        Environment environment = mock(Environment.class);
        when(environment.getId()).thenReturn(1L);

        when(featureFlagRepository
                .findByKeyAndProject_Id("new-dashboard", 10L))
                .thenReturn(Optional.of(flag));

        when(environmentRepository
                .findByIdAndProject_Id(1L, 10L))
                .thenReturn(Optional.of(environment));

        when(configRepository
                .findByFeatureFlag_IdAndEnvironment_Id(2L, 1L))
                .thenReturn(Optional.empty());

        assertFalse(
                evaluator.evaluate(10L, "new-dashboard", 1L, 42L)
        );
    }

    @Test
    void evaluateShouldReturn404WhenFlagIsMissing() {
        when(featureFlagRepository
                .findByKeyAndProject_Id("missing-flag", 10L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> evaluator.evaluate(
                        10L, "missing-flag", 1L, 42L
                )
        );

        assertEquals(404, exception.getStatusCode().value());

        verifyNoInteractions(environmentRepository, configRepository);
    }

    @Test
    void evaluateShouldReturn404WhenEnvironmentIsMissing() {
        FeatureFlag flag = mock(FeatureFlag.class);


        when(featureFlagRepository
                .findByKeyAndProject_Id("new-dashboard", 10L))
                .thenReturn(Optional.of(flag));

        when(environmentRepository
                .findByIdAndProject_Id(1L, 10L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> evaluator.evaluate(
                        10L, "new-dashboard", 1L, 42L
                )
        );

        assertEquals(404, exception.getStatusCode().value());

        verifyNoInteractions(configRepository);
    }
}
