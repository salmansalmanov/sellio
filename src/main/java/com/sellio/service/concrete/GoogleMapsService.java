package com.sellio.service.concrete;

import com.sellio.exception.custom.GoogleMapsException;
import com.sellio.mapper.AddressMapper;
import com.sellio.model.dto.domain.AddressDto;
import com.sellio.model.dto.response.client.GoogleMapsPlaceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class GoogleMapsService {
    private final WebClient webClient;
    private final AddressMapper addressMapper;

    @Value("${spring.google.maps.api-key}")
    private String apiKey;

    private static final String BASE_URL = "https://places.googleapis.com/v1/places/";

    public AddressDto getAddressByPlaceId(String placeId) {
        try {
            String fieldMask = "id,formattedAddress,location,addressComponents";

            GoogleMapsPlaceResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/{placeId}")
                            .queryParam("key", apiKey)
                            .queryParam("fields", "id,formattedAddress,location,addressComponents")
                            .build(placeId))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(GoogleMapsPlaceResponse.class)
                    .block();

            if (response == null) {
                throw new GoogleMapsException("Google Maps response is null");
            }

            return addressMapper.toDto(response);
        } catch (Exception e) {
            throw new GoogleMapsException("Google Maps exception: " + e.getMessage());
        }
    }
}
