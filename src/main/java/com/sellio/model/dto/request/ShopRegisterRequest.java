package com.sellio.model.dto.request;

import com.sellio.model.enums.PricingPlan;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShopRegisterRequest extends RegisterRequest {

    @NotBlank(message = "Name must not be blank")
    @Size(min = 3, max = 20, message = "Name must be between 3 and 20")
    private String name;

    @Email
    @NotBlank(message = "Email must not be blank")
    private String email;

    @Size(min = 20, max = 200, message = "")
    @NotBlank(message = "Description must not be blank")
    private String description;

    @NotNull(message = "Pricing plan must not be null")
    private PricingPlan pricingPlan;

    @NotNull
    private List<String> phoneNumbers;
    private List<String> placeIds;
}
