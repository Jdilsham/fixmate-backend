package com.fixmate.backend.service;

import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    String uploadProfileImage(MultipartFile file);
}

