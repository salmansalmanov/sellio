package com.sellio.model.dto.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
    private String placeId;
    private String country;
    private String city;
    private String fullAddress;
    private Double latitude;
    private Double longitude;
}
