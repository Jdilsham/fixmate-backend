package com.fixmate.backend.service.impl;

import com.fixmate.backend.entity.User;
import com.fixmate.backend.repository.UserRepository;
import com.fixmate.backend.service.UserService;
import com.fixmate.backend.util.FileStorageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FileStorageUtil fileStorageUtil;

    @Override
    public String uploadProfileImage(MultipartFile file) {
        User user = getCurrentUser();

        String imageUrl = fileStorageUtil.storeProfileImage(
                file,
                user.getId()
        );

        user.setProfilePic(imageUrl);
        userRepository.save(user);

        return imageUrl;
    }

    // ================= HELPERS =================

    private User getCurrentUser() {
        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (!(principal instanceof User user)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Unauthorized"
            );
        }

        return user;
    }
}
