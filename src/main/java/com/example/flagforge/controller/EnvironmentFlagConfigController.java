
package com.example.flagforge.controller;

import com.example.flagforge.dto.EnvironmentFlagConfigRequest;
import com.example.flagforge.dto.EnvironmentFlagConfigResponse;
import com.example.flagforge.service.EnvironmentFlagConfigService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects/{projectId}/flags/{flagId}/environments/{environmentId}/config")
public class EnvironmentFlagConfigController {

    private final EnvironmentFlagConfigService configService;

    public EnvironmentFlagConfigController(
            EnvironmentFlagConfigService configService
    ) {
        this.configService = configService;
    }

    @PostMapping
    public ResponseEntity<EnvironmentFlagConfigResponse> createConfig(
            @PathVariable Long projectId,
            @PathVariable Long flagId,
            @PathVariable Long environmentId,
            @Valid @RequestBody EnvironmentFlagConfigRequest request
    ) {
        EnvironmentFlagConfigResponse response =
                configService.createConfig(
                        projectId,
                        flagId,
                        environmentId,
                        request.enabled(),
                        request.rolloutPercentage()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<EnvironmentFlagConfigResponse> getConfig(
            @PathVariable Long projectId,
            @PathVariable Long flagId,
            @PathVariable Long environmentId
    ) {
        EnvironmentFlagConfigResponse response =
                configService.getConfig(
                        projectId,
                        flagId,
                        environmentId
                );

        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<EnvironmentFlagConfigResponse> updateConfig(
            @PathVariable Long projectId,
            @PathVariable Long flagId,
            @PathVariable Long environmentId,
            @Valid @RequestBody EnvironmentFlagConfigRequest request
    ) {
        EnvironmentFlagConfigResponse response =
                configService.updateConfig(
                        projectId,
                        flagId,
                        environmentId,
                        request.enabled(),
                        request.rolloutPercentage()
                );

        return ResponseEntity.ok(response);
    }


}
