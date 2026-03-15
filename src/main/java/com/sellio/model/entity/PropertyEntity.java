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
        name = "properties",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"subcategory_id", "name"})
        }
)
public class PropertyEntity extends BaseEntity {
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subcategory_id")
    private SubcategoryEntity subcategory;

    @OneToMany(
            mappedBy = "property",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<PropertyValueEntity> values = new ArrayList<>();
}
