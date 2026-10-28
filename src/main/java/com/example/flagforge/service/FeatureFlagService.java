package com.example.flagforge.service;

import com.example.flagforge.dto.FeatureFlagResponse;
import com.example.flagforge.dto.FeatureFlagUpdateRequest;
import com.example.flagforge.entity.FeatureFlag;
import com.example.flagforge.entity.Project;
import com.example.flagforge.entity.User;
import com.example.flagforge.repository.FeatureFlagRepository;
import com.example.flagforge.repository.ProjectRepository;
import com.example.flagforge.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class FeatureFlagService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final FeatureFlagRepository featureFlagRepository;

    public FeatureFlagService(ProjectRepository projectRepository, UserRepository userRepository, FeatureFlagRepository featureFlagRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.featureFlagRepository = featureFlagRepository;
    }

    public FeatureFlag createFeatureFlag(
            Long projectId,
            String name,
            String key,
            String description
    ) {
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        User currentUser = userRepository.findByEmail(email).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User not found"));
        Project project = projectRepository
                .findByIdAndOwner_Id(projectId, currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Project not found"
                ));
        if (featureFlagRepository.existsByProject_IdAndKey(projectId, key)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Feature flag key already exists in this project"
            );

        }
        FeatureFlag featureFlag = new FeatureFlag(
                name, key, description, project
        );

        return featureFlagRepository.save(featureFlag);


    }

    @Transactional(readOnly = true)
    public List<FeatureFlagResponse> getProjectFlags(Long projectId) {
        User currentUser = getCurrentUser();

        Project project = projectRepository
                .findByIdAndOwner_Id(projectId, currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Project not found"
                ));

        return featureFlagRepository.findAllByProject_Id(project.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public FeatureFlagResponse getFeatureFlag(Long projectId, Long flagId) {
        User currentUser = getCurrentUser();

        projectRepository.findByIdAndOwner_Id(projectId, currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Project not found"
                ));

        FeatureFlag flag = featureFlagRepository
                .findByIdAndProject_Id(flagId, projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Feature flag not found"
                ));

        return toResponse(flag);
    }
    @Transactional
    public FeatureFlagResponse updateFeatureFlag(Long projectId,
                                                 Long flagId,
                                                 FeatureFlagUpdateRequest request) {
        User currentUser = getCurrentUser();
        Project project = projectRepository.findByIdAndOwner_Id(projectId, currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Project not found"
                ));
        FeatureFlag flag = featureFlagRepository.findByIdAndProject_Id(flagId, project.getId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "feature flag not found"
                        ));
        boolean keyChanged = !flag.getKey().equals(request.key());
        if (keyChanged && featureFlagRepository
                .existsByProject_IdAndKey(projectId, request.key())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Feature flag key already exists in this project"
            );
        }
        flag.setName(request.name());
        flag.setKey(request.key());
        flag.setDescription(request.description());
        FeatureFlag updateFlag = featureFlagRepository.save(flag);
        return toResponse(updateFlag);
    }

    @Transactional
    public void deleteFeatureFlag(Long projectId, Long flagId) {
        User currentUser = getCurrentUser();

        Project project = projectRepository
                .findByIdAndOwner_Id(projectId, currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Project not found"
                ));

        FeatureFlag flag = featureFlagRepository
                .findByIdAndProject_Id(flagId, project.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Feature flag not found"
                ));

        featureFlagRepository.delete(flag);
    }


    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"
                ));
    }


    private FeatureFlagResponse toResponse(FeatureFlag flag) {
        return new FeatureFlagResponse(
                flag.getId(),
                flag.getName(),
                flag.getKey(),
                flag.getDescription(),
                flag.getProject().getId(),
                flag.getCreatedAt()
        );
    }
}
