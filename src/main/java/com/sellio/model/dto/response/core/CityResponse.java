package com.sellio.model.dto.response.core;

import lombok.*;

import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CityResponse {
    private UUID id;
    private String name;
}
