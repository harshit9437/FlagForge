package com.example.flagforge.service;

import com.example.flagforge.dto.EnvironmentResponse;
import com.example.flagforge.entity.Environment;
import com.example.flagforge.entity.EnvironmentType;
import com.example.flagforge.entity.Project;
import com.example.flagforge.entity.User;
import com.example.flagforge.repository.EnvironmentRepository;
import com.example.flagforge.repository.ProjectRepository;
import com.example.flagforge.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
@Service
public class EnvironmentService {
    private final EnvironmentRepository environmentRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public EnvironmentService(EnvironmentRepository environmentRepository, UserRepository userRepository, ProjectRepository projectRepository) {
        this.environmentRepository = environmentRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }
    public void createDefaultEnvironment(Project project){
        for (EnvironmentType type : EnvironmentType.values()) {
            Environment environment = new Environment(type, project);
            environmentRepository.save(environment);
        }
    }
    @Transactional(readOnly = true)
    public List<EnvironmentResponse> getProjectEnvironments(Long projectId) {
        String email= SecurityContextHolder.getContext().
                getAuthentication().getName();

        User currentUser=userRepository.findByEmail(email).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));

        Project project=projectRepository.findByIdAndOwner_Id(projectId, currentUser.getId())
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Project Not found"));

        return environmentRepository.findAllByProject_Id(project.getId())
                .stream()
                .map(environment -> new EnvironmentResponse(
                        environment.getId(),
                        environment.getName(),
                        environment.getCreatedAt()
                )).toList();

    }
}
