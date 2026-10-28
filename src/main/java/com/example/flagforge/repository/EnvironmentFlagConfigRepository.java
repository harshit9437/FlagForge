package com.example.flagforge.repository;

import com.example.flagforge.entity.EnvironmentFlagConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface EnvironmentFlagConfigRepository extends
        JpaRepository<EnvironmentFlagConfig,Long> {
    Optional<EnvironmentFlagConfig> findByFeatureFlag_IdAndEnvironment_Id(
            Long featureFlagId,
            Long environmentId
    );

    boolean existsByFeatureFlag_IdAndEnvironment_Id(
            Long featureFlagId,
            Long environmentId
    );
}
