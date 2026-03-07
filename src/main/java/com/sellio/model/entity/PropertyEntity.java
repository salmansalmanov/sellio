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
    private List<PropertyValueEntity> values = new ArrayList<>();
}
