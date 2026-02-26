package com.sellio.model.dto.response;

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
public class ShopResponse extends UserResponse {
    private String name;
    private String email;
    private String description;
    private PricingPlan pricingPlan;
    private List<String> phoneNumbers;
    private String logoUrl;
    private String bannerUrl;
}
