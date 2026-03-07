package com.sellio.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
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
    private List<PropertyDependencyEntity> children = new ArrayList<>();

    @OneToMany(mappedBy = "child")
    private List<PropertyDependencyEntity> parents = new ArrayList<>();
}
