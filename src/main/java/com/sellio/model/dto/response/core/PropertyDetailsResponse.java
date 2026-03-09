package com.sellio.model.dto.response.core;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyDetailsResponse {
    private UUID id;
    private String name;
    private UUID subcategoryId;
    private String subcategoryName;
    private List<PropertyValueResponse> values;
}
