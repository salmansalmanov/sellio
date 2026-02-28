package com.sellio.model.dto.response.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDetailsResponse extends UserResponse {
    private String firstName;
    private String lastName;
    private String phoneNumber;
}
