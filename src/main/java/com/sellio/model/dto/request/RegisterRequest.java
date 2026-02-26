package com.sellio.model.dto.request;

import com.sellio.validation.annotation.Password;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class RegisterRequest {

    @Password
    @NotBlank(message = "Password must not be blank")
    private String password;
}
