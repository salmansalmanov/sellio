package com.sellio.mapper;

import com.sellio.model.dto.response.client.GoogleMapsPlaceResponse;
import com.sellio.model.dto.response.core.AddressResponse;
import com.sellio.model.entity.AddressEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AddressMapper {
    public AddressResponse toResponse(GoogleMapsPlaceResponse googleMapsPlaceResponse) {
        return AddressResponse.builder()
                .placeId(googleMapsPlaceResponse.getId())
                .country(findComponent(googleMapsPlaceResponse.getAddressComponents(), "country"))
                .city(findComponent(googleMapsPlaceResponse.getAddressComponents(), "locality"))
                .fullAddress(googleMapsPlaceResponse.getFormattedAddress())
                .latitude(googleMapsPlaceResponse.getLocation().getLatitude())
                .longitude(googleMapsPlaceResponse.getLocation().getLongitude())
                .build();
    }

    private String findComponent(List<GoogleMapsPlaceResponse.AddressComponent> components, String type) {
        if (components == null) {
            return null;
        }
        return components.stream()
                .filter(component -> component.getTypes() != null && component.getTypes().contains(type))
                .findFirst()
                .map(GoogleMapsPlaceResponse.AddressComponent::getLongText)
                .orElse(null);
    }

    public AddressEntity toEntity(AddressResponse response) {
        return AddressEntity.builder()
                .placeId(response.getPlaceId())
                .country(response.getCountry())
                .city(response.getCity())
                .fullAddress(response.getFullAddress())
                .latitude(response.getLatitude())
                .longitude(response.getLongitude())
                .build();
    }
}
