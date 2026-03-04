package com.sellio.model.dto.response.core;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDetailsResponse extends UserResponse {
    private String firstName;
    private String lastName;
    private String username;
}
