package com.fixmate.backend.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Component
public class FileStorageUtil {
    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/jpg", "image/png", "image/webp", "image/gif");

    @Value("${app.upload.profile-pics-dir}")
    private String uploadDir;

    @Value("${app.upload.max-file-size}")
    private long maxFileSize;

    public String storeProfileImage(MultipartFile file, Long userId){
        validate(file);

        try {
            Files.createDirectories(Paths.get(uploadDir));

            String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String fileName = userId + "_" + UUID.randomUUID() + "." + extension;

            Path targetPath = Paths.get(uploadDir).resolve(fileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // Public URL (served via static resources)
            return "/files/profile-pics/" + fileName;
        }catch (IOException e) {
            throw new ResponseStatusException(
                    BAD_REQUEST,
                    "Failed to store profile image"
            );
        }
    }

    private void validate(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "File is empty");
        }

        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid image type");
        }

        if (file.getSize() > maxFileSize) {
            throw new ResponseStatusException(BAD_REQUEST, "File size exceeds limit");
        }
    }
}
