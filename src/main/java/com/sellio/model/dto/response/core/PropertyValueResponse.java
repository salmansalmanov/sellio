package com.sellio.model.dto.response.core;

import lombok.*;

import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyValueResponse {
    private UUID propertyValueId;
    private String propertyValue;
    private UUID propertyId;
    private String property;
}
