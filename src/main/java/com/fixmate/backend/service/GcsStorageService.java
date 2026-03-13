package com.fixmate.backend.service;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GcsStorageService {

    private final Storage storage;

    @Value("${gcp.bucket.name}")
    private String bucketName;

    public String uploadFile(MultipartFile file, String folder) throws IOException {
        String originalName = file.getOriginalFilename();
        String safeName = originalName != null ? originalName.replaceAll("\\s+", "_") : "file";
        String fileName = folder + "/" + UUID.randomUUID() + "_" + safeName;

        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, fileName)
                .setContentType(file.getContentType())
                .build();

        storage.create(blobInfo, file.getBytes());

        return String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
    }
}