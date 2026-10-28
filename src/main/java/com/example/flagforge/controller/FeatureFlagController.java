
package com.example.flagforge.controller;

import com.example.flagforge.dto.FeatureFlagRequest;
import com.example.flagforge.dto.FeatureFlagResponse;
import com.example.flagforge.dto.FeatureFlagUpdateRequest;
import com.example.flagforge.entity.FeatureFlag;
import com.example.flagforge.service.FeatureFlagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/flags")
@RequiredArgsConstructor
public class FeatureFlagController {

    private final FeatureFlagService featureFlagService;

    @PostMapping
    public ResponseEntity<FeatureFlagResponse> createFeatureFlag(
            @PathVariable Long projectId,
            @Valid @RequestBody FeatureFlagRequest request
    ) {
        FeatureFlag savedFlag = featureFlagService.createFeatureFlag(
                projectId,
                request.name(),
                request.key(),
                request.description()
        );

        FeatureFlagResponse response = new FeatureFlagResponse(
                savedFlag.getId(),
                savedFlag.getName(),
                savedFlag.getKey(),
                savedFlag.getDescription(),
                savedFlag.getProject().getId(),
                savedFlag.getCreatedAt()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<FeatureFlagResponse>> getProjectFlags(
            @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(
                featureFlagService.getProjectFlags(projectId)
        );
    }

    @GetMapping("/{flagId}")
    public ResponseEntity<FeatureFlagResponse> getFeatureFlag(
            @PathVariable Long projectId,
            @PathVariable Long flagId
    ) {
        return ResponseEntity.ok(
                featureFlagService.getFeatureFlag(projectId, flagId)
        );
    }
    @PutMapping("/{flagId}")
    public ResponseEntity<FeatureFlagResponse> updateFeatureFlag(
            @PathVariable Long projectId,
            @PathVariable Long flagId,
            @Valid @RequestBody FeatureFlagUpdateRequest request
    ){
        return ResponseEntity.ok(featureFlagService.updateFeatureFlag(projectId,flagId,request));
    }
    @DeleteMapping("/{flagId}")
    public ResponseEntity<Void> deleteFeatureFlag(@PathVariable Long projectId,
                                                  @PathVariable Long flagId){
        featureFlagService.deleteFeatureFlag(projectId,flagId);
        return ResponseEntity.noContent().build();
    }

}
