package com.fixmate.backend.service.impl;

import com.fixmate.backend.dto.request.ChangePasswordRequest;
import com.fixmate.backend.entity.User;
import com.fixmate.backend.exception.InvalidPasswordException;
import com.fixmate.backend.exception.ResourceNotFoundException;
import com.fixmate.backend.repository.UserRepository;
import com.fixmate.backend.service.UserService;
import com.fixmate.backend.util.FileStorageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FileStorageUtil fileStorageUtil;
    private final PasswordEncoder passwordEncoder;

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

    //change password
    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(() ->  new ResourceNotFoundException("User not found"));

        //verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())){
            throw new InvalidPasswordException("Invalid current password");
        }
        //confirm new password
        if (request.getConfirmationPassword() != null && !request.getNewPassword().equals(request.getConfirmationPassword())){
            throw new InvalidPasswordException("Confirmation password not match");
        }

        //encode and update new password
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);
    }

    // ================= HELPERS =================

    //Ensure user exists
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,"User not found"));
    }

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
