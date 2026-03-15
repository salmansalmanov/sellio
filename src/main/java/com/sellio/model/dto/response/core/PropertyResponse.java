package com.sellio.model.dto.response.core;

import lombok.*;

import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyResponse {
    private UUID id;
    private String name;
}
