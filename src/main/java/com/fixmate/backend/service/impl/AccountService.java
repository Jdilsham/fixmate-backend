package com.fixmate.backend.service.impl;

import com.fixmate.backend.dto.request.InitiateEmailChangeRequest;
import com.fixmate.backend.dto.request.VerifyEmailChangeRequest;
import com.fixmate.backend.entity.EmailChangeToken;
import com.fixmate.backend.entity.User;
import com.fixmate.backend.exception.InvalidPasswordException;
import com.fixmate.backend.repository.EmailChangeTokenRepository;
import com.fixmate.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final UserRepository userRepository;
    private final EmailChangeTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private static final int OTP_EXPIRY_MINUTES = 10;

    public void initiateEmailChange(User user, InitiateEmailChangeRequest request) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Invalid password");
        }
        if (userRepository.existsByEmail(request.getNewEmail())) {
            throw new RuntimeException("Email already in use");
        }

        //remove old otps
        tokenRepository.deleteByUser(user);

        String otp = generateOtp();

        EmailChangeToken token = EmailChangeToken.builder()
                .user(user)
                .newEmail(request.getNewEmail())
                .otp(otp)
                .expiresAt(Instant.now().plusSeconds(OTP_EXPIRY_MINUTES * 60))
                .build();

        tokenRepository.save(token);


        emailService.sendEmailChangeOtp(request.getNewEmail(), otp);


    }

    @Transactional
    public void verifyEmailChange(User user, VerifyEmailChangeRequest request) {

        EmailChangeToken token = tokenRepository
                .findByUserAndOtp(user, request.getOtp())
                .orElseThrow(() -> new RuntimeException("Invalid OTP"));

        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("OTP expired");
        }

        String oldEmail = user.getEmail();

        user.setEmail(token.getNewEmail());
        userRepository.save(user);

        tokenRepository.delete(token);

        emailService.sendEmailChangeAlert(oldEmail);
        emailService.sendWelcomeEmail(user.getEmail(), user.getFirstName());
    }

    //otp generation
    private String generateOtp() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }


}
