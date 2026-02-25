package com.sellio.model.entity;

import com.sellio.model.enums.AccountStatus;
import com.sellio.model.enums.PricingPlan;
import com.sellio.model.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class UserEntity extends BaseEntity {
    private String email;
    private String phoneNumber;
    private String password;
    private String stripeCustomerId;
    private String stripeSubscriptionId;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private PricingPlan pricingPlan;
}
