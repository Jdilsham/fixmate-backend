package com.fixmate.backend.service.impl;

import com.fixmate.backend.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class OtpService {
    private static  final Duration OTP_VALIDITY = Duration.ofMinutes(10);
    private static final Duration RESEND_COOLDOWN = Duration.ofSeconds(60);

    public String generateOtp(User user) {
        String otp = String.valueOf(
                ThreadLocalRandom.current().nextInt(100000, 1_000_000)
        );

        Instant now = Instant.now();

        user.setVerificationCode(otp);
        user.setOtpExpiresAt(now.plus(OTP_VALIDITY));
        user.setLastOtpSentAt(now);

        return otp;
    }

    public void validateOtp(User user, String otp) {

        if (user.getVerificationCode() == null ||
                user.getOtpExpiresAt() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No OTP found. Please request a new one."
            );
        }

        if (Instant.now().isAfter(user.getOtpExpiresAt())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Verification code has expired"
            );
        }

        if (!user.getVerificationCode().equals(otp)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid verification code"
            );
        }
    }

    public void validateResendAllowed(User user) {

        Instant lastSent = user.getLastOtpSentAt();

        if (lastSent != null &&
                Instant.now().isBefore(lastSent.plus(RESEND_COOLDOWN))) {

            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "Please wait before requesting another OTP"
            );
        }
    }

    public void clearOtp(User user) {
        user.setVerificationCode(null);
        user.setOtpExpiresAt(null);
        user.setLastOtpSentAt(null);
    }
}
