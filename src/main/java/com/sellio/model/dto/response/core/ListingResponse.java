package com.sellio.model.dto.response.core;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListingResponse {
    private UUID id;
    private String thumbnailUrl;
    private BigDecimal price;
    private String title;
    private CityResponse city;
    private LocalDateTime updatedAt;
}
