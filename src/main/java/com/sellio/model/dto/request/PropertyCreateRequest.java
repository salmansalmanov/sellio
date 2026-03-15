package com.sellio.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PropertyCreateRequest {

    @NotBlank(message = "Property name must not be blank")
    private String name;

    @NotNull(message = "isRequired must not be null")
    private Boolean isRequired;

    @NotNull(message = "Subcategory ID must not be null")
    private UUID subcategoryId;
    private List<PropertyValueCreateRequest> values;
}
