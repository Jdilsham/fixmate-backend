package com.fixmate.backend.controller;

import com.fixmate.backend.entity.Payment;
import com.fixmate.backend.enums.PaymentStatus;
import com.fixmate.backend.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/payments/webhook")
@RequiredArgsConstructor
public class PayHereSandboxWebhookController {

    private final PaymentRepository paymentRepository;

    @PostMapping("/payhere-sandbox")
    public ResponseEntity<String> handlePayHereSandboxWebhook(
            @RequestParam("order_id") String orderId,
            @RequestParam("status_code") String statusCode
    ) {

        Payment payment = paymentRepository.findByTransactionRef(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        // PayHere: status_code = 2 means SUCCESS
        if ("2".equals(statusCode)
                && payment.getStatus() == PaymentStatus.PROCESSING) {

            payment.setStatus(PaymentStatus.PAID);
            payment.setPaidAt(Instant.now());
            paymentRepository.save(payment);
        }

        return ResponseEntity.ok("OK");
    }
}
