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
    public String upload(MultipartFile file) {
        try {
            return gcsStorageService.uploadFile(file, "pdfs");
        } catch (Exception e) {
            throw new RuntimeException("File upload failed", e);
        }
    }
}