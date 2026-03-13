package com.fixmate.backend.controller;

import com.fixmate.backend.entity.Booking;
import com.fixmate.backend.entity.Payment;
import com.fixmate.backend.enums.BookingStatus;
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

        System.out.println("🔥 PAYHERE WEBHOOK RECEIVED");
        System.out.println("Order ID = " + orderId);
        System.out.println("Status Code = " + statusCode);

        Payment payment = paymentRepository.findByTransactionRef(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if ("2".equals(statusCode)) {
            // ✅ SUCCESS
            payment.setStatus(PaymentStatus.PAID);
            payment.setPaidAt(Instant.now());
            paymentRepository.save(payment);
            System.out.println("✅ Payment SUCCESS → PAID");

        } else {
            // ❌ FAILED / DECLINED / CANCELLED
            payment.setStatus(PaymentStatus.REQUESTED);
            paymentRepository.save(payment);
            System.out.println("❌ Payment FAILED → reverted to REQUESTED");
        }

        return ResponseEntity.ok("OK");
    }
}
