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
@Table(name = "subcategories")
public class SubcategoryEntity extends BaseEntity {
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    @OneToMany(
            mappedBy = "subcategory",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<PropertyEntity> properties = new ArrayList<>();
}
