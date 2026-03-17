package com.sellio.model.dto.response.core;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDetailsResponse {
    private UUID categoryId;
    private String name;
    private List<SubcategoryResponse> subcategories;
}
