package com.sellio.model.dto.response.core;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyDependencyDetailsResponse {
    private UUID parentPropertyValueId;
    private String parentPropertyValue;
    private List<PropertyValueResponse> childPropertyValues;
}
