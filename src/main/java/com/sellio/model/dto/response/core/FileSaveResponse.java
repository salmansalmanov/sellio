package com.sellio.model.dto.response.core;

import com.sellio.model.enums.ImageType;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileSaveResponse {
    private String fileName;
    private String publicId;
    private String secureUrl;
    private Long size;
    private String format;
    private ImageType type;
}
