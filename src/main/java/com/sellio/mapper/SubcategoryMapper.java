package com.sellio.mapper;

import com.sellio.model.dto.request.SubcategoryCreateRequest;
import com.sellio.model.dto.request.SubcategoryUpdateRequest;
import com.sellio.model.dto.response.core.SubcategoryDetailsResponse;
import com.sellio.model.dto.response.core.SubcategoryResponse;
import com.sellio.model.entity.SubcategoryEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SubcategoryMapper {
    private final PropertyMapper propertyMapper;

    public SubcategoryEntity createRequestToEntity(SubcategoryCreateRequest request) {
        return SubcategoryEntity.builder()
                .name(request.getName())
                .isTitleRequired(request.getIsTitleRequired())
                .build();
    }

    public SubcategoryDetailsResponse toDetailsResponse(SubcategoryEntity entity) {
        return SubcategoryDetailsResponse.builder()
                .subcategoryId(entity.getId())
                .name(entity.getName())
                .categoryId(entity.getCategory().getId())
                .categoryName(entity.getCategory().getName())
                .properties(entity.getProperties().stream()
                        .map(propertyMapper::toResponse)
                        .toList())
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
