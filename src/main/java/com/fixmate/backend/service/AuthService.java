package com.fixmate.backend.service;

import com.fixmate.backend.config.JwtUtil;
import com.fixmate.backend.dto.request.LoginRequest;
import com.fixmate.backend.dto.request.SignupRequest;
import com.fixmate.backend.enums.Role;
import com.fixmate.backend.entity.ServiceProvider;
import com.fixmate.backend.entity.User;
import com.fixmate.backend.repository.ServiceProviderRepository;
import com.fixmate.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;
    private final ServiceProviderRepository serviceProviderRepository;

    public AuthService(
            UserRepository repo,
            PasswordEncoder encoder,
            JwtUtil jwt,
            AuthenticationManager am,
            ServiceProviderRepository serviceProviderRepository
    ) {
        this.repo = repo;
        this.encoder = encoder;
        this.jwtUtil = jwt;
        this.authManager = am;
        this.serviceProviderRepository = serviceProviderRepository;
    }


    public void signup(SignupRequest r){

        if (repo.existsByEmail(r.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        User user = new User(
                r.getFirstName(),
                r.getLastName(),
                r.getEmail(),
                r.getPhone(),
                encoder.encode(r.getPassword()),
                r.getRole()
        );

        // 1️⃣ Save user FIRST
        User savedUser = repo.save(user);

        // 2️⃣ If SERVICE_PROVIDER → create service_provider row
        if (savedUser.getRole() == Role.SERVICE_PROVIDER) {

            ServiceProvider sp = new ServiceProvider();
            sp.setUser(savedUser);
            sp.setIsVerified(false);   // must be approved by admin
            sp.setIsAvailable(false);  // cannot work yet

            serviceProviderRepository.save(sp);
        }
    }


    public String login(LoginRequest r){

        repo.findByEmail(r.getEmail()).ifPresent(user -> {
            if (user.isBanned()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Your account has been banned.");
            }
        });


        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        r.getEmail(),
                        r.getPassword()
                )
        );

        User user = repo.findByEmail(r.getEmail())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found")
                );

        return jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

    }
}
