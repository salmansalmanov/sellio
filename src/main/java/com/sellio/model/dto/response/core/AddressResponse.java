package com.sellio.model.dto.response.core;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {
    private String placeId;
    private String country;
    private String city;
    private String fullAddress;
    private Double latitude;
    private Double longitude;
}
