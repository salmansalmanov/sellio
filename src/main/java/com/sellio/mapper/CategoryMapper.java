package com.sellio.mapper;

import com.sellio.model.dto.request.CategoryCreateRequest;
import com.sellio.model.dto.request.CategoryUpdateRequest;
import com.sellio.model.dto.response.core.CategoryDetailsResponse;
import com.sellio.model.dto.response.core.CategoryResponse;
import com.sellio.model.entity.CategoryEntity;
import com.sellio.model.entity.SubcategoryEntity;
import com.sellio.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryMapper {
    private final SubcategoryMapper subcategoryMapper;

    public CategoryEntity createRequestToEntity(CategoryCreateRequest request) {
        return CategoryEntity.builder()
                .name(request.getName())
                .build();
    }

    public CategoryDetailsResponse toDetailsResponse(CategoryEntity entity) {
        return CategoryDetailsResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .subcategories(subcategoryMapper.toResponses(entity.getSubcategories()))
                .build();
    }

    public CategoryResponse toResponse(CategoryEntity entity) {
        return CategoryResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    public List<CategoryResponse> toResponses(List<CategoryEntity> entities) {
        return entities.stream()
                .map(this::toResponse)
                .toList();
    }

    public CategoryEntity updateRequestToEntity(CategoryUpdateRequest request, CategoryEntity entity) {
        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        return entity;
    }
}
