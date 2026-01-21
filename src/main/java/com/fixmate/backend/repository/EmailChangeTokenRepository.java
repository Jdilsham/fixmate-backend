package com.fixmate.backend.repository;

import com.fixmate.backend.entity.EmailChangeToken;
import com.fixmate.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailChangeTokenRepository extends JpaRepository<EmailChangeToken,Long> {

    Optional<EmailChangeToken> findByUser(User user);
    Optional<EmailChangeToken> findByUserAndOtp(User user, String otp);
    void deleteByUser(User user);

}
