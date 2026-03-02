package com.sellio.model.dto.response.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShopResponse {
    private UUID id;
    private String name;
    private String logoUrl;
    private String description;
    private List<String> phoneNumbers;
    private Long viewCount = 0L;
}
