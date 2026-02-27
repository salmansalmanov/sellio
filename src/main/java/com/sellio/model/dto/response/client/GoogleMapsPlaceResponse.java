package com.sellio.model.dto.response.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleMapsPlaceResponse {
    private String id;
    private String formattedAddress;
    private Location location;
    private List<AddressComponent> addressComponents;

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Location {
        private Double latitude;
        private Double longitude;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AddressComponent {
        private String longText;
        private List<String> types;
    }
}
