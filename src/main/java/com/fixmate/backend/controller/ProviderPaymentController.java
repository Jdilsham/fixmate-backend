package com.fixmate.backend.controller;

import com.fixmate.backend.dto.request.PaymentRequest;
import com.fixmate.backend.entity.User;
import com.fixmate.backend.repository.PaymentRepository;
import com.fixmate.backend.entity.Payment;
import com.fixmate.backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/provider/payments")
@PreAuthorize("hasRole('SERVICE_PROVIDER')")
@RequiredArgsConstructor
public class ProviderPaymentController {

    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;

    @PostMapping("/request")
    public ResponseEntity<?> requestPayment(
            @RequestBody PaymentRequest dto,
            @AuthenticationPrincipal User providerUser
    ) {
        paymentService.requestPayment(dto, providerUser);
        return ResponseEntity.ok("Payment requested successfully");
    }

    @GetMapping("/by-booking/{bookingId}")
    public ResponseEntity<?> getPaymentByBooking(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal User providerUser
    ) {
        Payment payment = paymentRepository.findByBooking_BookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        // provider ownership check
        if (!payment.getProvider().getUser().getId().equals(providerUser.getId())) {
            return ResponseEntity.status(403).body(Map.of("message", "Forbidden"));
        }

        return ResponseEntity.ok(Map.of(
                "paymentId", payment.getPaymentId(),
                "paymentMethod", payment.getPaymentMethod() == null ? null : payment.getPaymentMethod().name(),
                "paymentStatus", payment.getStatus() == null ? null : payment.getStatus().name(),
                "amount", payment.getAmount(),
                "workedSeconds", payment.getWorkedSeconds()
        ));
    }

    @PostMapping("/confirm/{paymentId}")
    public ResponseEntity<?> confirmPayment(
            @PathVariable Long paymentId,
            @AuthenticationPrincipal User providerUser
    ) {
        paymentService.confirmPayment(paymentId, providerUser);
        return ResponseEntity.ok("Payment confirmed successfully");
    }
}