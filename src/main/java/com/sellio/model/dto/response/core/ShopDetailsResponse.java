package com.sellio.model.dto.response.core;

import com.sellio.model.enums.PricingPlan;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShopDetailsResponse extends UserResponse {
    private UUID id;
    private String name;
    private String email;
    private String description;
    private PricingPlan pricingPlan;
    private List<String> phoneNumbers;
    private String logoUrl;
    private String bannerUrl;
    private List<String> addresses;
    private Long viewCount = 0L;
}
