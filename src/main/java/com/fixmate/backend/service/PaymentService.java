package com.fixmate.backend.service;

import com.fixmate.backend.dto.request.PaymentRequest;
import com.fixmate.backend.dto.response.CustomerPaymentView;
import com.fixmate.backend.dto.response.PayHereSandboxResponse;
import com.fixmate.backend.entity.*;
import com.fixmate.backend.enums.BookingStatus;
import com.fixmate.backend.enums.PaymentMethod;
import com.fixmate.backend.enums.PaymentStatus;
import com.fixmate.backend.repository.BookingRepository;
import com.fixmate.backend.repository.PaymentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Value;
import java.security.MessageDigest;


import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.apache.catalina.manager.StatusTransformer.formatSeconds;

@Service
public class PaymentService {

    @Value("${payhere.merchant-id}")
    private String merchantId;

    @Value("${payhere.merchant-secret}")
    private String merchantSecret;


    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    public PaymentService(PaymentRepository paymentRepository,
                          BookingRepository bookingRepository) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
    }

    public void requestPayment(PaymentRequest dto, User providerUser) {

        // Get booking
        Booking booking = bookingRepository.findById(dto.getBookingId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Booking not found"));

        // 2️⃣ Validate provider owns booking
        ServiceProvider provider = booking.getServiceProvider();
        if (!provider.getUser().getId().equals(providerUser.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "You cannot request payment for this booking");
        }

        // reate payment
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setProvider(provider);
        payment.setCustomer(booking.getUser());
        payment.setAmount(dto.getAmount());
        payment.setWorkedSeconds(dto.getWorkedSeconds());
        payment.setStatus(PaymentStatus.REQUESTED);
        payment.setCreatedAt(Instant.now());

        paymentRepository.save(payment);

        booking.setPayment(payment);

        // Update booking status
        booking.setStatus(BookingStatus.PAYMENT_PENDING);
        bookingRepository.save(booking);

        //send notification + email
    }

    public String initiateCardPayment(Long paymentId, User customerUser) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Payment not found"
                ));

        // Ownership check
        if (!payment.getCustomer().getId().equals(customerUser.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "You cannot pay this payment"
            );
        }

        // State check
        if (payment.getStatus() != PaymentStatus.REQUESTED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Payment is not in REQUESTED state"
            );
        }

        // Mark as CARD payment
        payment.setPaymentMethod(PaymentMethod.CARD);
        payment.setStatus(PaymentStatus.PROCESSING);

        // Sandbox transaction reference
        String sandboxRef = "SANDBOX_TXN_" + System.currentTimeMillis();
        payment.setTransactionRef(sandboxRef);

        paymentRepository.save(payment);

        // For now we return transaction ref
        return sandboxRef;
    }


    public CustomerPaymentView getPaymentForCustomer(
            Long bookingId,
            User customerUser) {

        Payment payment = paymentRepository
                .findByBooking_BookingId(bookingId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Payment not found"
                        )
                );

        // Ownership check
        if (!payment.getCustomer().getId().equals(customerUser.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot view this payment"
            );
        }

        CustomerPaymentView dto = new CustomerPaymentView();
        dto.setPaymentId(payment.getPaymentId());
        dto.setBookingId(payment.getBooking().getBookingId());

        dto.setProviderName(
                payment.getProvider().getUser().getFirstName()
        );

        dto.setServiceName(
                payment.getBooking().getProviderService().getService().getTitle()
        );

        dto.setWorkedTime(formatSeconds(payment.getWorkedSeconds()));
        dto.setAmount(payment.getAmount());

        dto.setPaymentStatus(payment.getStatus().name());
        dto.setBookingStatus(
                payment.getBooking().getStatus().name()
        );

        return dto;
    }

    public void payByCash(Long paymentId, User customerUser) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Payment not found"
                        )
                );

        // Ensure correct customer
        if (!payment.getCustomer().getId().equals(customerUser.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot pay for this booking"
            );
        }

        if (payment.getStatus() != PaymentStatus.REQUESTED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Payment is not in REQUESTED state"
            );
        }

        payment.setPaymentMethod(PaymentMethod.CASH);
        payment.setStatus(PaymentStatus.CASH_WAITING_CONFIRMATION);

        payment.setPaidAt(null);

        paymentRepository.save(payment);
    }

    public void confirmPayment(Long paymentId, User providerUser) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Payment not found"
                        )
                );

        // Ensure correct provider
        if (!payment.getProvider().getUser().getId().equals(providerUser.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot confirm this payment"
            );
        }

        if (payment.getPaymentMethod() == PaymentMethod.CASH) {
            if (payment.getStatus() != PaymentStatus.CASH_WAITING_CONFIRMATION) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Cash payment is not waiting for confirmation"
                );
            }

            // Provider confirms cash received
            payment.setStatus(PaymentStatus.CONFIRMED);
            payment.setPaidAt(Instant.now());
            paymentRepository.save(payment);

            // Complete booking
            Booking booking = payment.getBooking();
            booking.setStatus(BookingStatus.COMPLETED);
            bookingRepository.save(booking);

            return;
        }
        // Card payment is confirmed by PayHere webhook.
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Card payments are confirmed automatically"
        );
    }

    public PayHereSandboxResponse initiatePayHereSandbox(
            Long paymentId, User customerUser) {

        if (isBlank(customerUser.getFirstName())
                || isBlank(customerUser.getLastName())
                || isBlank(customerUser.getEmail())
                || isBlank(customerUser.getPhone())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Customer profile incomplete for card payment"
            );
        }


        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Payment not found"
                ));

        if (!payment.getCustomer().getId().equals(customerUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        if (payment.getStatus() != PaymentStatus.REQUESTED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Payment is not in REQUESTED state"
            );
        }

        payment.setPaymentMethod(PaymentMethod.CARD);
        payment.setStatus(PaymentStatus.PROCESSING);

        String orderId = "PH_SANDBOX_" + System.currentTimeMillis();

        String amountFormatted = String.format("%.2f", payment.getAmount());

        String secretMd5 = md5(merchantSecret).toUpperCase();

        String hash = md5(
                merchantId +
                        orderId +
                        amountFormatted +
                        "LKR" +
                        secretMd5
        ).toUpperCase();



        payment.setTransactionRef(orderId);
        paymentRepository.save(payment);

        if (customerUser.getPhone() == null || customerUser.getPhone().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Customer phone number is required to pay by card"
            );
        }

        Map<String, String> fields = new HashMap<>();
        fields.put("merchant_id", merchantId);
        fields.put("order_id", orderId);
        fields.put("items", "FixMate Service Payment");
        fields.put("currency", "LKR");
        fields.put("amount", amountFormatted);
        fields.put("first_name", customerUser.getFirstName());
        fields.put("last_name", customerUser.getLastName());
        fields.put("email", customerUser.getEmail());
        fields.put("phone", customerUser.getPhone());
        fields.put("return_url", "http://localhost:5173/payment-success");
        fields.put("cancel_url", "http://localhost:5173/payment-cancel");
        fields.put(
                "notify_url",
                "https://metallic-kayce-nonautonomously.ngrok-free.dev/api/payments/webhook/payhere-sandbox"
        );

        Booking booking = payment.getBooking();
        BookingContactInfo contact = booking.getContactInfo();

        String address = "N/A";
        String city = "Colombo";

        if (contact != null) {
            if (contact.getAddress() != null && !contact.getAddress().isBlank()) {
                address = contact.getAddress();
            }
            if (contact.getCity() != null && !contact.getCity().isBlank()) {
                city = contact.getCity();
            }
        }

        fields.put("address", address);
        fields.put("city", city);
        fields.put("country", "Sri Lanka");
        fields.put("hash", hash);

        PayHereSandboxResponse response = new PayHereSandboxResponse();
        response.setCheckoutUrl("https://sandbox.payhere.lk/pay/checkout");
        response.setFields(fields);

        return response;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5 error", e);
        }
    }

    private String formatSeconds(Long seconds) {
        if (seconds == null || seconds <= 0) {
            return "00:00:00";
        }

        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long remainingSeconds = seconds % 60;

        return String.format(
                "%02d:%02d:%02d",
                hours, minutes, remainingSeconds
        );
    }
}

