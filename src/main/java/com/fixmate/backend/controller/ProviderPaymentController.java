package com.fixmate.backend.controller;

import com.fixmate.backend.dto.request.PaymentRequest;
import com.fixmate.backend.entity.User;
import com.fixmate.backend.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/provider/payments")
@PreAuthorize("hasRole('SERVICE_PROVIDER')")
public class ProviderPaymentController {

    private final PaymentService paymentService;

    public ProviderPaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/request")
    public ResponseEntity<?> requestPayment(
            @RequestBody PaymentRequest request,
            @AuthenticationPrincipal User providerUser) {

        paymentService.requestPayment(request, providerUser);
        return ResponseEntity.ok("Payment request created");
    }

    @PostMapping("/confirm/{paymentId}")
    public ResponseEntity<?> confirmPayment(
            @PathVariable Long paymentId,
            @AuthenticationPrincipal User providerUser) {

        paymentService.confirmPayment(paymentId, providerUser);
        return ResponseEntity.ok("Payment confirmed successfully");
    }
}
