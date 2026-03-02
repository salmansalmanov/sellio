package com.sellio.model.dto.response.core;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponse {
    private String publicId;
    private String secureUrl;
    private String format;
    private Number bytes;
}
