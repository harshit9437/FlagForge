
package com.example.flagforge.repository;

import com.example.flagforge.entity.FeatureFlag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeatureFlagRepository
        extends JpaRepository<FeatureFlag, Long> {

    boolean existsByProject_IdAndKey(Long projectId, String key);

    List<FeatureFlag> findAllByProject_Id(Long projectId);

    Optional<FeatureFlag> findByIdAndProject_Id(
            Long flagId,
            Long projectId
    );
    Optional<FeatureFlag> findByKeyAndProject_Id(
            String key,
            Long projectId
    );

}
