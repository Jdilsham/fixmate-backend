package com.fixmate.backend.controller;

import com.fixmate.backend.dto.response.UserMeResponse;
import com.fixmate.backend.entity.User;
import com.fixmate.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserMeResponse me(@AuthenticationPrincipal User user) {

        return new UserMeResponse(
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name()
        );
    }

    @PostMapping("/profile/image")
    public ResponseEntity<String> uploadProfileImage(
            @RequestParam MultipartFile file
    ) {
        return ResponseEntity.ok(userService.uploadProfileImage(file));
    }

}

