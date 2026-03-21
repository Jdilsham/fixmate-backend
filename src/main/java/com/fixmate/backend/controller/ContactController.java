package com.fixmate.backend.controller;

import com.fixmate.backend.dto.request.ContactRequest;
import com.fixmate.backend.service.impl.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contact")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // adjust for production
public class ContactController {

    private final EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendContactMessage(@Valid @RequestBody ContactRequest request) {

        emailService.sendContactInquiryEmail(request);

        return ResponseEntity.ok("Your message has been sent successfully.");
    }
}