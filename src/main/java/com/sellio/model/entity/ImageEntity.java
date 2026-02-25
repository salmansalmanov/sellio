package com.sellio.model.entity;

import com.sellio.model.enums.ImageType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "images")
public class ImageEntity extends BaseEntity {
    private String fileName;
    private String publicId;
    private String secureUrl;
    private Long size;
    private String format;

    @Enumerated(EnumType.STRING)
    private ImageType type;
}
