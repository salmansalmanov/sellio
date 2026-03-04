package com.sellio.event;

import com.sellio.model.enums.ImageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
public class ShopImageUploadEvent {
    private UUID shopId;
    private byte[] fileBytes;
    private ImageType imageType;
}
