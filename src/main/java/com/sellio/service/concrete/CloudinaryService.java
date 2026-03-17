package com.sellio.service.concrete;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.sellio.exception.custom.CloudinaryException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
                    ObjectUtils.asMap("resource_type", "image"));
            cloudinary.api().deleteFolder(folder, ObjectUtils.emptyMap());
        } catch (Exception e) {
            throw new CloudinaryException("Cloudinary exception: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> getAllPublicIdsInFolder(String folder) {
        List<String> publicIds = new ArrayList<>();
        try {
            var result = cloudinary.api().resources(ObjectUtils.asMap(
                    "type", "upload",
                    "prefix", folder + "/",
                    "max_results", 500
            ));
            List<Map<String, Object>> resources = (List<Map<String, Object>>) result.get("resources");
            for (Map<String, Object> resource : resources) {
                publicIds.add((String) resource.get("public_id"));
            }
        } catch (Exception e) {
            throw new CloudinaryException("Cloudinary exception: " + e.getMessage());
        }
        return publicIds;
    }

    public void deleteImage(String publicId) {
        try {
            var result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            if ("ok".equals(result.get("result"))) {
                log.info("Image deleted successfully");
            } else {
                log.warn("Image delete failed");
            }
        } catch (Exception e) {
            throw new CloudinaryException("Cloudinary exception: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> getSubfolderNames(String rootFolder) {
        List<String> subfolderNames = new ArrayList<>();
        try {
            var result = cloudinary.api().subFolders(rootFolder, ObjectUtils.asMap(
                    "max_results", 500
            ));
            List<Map<String, Object>> folders = (List<Map<String, Object>>) result.get("folders");

            if (folders != null) {
                for (Map<String, Object> folder : folders) {
                    subfolderNames.add((String) folder.get("name"));
                }
            }
            log.info("{} Subfolders found from {}", subfolderNames.size(), rootFolder);
        } catch (Exception e) {
            throw new CloudinaryException("Cloudinary exception: " + e.getMessage());
        }
        return subfolderNames;
    }

    public void deleteFolderOnlyIfEmpty(String folder) {
        try {
            cloudinary.api().deleteFolder(folder, ObjectUtils.emptyMap());
        } catch (Exception e) {
            log.warn("Folder is not empty");
        }
    }
}
