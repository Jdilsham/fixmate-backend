package com.fixmate.backend.service.impl;

import com.fixmate.backend.service.FileStorageService;
import com.fixmate.backend.service.GcsStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class LocalFileStorageServiceImpl implements FileStorageService {

    private final GcsStorageService gcsStorageService;

    @Override
    public String upload(MultipartFile file, String folder) {
        try {
            return gcsStorageService.uploadFile(file, folder);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("File upload failed: " + e.getMessage(), e);
        }
    }
}