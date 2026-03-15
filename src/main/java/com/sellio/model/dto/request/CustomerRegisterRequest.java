package com.sellio.model.dto.request;

import com.sellio.model.enums.PricingPlan;
import com.sellio.validation.annotation.PhoneNumber;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRegisterRequest extends RegisterRequest {

    @NotBlank(message = "First name cannot be blank")
    @Size(min = 3, max = 20, message = "First name must be between 3 and 20")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(min = 3, max = 20, message = "Last name must be between 3 and 20")
    private String lastName;

    @PhoneNumber
    @NotBlank(message = "Phone number must not be blank")
    private String phoneNumber;

    @NotNull(message = "Pricing plan must not be null")
    private PricingPlan pricingPlan;
}
