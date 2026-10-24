package com.example.flagforge.service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.flagforge.entity.Project;
import com.example.flagforge.entity.User;
import com.example.flagforge.repository.ProjectRepository;
import com.example.flagforge.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
@Service
public class ProjectService {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final EnvironmentService environmentService;

    public ProjectService(UserRepository userRepository, ProjectRepository projectRepository, EnvironmentService environmentService) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.environmentService = environmentService;
    }


    private User getCurrentUser(){
        String email= SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(()->
                new RuntimeException("User not found"));

    }
    @Transactional
    public Project createProject(String name, String description) {
        User owner = getCurrentUser();

        Project project = new Project(name, description, owner);

        Project savedProject = projectRepository.save(project);

        environmentService.createDefaultEnvironment(savedProject);

        return savedProject;
    }
    @Transactional(readOnly = true)
    public List<Project> getMyProjects() {
        User owner = getCurrentUser();

        return projectRepository.findAllByOwner_Id(owner.getId());
    }
    @Transactional(readOnly = true)
    public Project getMyProjectById(Long projectId) {
        User owner = getCurrentUser();

        return projectRepository
                .findByIdAndOwner_Id(projectId, owner.getId())
                .orElseThrow(() ->
                        new RuntimeException("Project not found"));
    }

}
