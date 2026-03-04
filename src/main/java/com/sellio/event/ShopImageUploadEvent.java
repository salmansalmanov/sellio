package com.sellio.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
public class ShopImageUploadEvent {
    private UUID shopId;
    private byte[] logoBytes;
    private byte[] bannerBytes;
}
