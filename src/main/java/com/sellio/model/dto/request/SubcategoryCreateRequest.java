package com.sellio.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubcategoryCreateRequest {

    @NotNull(message = "Category must not be null")
    private UUID categoryId;

    @NotBlank(message = "Name must not be blank")
    private String name;
}
