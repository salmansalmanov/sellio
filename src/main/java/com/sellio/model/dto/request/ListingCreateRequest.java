package com.sellio.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ListingCreateRequest {

    @NotNull(message = "Owner ID must not be null")
    private UUID ownerId;

    @NotNull(message = "Subcategory must not be null")
    private UUID subcategoryId;
    private List<UUID> propertyValueIds;

    @NotNull(message = "isNew must not be null")
    private Boolean isNew;

    @NotNull(message = "hasDelivery must not be null")
    private Boolean hasDelivery;

    @NotNull(message = "City must not be null")
    private UUID cityId;

    @NotNull(message = "Price must not be null")
    private BigDecimal price;
    private String title;

    @NotBlank(message = "Description must not be blank")
    @Size(min = 10, max = 750)
    private String description;
}
