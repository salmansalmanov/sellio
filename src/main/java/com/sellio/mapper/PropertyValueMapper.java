package com.sellio.mapper;

import com.sellio.model.dto.request.PropertyValueAddRequest;
import com.sellio.model.dto.request.PropertyValueCreateRequest;
import com.sellio.model.dto.request.PropertyValueUpdateRequest;
import com.sellio.model.dto.response.core.PropertyValueDetailsResponse;
import com.sellio.model.dto.response.core.PropertyValueResponse;
import com.sellio.model.entity.PropertyValueEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PropertyValueMapper {
    public PropertyValueEntity addRequestToEntity(PropertyValueAddRequest request) {
        return PropertyValueEntity.builder()
                .value(request.getValue())
                .build();
    }

    public PropertyValueEntity createRequestToEntity(PropertyValueCreateRequest request) {
        return PropertyValueEntity.builder()
                .value(request.getValue())
                .build();
    }

    public PropertyValueDetailsResponse toDetailsResponse(PropertyValueEntity entity) {
        return PropertyValueDetailsResponse.builder()
                .subcategoryId(entity.getProperty().getSubcategory().getId())
                .subcategoryName(entity.getProperty().getSubcategory().getName())
                .propertyId(entity.getProperty().getId())
                .propertyName(entity.getProperty().getName())
                .valueId(entity.getId())
                .value(entity.getValue())
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
