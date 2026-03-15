package com.sellio.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Setter
@Getter
@Builder
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
