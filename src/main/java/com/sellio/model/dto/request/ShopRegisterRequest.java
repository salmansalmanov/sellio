package com.sellio.model.dto.request;

import com.sellio.model.enums.PricingPlan;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShopRegisterRequest extends RegisterRequest {
    private String name;
    private String email;
    private String description;
    private PricingPlan pricingPlan;
    private List<String> phoneNumbers;
}
