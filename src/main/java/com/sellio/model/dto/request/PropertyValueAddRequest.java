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
public class PropertyValueAddRequest {

    @NotNull(message = "Property ID must not be null")
    private UUID propertyId;

    @NotNull(message = "Values must not be null")
    private List<String> values;
}
