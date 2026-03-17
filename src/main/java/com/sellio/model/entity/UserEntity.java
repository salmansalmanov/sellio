package com.sellio.model.entity;

import com.sellio.model.enums.PricingPlan;
import com.sellio.model.enums.Role;
import com.sellio.model.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class UserEntity extends BaseEntity {
    private String email;
    private String password;

    @ElementCollection
    @CollectionTable(name = "users_phone_numbers", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "phone_number")
    @Builder.Default
    private Set<String> phoneNumbers = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private PricingPlan pricingPlan;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            mappedBy = "owner"
    )
    private List<ListingEntity> listings;
}
