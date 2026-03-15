package com.sellio.model.dto.response.core;

import com.sellio.model.enums.ListingStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListingDetailsResponse {
    private String title;
    private List<String> imageUrls;
    private BigDecimal price;
    private ListingStatus status;
    private UserResponse owner;
    private String description;
    private CityResponse city;
    private CategoryResponse category;
    private SubcategoryResponse subcategory;
    private List<PropertyValueResponse> propertyValues;
    private Boolean isNew;
    private Boolean hasDelivery;
    private UUID id;
    private LocalDateTime updatedAt;
    private Long viewCount;
}
