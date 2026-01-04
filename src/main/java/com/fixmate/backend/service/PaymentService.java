//package com.fixmate.backend.service;
//
//import com.fixmate.backend.repository.PaymentRepository;
//import org.springframework.stereotype.Service;
//
//@Service
//public class PaymentService {
//
//    private final PaymentRepository paymentRepository;
//    private final BookingRepository bookingRepository;
//
//    public PaymentService(PaymentRepository paymentRepository,
//                          BookingRepository bookingRepository) {
//        this.paymentRepository = paymentRepository;
//        this.bookingRepository = bookingRepository;
//    }
//
//    public void requestPayment(PaymentRequestDto dto, User providerUser) {
//
//        // 1️⃣ Get booking
//        Booking booking = bookingRepository.findById(dto.getBookingId())
//                .orElseThrow(() -> new ResponseStatusException(
//                        HttpStatus.NOT_FOUND, "Booking not found"));
//
//        // 2️⃣ Validate provider owns booking
//        ServiceProvider provider = booking.getServiceProvider();
//        if (!provider.getUser().getId().equals(providerUser.getId())) {
//            throw new ResponseStatusException(
//                    HttpStatus.FORBIDDEN, "You cannot request payment for this booking");
//        }
//
//        // 3️⃣ Create payment
//        Payment payment = new Payment();
//        payment.setBooking(booking);
//        payment.setProvider(provider);
//        payment.setCustomer(booking.getCustomer());
//        payment.setAmount(dto.getAmount());
//        payment.setWorkedTime(dto.getWorkedTime());
//        payment.setStatus(PaymentStatus.REQUESTED);
//        payment.setCreatedAt(Instant.now());
//
//        paymentRepository.save(payment);
//
//        // 4️⃣ Update booking status
//        booking.setStatus(BookingStatus.PAYMENT_PENDING);
//        bookingRepository.save(booking);
//
//        // 5️⃣ (Later) send notification + email
//    }
//}
