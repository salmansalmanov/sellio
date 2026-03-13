package com.sellio.mapper;

import com.sellio.model.dto.request.PropertyValueCreateRequest;
import com.sellio.model.dto.request.PropertyValueUpdateRequest;
import com.sellio.model.dto.response.core.PropertyValueGetResponse;
import com.sellio.model.dto.response.core.PropertyValueResponse;
import com.sellio.model.dto.response.core.PropertyValueSaveResponse;
import com.sellio.model.entity.PropertyValueEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PropertyValueMapper {
    public PropertyValueEntity createRequestToEntity(PropertyValueCreateRequest request) {
        return PropertyValueEntity.builder()
                .value(request.getValue())
                .build();
    }

    public PropertyValueGetResponse toGetResponse(PropertyValueEntity entity) {
        return PropertyValueGetResponse.builder()
                .subcategoryId(entity.getProperty().getSubcategory().getId())
                .subcategoryName(entity.getProperty().getSubcategory().getName())
                .propertyId(entity.getProperty().getId())
                .propertyName(entity.getProperty().getName())
                .propertyValueId(entity.getId())
                .propertyValueName(entity.getValue())
                .build();
    }

    public PropertyValueSaveResponse toSaveResponse(List<PropertyValueEntity> entities) {
        return PropertyValueSaveResponse.builder()
                .subcategoryId(entities.getFirst().getProperty().getSubcategory().getId())
                .subcategoryName(entities.getFirst().getProperty().getSubcategory().getName())
                .propertyId(entities.getFirst().getProperty().getId())
                .propertyName(entities.getFirst().getProperty().getName())
                .propertyValues(this.toResponses(entities))
                .build();
    }

    public PropertyValueResponse toResponse(PropertyValueEntity entity) {
        return PropertyValueResponse.builder()
                .id(entity.getId())
                .value(entity.getValue())
                .build();
    }

    public List<PropertyValueResponse> toResponses(List<PropertyValueEntity> entities) {
        return entities.stream()
                .map(this::toResponse)
                .toList();
    }

    public PropertyValueEntity updateRequestToEntity(PropertyValueUpdateRequest request, PropertyValueEntity entity) {
        if (request.getValue() != null) {
            entity.setValue(request.getValue());
        }
        return entity;
    }
}
