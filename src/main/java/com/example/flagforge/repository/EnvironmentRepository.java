package com.example.flagforge.repository;

import com.example.flagforge.entity.Environment;
import com.example.flagforge.entity.EnvironmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public interface EnvironmentRepository extends JpaRepository<Environment,Long> {
    List<Environment> findAllByProject_Id(Long projectId);
    Optional<Environment> findByProject_IdAndName(
            Long projectId,
            EnvironmentType environmentType
    );
    boolean existsByProject_IdAndName(
            Long projectId,
            EnvironmentType name
    );

}
