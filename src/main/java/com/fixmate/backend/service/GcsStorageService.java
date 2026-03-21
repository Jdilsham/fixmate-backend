package com.fixmate.backend.service;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;
import com.google.cloud.storage.HttpMethod;
import java.util.concurrent.TimeUnit;

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

    public String generateSignedUrl(String objectPath) {
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectPath).build();

        URL signedUrl = storage.signUrl(
                blobInfo,
                15,
                TimeUnit.MINUTES,
                Storage.SignUrlOption.httpMethod(HttpMethod.GET),
                Storage.SignUrlOption.withV4Signature()
        );

        return signedUrl.toString();
    }

    public String generateSignedUrlFromPublicUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return fileUrl;
        }

        String prefix = "https://storage.googleapis.com/" + bucketName + "/";
        if (!fileUrl.startsWith(prefix)) {
            return fileUrl;
        }

        String objectPath = fileUrl.substring(prefix.length());
        return generateSignedUrl(objectPath);
    }
}