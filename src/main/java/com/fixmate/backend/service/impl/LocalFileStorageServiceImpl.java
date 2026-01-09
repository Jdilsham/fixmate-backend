package com.fixmate.backend.service.impl;

import com.fixmate.backend.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class LocalFileStorageServiceImpl implements FileStorageService {
    private static final String UPLOAD_DIR = "uploads/pdfs/";

    @Override
    public String upload(MultipartFile file) {
        try {
            Files.createDirectories(Path.of(UPLOAD_DIR));

            String filename = UUID.randomUUID() + "-" + file.getOriginalFilename();
            Path path = Path.of(UPLOAD_DIR + filename);

            Files.write(path, file.getBytes());

            return "/files/pdfs/" + filename; // public access URL

        } catch (Exception e) {
            throw new RuntimeException("File upload failed", e);
        }
    }
}
