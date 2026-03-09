package com.sellio.model.dto.response.core;

import lombok.*;

import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyValueDetailsResponse {
    private UUID subcategoryId;
    private String subcategoryName;
    private UUID propertyId;
    private String propertyName;
    private UUID valueId;
    private String value;
}
