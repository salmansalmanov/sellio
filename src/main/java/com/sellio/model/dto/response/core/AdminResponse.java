package com.sellio.model.dto.response.core;

import com.sellio.model.enums.UserStatus;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminResponse {
    private UUID id;
    private String fullName;
    private String username;
    private UserStatus status;
}
