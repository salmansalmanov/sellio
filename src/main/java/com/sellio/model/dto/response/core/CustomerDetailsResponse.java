package com.sellio.model.dto.response.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDetailsResponse extends UserResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
}
