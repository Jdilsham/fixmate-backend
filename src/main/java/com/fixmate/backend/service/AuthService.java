package com.fixmate.backend.service;

import com.fixmate.backend.config.JwtUtil;
import com.fixmate.backend.dto.request.LoginRequest;
import com.fixmate.backend.dto.request.SignupRequest;
import com.fixmate.backend.entity.ServiceProvider;
import com.fixmate.backend.entity.User;
import com.fixmate.backend.enums.Role;
import com.fixmate.backend.repository.ServiceProviderRepository;
import com.fixmate.backend.repository.UserRepository;
import com.fixmate.backend.service.impl.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final ServiceProviderRepository serviceProviderRepository;
    private final GoogleAuthService googleAuthService;
    private final EmailService emailService;

    public void signup(SignupRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email already exists"
            );
        }

        String otp = generateOtp();

        User user = new User(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPhone(),
                passwordEncoder.encode(request.getPassword()),
                request.getRole()
        );

        user.setVerificationCode(otp);
        user.setVerified(false);

        User savedUser = userRepository.save(user);

        // Create service provider profile if needed
        if (savedUser.getRole() == Role.SERVICE_PROVIDER) {

            ServiceProvider provider = new ServiceProvider();
            provider.setUser(savedUser);
            provider.setIsVerified(false);     // must be approved by admin
            provider.setIsAvailable(true);   // cannot work yet

            serviceProviderRepository.save(provider);
        }

        emailService.sendVerificationCode(
                savedUser.getEmail(),
                otp
        );
    }

    public String login(LoginRequest request) {

        userRepository.findByEmail(request.getEmail())
                .ifPresent(user -> {
                    if (user.isBanned()) {
                        throw new ResponseStatusException(
                                HttpStatus.FORBIDDEN,
                                "Your account has been banned."
                        );
                    }
                });

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "User not found"
                ));

        if (!user.isVerified()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Please verify your email"
            );
        }

        return jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );
    }


    //verify email and send welcome email
    public void verifyUser(String email, String code) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        if (user.isVerified()) {
            return;
        }

        if (!code.equals(user.getVerificationCode())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid verification code"
            );
        }

        user.setVerified(true);
        user.setVerificationCode(null);

        userRepository.save(user);

        //send welcome email
        emailService.sendWelcomeEmail(user.getEmail(), user.getFirstName());
    }

    //  GOOGLE LOGIN
    public String googleLogin(String idToken) {

        var payload = googleAuthService.verifyToken(idToken);

        String email = payload.getEmail();
        String firstName = (String) payload.get("given_name");
        String lastName = (String) payload.get("family_name");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setFirstName(firstName);
                    newUser.setLastName(lastName);
                    newUser.setPassword("GOOGLE_AUTH");
                    newUser.setRole(Role.CUSTOMER);
                    newUser.setBanned(false);


                    User saved =  userRepository.save(newUser);

                    emailService.sendWelcomeEmail(saved.getEmail(), saved.getFirstName());

                    return saved;
                });

        if (user.isBanned()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Your account has been banned."
            );
        }

        return jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );
    }

    //generate otp
    private String generateOtp() {
        return String.valueOf(
                ThreadLocalRandom.current().nextInt(100000, 1_000_000)
        );
    }
}
