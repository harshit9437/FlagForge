
package com.example.flagforge.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(
        name = "feature_flags",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_feature_flag_project_key",
                        columnNames = {"project_id", "flag_key"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class FeatureFlag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100,unique = true)
    private String name;

    @NotBlank
    @Size(max = 100)
    @Column(name = "flag_key", nullable = false, length = 100)
    private String key;

    @Size(max = 500)
    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public FeatureFlag(
            String name,
            String key,
            String description,
            Project project
    ) {
        this.name = name;
        this.key = key;
        this.description = description;
        this.project = project;
    }
}
