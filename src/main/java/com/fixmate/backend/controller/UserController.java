package com.fixmate.backend.controller;

import com.fixmate.backend.dto.response.UserMeResponse;
import com.fixmate.backend.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/me")
    public UserMeResponse me(@AuthenticationPrincipal User user) {

        return new UserMeResponse(
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name()
        );
    }
}

