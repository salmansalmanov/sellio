package com.sellio.mapper;

import com.sellio.model.dto.request.SubcategoryCreateRequest;
import com.sellio.model.dto.request.SubcategoryUpdateRequest;
import com.sellio.model.dto.response.core.SubcategoryDetailsResponse;
import com.sellio.model.dto.response.core.SubcategoryResponse;
import com.sellio.model.entity.CategoryEntity;
import com.sellio.model.entity.SubcategoryEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class SubcategoryMapper {
    public SubcategoryEntity createRequestToEntity(SubcategoryCreateRequest request, CategoryEntity categoryEntity) {
        return SubcategoryEntity.builder()
                .name(request.getName())
                .category(categoryEntity)
                .build();
    }

    public SubcategoryDetailsResponse toDetailsResponse(SubcategoryEntity entity) {
        return SubcategoryDetailsResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .categoryId(entity.getCategory().getId())
                .categoryName(entity.getCategory().getName())
                .build();
    }

    public SubcategoryResponse toResponse(SubcategoryEntity entity) {
        return SubcategoryResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    public List<SubcategoryResponse> toResponses(List<SubcategoryEntity> entities) {
        return entities.stream()
                .map(this::toResponse)
                .toList();
    }

    public SubcategoryEntity updateRequestToEntity(SubcategoryUpdateRequest request, SubcategoryEntity subcategoryEntity) {
        if (request.getName() != null) {
            subcategoryEntity.setName(request.getName());
        }
        return subcategoryEntity;
    }
}
