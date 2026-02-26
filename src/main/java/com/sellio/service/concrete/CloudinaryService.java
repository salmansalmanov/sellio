package com.sellio.service.concrete;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.sellio.exception.custom.CloudinaryException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {
    private final Cloudinary cloudinary;

    @SuppressWarnings("unchecked")
    public Map<String, Object> upload(MultipartFile file, String folder) throws IOException {
        return (Map<String, Object>) cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folder,
                        "public_id", file.getOriginalFilename(),
                        "overwrite", true
                ));
    }

    public void forceRemoveFolder(String folder) {
        try {
            cloudinary.api().deleteResourcesByPrefix(folder + "/",
                    ObjectUtils.asMap("resource_type", "image", "type", "upload"));
            cloudinary.api().deleteFolder(folder, ObjectUtils.emptyMap());
        } catch (Exception e) {
            throw new CloudinaryException("Could not delete the folder: " + folder);
        }
    }
}
