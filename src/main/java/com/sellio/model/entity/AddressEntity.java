package com.sellio.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "addresses")
public class AddressEntity extends BaseEntity {
    private String placeId;
    private String country;
    private String city;
    private String fullAddress;
    private Double latitude;
    private Double longitude;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            mappedBy = "address"
    )
    private List<ShopAddressEntity> shops;
}
