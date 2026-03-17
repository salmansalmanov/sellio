package com.sellio.model.dto.response.core;

import com.sellio.model.enums.Role;
import com.sellio.model.enums.UserStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDetailsResponse extends UserResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private UserStatus status;
    private Role role;
}
