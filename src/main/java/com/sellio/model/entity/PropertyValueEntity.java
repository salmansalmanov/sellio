package com.sellio.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "property_values",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"property_id", "value"})
        }
)
public class PropertyValueEntity extends BaseEntity {
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id")
    private PropertyEntity property;

    @OneToMany(
            mappedBy = "parent",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<PropertyDependencyEntity> children = new ArrayList<>();

    @OneToMany(mappedBy = "child")
    @Builder.Default
    private List<PropertyDependencyEntity> parents = new ArrayList<>();
}
