package com.sellio.mapper;

import com.sellio.model.dto.request.PropertyCreateRequest;
import com.sellio.model.dto.request.PropertyUpdateRequest;
import com.sellio.model.dto.response.core.PropertyDetailsResponse;
import com.sellio.model.dto.response.core.PropertyResponse;
import com.sellio.model.entity.PropertyEntity;
import com.sellio.model.entity.PropertyValueEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PropertyMapper {
    private final PropertyValueMapper propertyValueMapper;

    public PropertyEntity createRequestToEntity(PropertyCreateRequest request) {
        PropertyEntity propertyEntity = PropertyEntity.builder()
                .name(request.getName())
                .build();

        if (request.getValues() != null) {
            propertyEntity.setValues(request.getValues().stream()
                    .map(propertyValueCreateRequest -> {
                        PropertyValueEntity propertyValueEntity = propertyValueMapper.createRequestToEntity(propertyValueCreateRequest);
                        propertyValueEntity.setProperty(propertyEntity);
                        return propertyValueEntity;
                    })
                    .toList());
        }
        return propertyEntity;
    }

    public PropertyDetailsResponse toDetailsResponse(PropertyEntity entity) {
        return PropertyDetailsResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .subcategoryId(entity.getSubcategory().getId())
                .subcategoryName(entity.getSubcategory().getName())
                .values(propertyValueMapper.toResponses(entity.getValues()))
                .build();
    }

    public PropertyResponse toResponse(PropertyEntity entity) {
        return PropertyResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    public List<PropertyResponse> toResponses(List<PropertyEntity> entities) {
        return entities.stream()
                .map(this::toResponse)
                .toList();
    }

    public PropertyEntity updateRequestToEntity(PropertyUpdateRequest request, PropertyEntity entity) {
        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        return entity;
    }
}
