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
@Table(name = "listing_properties")
public class ListingPropertyEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id")
    private ListingEntity listing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "value_id")
    private PropertyValueEntity value;
}
