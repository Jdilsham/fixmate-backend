package com.fixmate.backend.controller;

import com.fixmate.backend.entity.Booking;
import com.fixmate.backend.entity.Payment;
import com.fixmate.backend.enums.BookingStatus;
import com.fixmate.backend.enums.PaymentStatus;
import com.fixmate.backend.repository.BookingRepository;
import com.fixmate.backend.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/payments/webhook")
@RequiredArgsConstructor
public class PayHereSandboxWebhookController {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    @PostMapping("/payhere-sandbox")
    @Transactional
    public ResponseEntity<String> handlePayHereSandboxWebhook(
            @RequestParam("order_id") String orderId,
            @RequestParam("status_code") String statusCode
    ) {

        Payment payment = paymentRepository.findByTransactionRef(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if ("2".equals(statusCode)) {

            payment.setStatus(PaymentStatus.CONFIRMED);
            payment.setPaidAt(Instant.now());

            Booking booking = payment.getBooking();
            booking.setStatus(BookingStatus.COMPLETED);

            paymentRepository.save(payment);
            bookingRepository.save(booking);

        } else {
            payment.setStatus(PaymentStatus.REQUESTED);
            paymentRepository.save(payment);
        }

        return ResponseEntity.ok("OK");
    }
}
