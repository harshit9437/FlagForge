package com.example.flagforge.service;

import com.example.flagforge.entity.Environment;
import com.example.flagforge.entity.EnvironmentType;
import com.example.flagforge.entity.Project;
import com.example.flagforge.repository.EnvironmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class EnvironmentService {
    private final EnvironmentRepository environmentRepository;

    public EnvironmentService(EnvironmentRepository environmentRepository) {
        this.environmentRepository = environmentRepository;
    }
    public void createDefaultEnvironment(Project project){
        for (EnvironmentType type : EnvironmentType.values()) {
            Environment environment = new Environment(type, project);
            environmentRepository.save(environment);
        }
    }
    public List<Environment> getProjectEnvironments(Long projectId) {
        return environmentRepository.findAllByProject_Id(projectId);
    }
}
