package com.sellio.model.dto.response.core;

import com.sellio.model.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminResponse {
    private UUID id;
    private String fullName;
    private String username;
    private UserStatus status;
}
