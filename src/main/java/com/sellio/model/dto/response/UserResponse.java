package com.sellio.model.dto.response;

import com.sellio.model.enums.AccountStatus;
import com.sellio.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class UserResponse {
    private AccountStatus status;
    private Role role;
}
