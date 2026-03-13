package com.fixmate.backend.controller;

import com.fixmate.backend.dto.response.CustomerPaymentView;
import com.fixmate.backend.dto.response.PayHereSandboxResponse;
import com.fixmate.backend.entity.User;
import com.fixmate.backend.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer/payments")
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerPaymentController {

    private final PaymentService paymentService;

    public CustomerPaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<CustomerPaymentView> viewPayment(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal User customerUser) {

        return ResponseEntity.ok(
                paymentService.getPaymentForCustomer(
                        bookingId, customerUser
                )
        );
    }

    @PostMapping("/pay-cash/{paymentId}")
    public ResponseEntity<?> payCash(
            @PathVariable Long paymentId,
            @AuthenticationPrincipal User customerUser) {

        paymentService.payByCash(paymentId, customerUser);
        return ResponseEntity.ok("Payment marked as PAID (Cash)");
    }

    @PostMapping("/pay-here-sandbox/{paymentId}")
    public ResponseEntity<PayHereSandboxResponse> payWithPayHereSandbox(
            @PathVariable Long paymentId,
            @AuthenticationPrincipal User customerUser) {

        return ResponseEntity.ok(
                paymentService.initiatePayHereSandbox(paymentId, customerUser)
        );
    }


}
