package com.sellio.service.concrete;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.sellio.exception.custom.CloudinaryException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryService {
    private final Cloudinary cloudinary;

    @SuppressWarnings("unchecked")
    public Map<String, Object> upload(byte[] fileBytes, String folder, String fileName) {
        if (fileBytes == null) return null;
        try {
            return (Map<String, Object>) cloudinary.uploader().upload(fileBytes,
                    ObjectUtils.asMap(
                            "folder", folder,
                            "public_id", fileName,
                            "overwrite", true
                    ));
        } catch (Exception e) {
            throw new CloudinaryException("Cloudinary exception: " + e.getMessage());
        }
    }

    public void forceRemoveFolder(String folder) {
        try {
            cloudinary.api().deleteResourcesByPrefix(folder + "/",
                    ObjectUtils.asMap("resource_type", "image", "type", "upload"));
            cloudinary.api().deleteFolder(folder, ObjectUtils.emptyMap());
        } catch (Exception ignore) {
        }
    }
}
