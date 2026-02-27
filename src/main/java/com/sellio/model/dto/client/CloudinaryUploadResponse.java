package com.sellio.model.dto.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CloudinaryUploadResponse {
    private String publicId;
    private String secureUrl;
    private String format;
    private Number bytes;
}
