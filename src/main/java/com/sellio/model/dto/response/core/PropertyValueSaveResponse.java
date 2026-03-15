package com.sellio.model.dto.response.core;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyValueSaveResponse {
    private UUID subcategoryId;
    private String subcategoryName;
    private UUID propertyId;
    private String propertyName;
    List<PropertyValueResponse> propertyValues;
}
