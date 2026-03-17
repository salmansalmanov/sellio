package com.sellio.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerUpdateRequest {
    private String firstName;
    private String lastName;
    private String phoneNumber;
}
