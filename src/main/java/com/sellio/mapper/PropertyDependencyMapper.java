package com.sellio.mapper;

import com.sellio.model.dto.response.core.PropertyDependencyDetailsResponse;
import com.sellio.model.dto.response.core.PropertyDependencyResponse;
import com.sellio.model.entity.PropertyDependencyEntity;
import com.sellio.model.entity.PropertyValueEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PropertyDependencyMapper {
    private final PropertyValueMapper propertyValueMapper;

    public PropertyDependencyDetailsResponse toDetailsResponse(List<PropertyDependencyEntity> entities) {
        List<PropertyValueEntity> childPropertyValues = new ArrayList<>();
        for (PropertyDependencyEntity entity : entities) {
            childPropertyValues.add(entity.getChild());
        }
        return PropertyDependencyDetailsResponse.builder()
                .parentPropertyValueId(entities.getFirst().getParent().getId())
                .parentPropertyValue(entities.getFirst().getParent().getValue())
                .childPropertyValues(propertyValueMapper.toResponses(childPropertyValues))
                .build();
    }

    public PropertyDependencyResponse toResponse(PropertyDependencyEntity entity) {
        return PropertyDependencyResponse.builder()
                .propertyDependencyId(entity.getId())
                .parentPropertyValueId(entity.getParent().getId())
                .parentPropertyValue(entity.getParent().getValue())
                .childPropertyValueId(entity.getChild().getId())
                .childPropertyValue(entity.getChild().getValue())
                .build();
    }

    public List<PropertyDependencyResponse> toResponses(List<PropertyDependencyEntity> entities) {
        return entities.stream()
                .map(this::toResponse)
                .toList();
    }
}
