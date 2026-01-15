package com.fixmate.backend.service;

import com.fixmate.backend.dto.request.ChangePasswordRequest;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    String uploadProfileImage(MultipartFile file);

    void changePassword(Long userId, ChangePasswordRequest request);
}

