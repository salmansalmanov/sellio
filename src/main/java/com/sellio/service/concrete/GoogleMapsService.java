package com.sellio.service.concrete;

import com.sellio.exception.custom.GoogleMapsException;
import com.sellio.mapper.AddressMapper;
import com.sellio.model.dto.response.client.GoogleMapsPlaceResponse;
import com.sellio.model.dto.response.core.AddressResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleMapsService {
    private final WebClient webClient;
    private final AddressMapper addressMapper;

    @Value("${spring.google.maps.api-key}")
    private String apiKey;

    public AddressResponse getAddressByPlaceId(String placeId) {
        log.info("ActionLog.loadAddressByPlaceId.start: {}", placeId);
        try {
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
            log.info("ActionLog.loadAddressByPlaceId.end: {}", placeId);
            return addressMapper.toResponse(response);
        } catch (Exception e) {
            log.error("ActionLog.loadAddressByPlaceId.exception: {}", e.getMessage());
            throw new GoogleMapsException("Google Maps exception: " + e.getMessage());
        }
    }
}
