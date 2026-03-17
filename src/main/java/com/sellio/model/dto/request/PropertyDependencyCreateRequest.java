package com.sellio.model.dto.request;

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
public class PropertyDependencyCreateRequest {

    @NotNull(message = "Parent property ID must not be null")
    private UUID parentPropertyValueId;

    @NotNull(message = "Child property ID must not be null")
    private List<UUID> childPropertyValueIds;
}
