package com.sellio.mapper;

import com.sellio.model.dto.domain.AddressDto;
import com.sellio.model.dto.response.client.GoogleMapsPlaceResponse;
import com.sellio.model.entity.AddressEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(target = "placeId", source = "id")
    @Mapping(target = "fullAddress", source = "formattedAddress")
    @Mapping(target = "latitude", source = "location.latitude")
    @Mapping(target = "longitude", source = "location.longitude")
    @Mapping(target = "country", source = "addressComponents", qualifiedByName = "extractCountry")
    @Mapping(target = "city", source = "addressComponents", qualifiedByName = "extractCity")
    AddressDto toDto(GoogleMapsPlaceResponse googleMapsPlaceResponse);

    @Named("extractCountry")
    default String extractCountry(List<GoogleMapsPlaceResponse.AddressComponent> components) {
        return findComponent(components, "country");
    }

    @Named("extractCity")
    default String extractCity(List<GoogleMapsPlaceResponse.AddressComponent> components) {
        return findComponent(components, "locality");
    }

    default String findComponent(List<GoogleMapsPlaceResponse.AddressComponent> components, String type) {
        if (components == null) return null;
        return components.stream()
                .filter(c -> c.getTypes() != null && c.getTypes().contains(type))
                .findFirst()
                .map(GoogleMapsPlaceResponse.AddressComponent::getLongText)
                .orElse(null);
    }

    AddressEntity toEntity(AddressDto addressDto);
}
