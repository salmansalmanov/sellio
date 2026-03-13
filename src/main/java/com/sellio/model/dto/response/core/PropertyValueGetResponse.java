package com.sellio.model.dto.response.core;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyValueGetResponse {
    private UUID subcategoryId;
    private String subcategoryName;
    private UUID propertyId;
    private String propertyName;
    private UUID propertyValueId;
    private String propertyValueName;
}
