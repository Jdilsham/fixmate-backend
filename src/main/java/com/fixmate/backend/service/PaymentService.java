package com.fixmate.backend.service;

import com.fixmate.backend.dto.request.PaymentRequest;
import com.fixmate.backend.dto.response.CustomerPaymentView;
import com.fixmate.backend.entity.Booking;
import com.fixmate.backend.entity.Payment;
import com.fixmate.backend.entity.ServiceProvider;
import com.fixmate.backend.entity.User;
import com.fixmate.backend.enums.BookingStatus;
import com.fixmate.backend.enums.PaymentMethod;
import com.fixmate.backend.enums.PaymentStatus;
import com.fixmate.backend.repository.BookingRepository;
import com.fixmate.backend.repository.PaymentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Service
public class PaymentService {

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
        payment.setWorkedTime(dto.getWorkedTime());
        payment.setStatus(PaymentStatus.REQUESTED);
        payment.setCreatedAt(Instant.now());

        paymentRepository.save(payment);

        // Update booking status
        booking.setStatus(BookingStatus.PENDING);
        bookingRepository.save(booking);

        //send notification + email
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

        dto.setWorkedTime(payment.getWorkedTime());
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

        // Update payment
        payment.setPaymentMethod(PaymentMethod.CASH);
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(Instant.now());

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

        // Ensure customer already paid
        if (payment.getStatus() != PaymentStatus.PAID) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Payment is not yet paid by customer"
            );
        }

        // Confirm payment
        payment.setStatus(PaymentStatus.CONFIRMED);
        paymentRepository.save(payment);

        // Complete booking
        Booking booking = payment.getBooking();
        booking.setStatus(BookingStatus.COMPLETED);
        bookingRepository.save(booking);

        // send email + notification
    }

}

