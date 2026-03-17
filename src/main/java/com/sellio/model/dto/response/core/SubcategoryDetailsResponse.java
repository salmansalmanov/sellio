package com.sellio.model.dto.response.core;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubcategoryDetailsResponse {
    private UUID subcategoryId;
    private String name;
    private UUID categoryId;
    private String categoryName;
    private List<PropertyResponse> properties;
}
