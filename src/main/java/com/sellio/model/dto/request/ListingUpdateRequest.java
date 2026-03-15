package com.sellio.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ListingUpdateRequest {
    private Boolean hasDelivery;
    private BigDecimal price;
    private String description;
}
