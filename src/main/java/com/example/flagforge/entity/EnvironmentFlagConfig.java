package com.example.flagforge.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(
        name = "environment_flag_configs",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_flag_environment",
                        columnNames = {
                                "feature_flag_id",
                                "environment_id"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class EnvironmentFlagConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,
            optional = false)
    @JoinColumn(name = "feature_flag_id",nullable = false)
    private FeatureFlag featureFlag;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "environment_id", nullable = false)
    private Environment environment;

    @Column(nullable = false)
    private boolean enabled = false;

    @Min(0)
    @Max(100)
    @Column(name = "rollout_percentage", nullable = false)
    private int rolloutPercentage = 0;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    public EnvironmentFlagConfig(
            FeatureFlag featureFlag,
            Environment environment,
            boolean enabled,
            int rolloutPercentage
    ) {
        this.featureFlag = featureFlag;
        this.environment = environment;
        this.enabled = enabled;
        this.rolloutPercentage = rolloutPercentage;
    }
}