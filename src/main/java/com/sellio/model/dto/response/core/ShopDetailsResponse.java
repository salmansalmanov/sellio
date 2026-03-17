package com.sellio.model.dto.response.core;

import com.sellio.model.enums.PricingPlan;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ShopDetailsResponse extends UserResponse {
    private UUID id;
    private String name;
    private String email;
    private String description;
    private PricingPlan pricingPlan;
    private Set<String> phoneNumbers;
    private String logoUrl;
    private String bannerUrl;

    @Builder.Default
    private List<String> addresses = new ArrayList<>();

    @Builder.Default
    private Long viewCount = 0L;
}
