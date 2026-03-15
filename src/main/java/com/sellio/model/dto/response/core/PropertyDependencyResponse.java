package com.sellio.model.dto.response.core;

import lombok.*;

import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyDependencyResponse {
    private UUID propertyDependencyId;
    private UUID parentPropertyValueId;
    private String parentPropertyValue;
    private UUID childPropertyValueId;
    private String childPropertyValue;
}
