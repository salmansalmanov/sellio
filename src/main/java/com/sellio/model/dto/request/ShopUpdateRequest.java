package com.sellio.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShopUpdateRequest {
    private String name;
    private String description;
    private Set<String> phoneNumbers;
    private Set<String> placeIds;
}
