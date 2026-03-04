package com.sellio.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shops")
@NamedEntityGraph(
        name = "ShopWithAddresses",
        attributeNodes = @NamedAttributeNode("addresses")
)
public class ShopEntity extends UserEntity {
    private String name;
    private String description;

    @OneToOne(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    @JoinColumn(name = "logo_id")
    private ImageEntity logo;

    @OneToOne(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    @JoinColumn(name = "banner_id")
    private ImageEntity banner;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            mappedBy = "shop"
    )
    private List<ShopAddressEntity> addresses = new ArrayList<>();
}
