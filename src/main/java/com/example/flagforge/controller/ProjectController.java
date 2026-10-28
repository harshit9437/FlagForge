package com.example.flagforge.controller;

import com.example.flagforge.dto.EnvironmentResponse;
import com.example.flagforge.dto.ProjectRequest;
import com.example.flagforge.dto.ProjectResponse;
import com.example.flagforge.entity.Project;
import com.example.flagforge.repository.EnvironmentRepository;
import com.example.flagforge.service.EnvironmentService;
import com.example.flagforge.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService projectService;
    private final EnvironmentService environmentService;

    public ProjectController(ProjectService projectService, EnvironmentService environmentService) {
        this.projectService = projectService;
        this.environmentService = environmentService;
    }
    private ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getCreatedAt()
        );
    }
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse createProject(
            @Valid @RequestBody ProjectRequest request) {

        Project project = projectService.createProject(
                request.name(),
                request.description()
        );

        return toResponse(project);
    }
    @GetMapping
    public List<ProjectResponse> getMyProjects() {
        return projectService.getMyProjects()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ProjectResponse getProjectById(@PathVariable Long id) {
        return toResponse(projectService.getMyProjectById(id));
    }
    @GetMapping("/{projectId}/environments")
    public ResponseEntity<List<EnvironmentResponse>> getProjectEnvironments(
            @PathVariable Long projectId
    ){
        return ResponseEntity.ok(environmentService.getProjectEnvironments(projectId));
    }
}
