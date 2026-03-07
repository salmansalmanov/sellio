package com.sellio.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "property_dependencies")
public class PropertyDependencyEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_value_id")
    private PropertyValueEntity parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_value_id")
    private PropertyValueEntity child;
}
