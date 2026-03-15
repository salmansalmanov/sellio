package com.sellio.event;

import com.sellio.model.enums.DomainType;
import com.sellio.model.enums.ImageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
public class ImageUploadEvent {
    private UUID referenceId;
    private byte[] fileBytes;
    private ImageType imageType;
    private DomainType domainType;
}
