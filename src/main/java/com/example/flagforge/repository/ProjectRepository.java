package com.example.flagforge.repository;

import com.example.flagforge.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project,Long> {
    List<Project> findAllByOwner_Id(Long ownerId);
    Optional<Project> findByIdAndOwner_Id(Long id, Long ownerId);
}
