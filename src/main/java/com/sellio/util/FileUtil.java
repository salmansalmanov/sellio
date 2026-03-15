package com.sellio.util;

import com.sellio.exception.custom.InvalidInputException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
public class FileUtil {
    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png");
    private static final long MAX_SIZE = 5 * 1024 * 1024;

    public void validateImage(MultipartFile file) {
        if (file == null) {
            throw new InvalidInputException("File is null");
        }
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new InvalidInputException("File name is null");
        }
        String extension = getExtension(fileName);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new InvalidInputException("Invalid file extension");
        }

        if (file.getSize() > MAX_SIZE) {
            throw new InvalidInputException("Invalid file size");
        }
    }

    private String getExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index > 0 && index < fileName.length() - 1) {
            return fileName.substring(index + 1);
        }
        return "";
    }
}
