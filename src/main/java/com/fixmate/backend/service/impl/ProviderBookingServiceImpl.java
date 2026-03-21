package com.fixmate.backend.service.impl;

import com.fixmate.backend.dto.request.FinalizeBookingRequest;
import com.fixmate.backend.dto.response.ProviderBookingResponse;
import com.fixmate.backend.entity.Booking;
import com.fixmate.backend.entity.Payment;
import com.fixmate.backend.enums.BookingStatus;
import com.fixmate.backend.enums.PricingType;
import com.fixmate.backend.repository.BookingRepository;
import com.fixmate.backend.service.ProviderBookingService;
import com.fixmate.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProviderBookingServiceImpl implements ProviderBookingService {

    private final BookingRepository bookingRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;

    @Override
    public List<Booking> getProviderBookings(Long serviceProviderId){
        return bookingRepository.findByProviderService_ServiceProvider_ServiceProviderId(serviceProviderId);
    }

    @Override
    public void confirmBooking(Long bookingId, Long serviceProviderId, Long providerServiceId) {
        Booking booking = bookingRepository.findProviderBookingForConfirm(bookingId, serviceProviderId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Booking not found."
                ));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Only pending bookings can be confirmed."
            );

        }

        booking.setStatus(BookingStatus.ACCEPTED);

        // Email notification
        emailService.sendBookingAcceptedEmail(
                booking.getUser().getEmail(),
                booking.getUser().getFirstName(),
                booking.getProviderService().getService().getTitle(),
                booking.getBookingId().toString()
        );

        notificationService.notifyCustomer(
                booking.getUser(),
                "Your booking has been CONFIRMED by the service provider."
        );
    }

    @Override
    public void rejectBooking(
            Long bookingId,
            Long serviceProviderId,
            Long providerServiceId,
            String reason
    ) {
        if (reason == null || reason.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Please provide a reason."
            );
        }

        Booking booking = bookingRepository.findProviderBookingById(
                bookingId, providerServiceId, serviceProviderId
        ).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Booking not found."
        ));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Only pending bookings can be rejected."
            );
        }

        booking.setStatus(BookingStatus.REJECTED);
        booking.setRejectionReason(reason);
        booking.setRejectedAt(LocalDateTime.now());

        notificationService.notifyCustomer(
                booking.getUser(),
                "Your booking has been rejected. Reason: " + reason
        );

        // Email notification
        emailService.sendBookingRejectedEmail(
                booking.getUser().getEmail(),
                booking.getUser().getFirstName(),
                booking.getProviderService().getService().getTitle(),
                reason
        );
    }


    @Override
    public void startJob(Long bookingId, Long serviceProviderId, Long providerServiceId) {

        Booking booking = bookingRepository.findProviderBookingById(
                bookingId, providerServiceId, serviceProviderId
        ).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Booking not found"
        ));

        if (booking.getStatus() != BookingStatus.ACCEPTED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only accepted bookings can be started"
            );
        }

        booking.setStatus(BookingStatus.IN_PROGRESS);
        booking.setStartedAt(Instant.now());

        notificationService.notifyCustomer(
                booking.getUser(),
                "Service provider has started your job"
        );
    }

    @Override
    public void finalizeBooking(
            Long bookingId,
            Long serviceProviderId,
            Long providerServiceId,
            FinalizeBookingRequest request
    ) {

        Booking booking = bookingRepository.findProviderBookingById(
                bookingId, providerServiceId, serviceProviderId
        ).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Booking not found"
        ));

        if (booking.getStatus() != BookingStatus.IN_PROGRESS) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only in-progress bookings can be finalized"
            );
        }

        BigDecimal finalAmount;

        if (booking.getPricingType() == PricingType.FIXED) {

            if (request.getFinalAmount() == null ||
                    request.getFinalAmount().compareTo(BigDecimal.ZERO) <= 0) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Final amount is required for FIXED pricing"
                );
            }

            finalAmount = request.getFinalAmount();

        } else { // HOURLY

            if (request.getHourlyRate() == null ||
                    request.getWorkedSeconds() == null ||
                    request.getWorkedSeconds() <= 0) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Hourly rate and worked time are required"
                );
            }

            finalAmount = request.getHourlyRate()
                    .multiply(BigDecimal.valueOf(request.getWorkedSeconds()))
                    .divide(BigDecimal.valueOf(3600), 2, RoundingMode.HALF_UP);
        }

        booking.setTotalPrice(finalAmount);

        // DO NOT mark completed yet
        notificationService.notifyCustomer(
                booking.getUser(),
                "Job completed. Please proceed with payment."
        );
    }

    @Override
    public void markAsPaid(Long bookingId, String customerEmail) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Booking not found"
                ));

        // Ensure correct customer
        if (!booking.getUser().getEmail().equals(customerEmail)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Not your booking"
            );
        }

        if (booking.getStatus() != BookingStatus.IN_PROGRESS) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Job must be finalized by provider before payment"
            );
        }

        if (booking.getTotalPrice() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Provider has not finalized the amount yet"
            );
        }

        // Complete booking
        booking.setStatus(BookingStatus.COMPLETED);

        // Email notification
        emailService.sendServiceCompletedEmail(
                booking.getUser().getEmail(),
                booking.getUser().getFirstName(),
                booking.getProviderService().getService().getTitle(),
                booking.getBookingId().toString()
        );
    }

    @Override
    public List<ProviderBookingResponse> getProviderBookingResponses(Long serviceProviderId) {

        List<Booking> bookings =
                bookingRepository.findByProviderService_ServiceProvider_ServiceProviderId(serviceProviderId);

        return bookings.stream().map(booking -> {

            ProviderBookingResponse dto = new ProviderBookingResponse();
            Payment payment = booking.getPayment();

            dto.setBookingId(booking.getBookingId());
            dto.setStatus(booking.getStatus());
            dto.setDescription(booking.getDescription());
            dto.setPaymentAmount(booking.getTotalPrice());
            dto.setPaymentType(booking.getPricingType().name());
            dto.setHourlyRate(
                    booking.getProviderService() != null
                            ? booking.getProviderService().getHourlyRate()
                            : null
            );


            if (booking.getUser() != null) {
                dto.setCustomerName(
                        booking.getUser().getFirstName() + " " + booking.getUser().getLastName()
                );
                dto.setCustomerPhone(booking.getUser().getPhone());
            }

            if (booking.getContactInfo() != null) {
                dto.setBookingAddress(booking.getContactInfo().getAddress());
                dto.setBookingPhone(booking.getContactInfo().getPhone());
                dto.setLatitude(booking.getContactInfo().getLatitude());
                dto.setLongitude(booking.getContactInfo().getLongitude());
            }

            if (booking.getProviderService() != null &&
                    booking.getProviderService().getService() != null) {

                dto.setServiceTitle(
                        booking.getProviderService().getService().getTitle()
                );
            }
            if (booking.getScheduledAt() != null) {
                dto.setScheduledAt(
                        LocalDateTime.ofInstant(
                                booking.getScheduledAt(),
                                ZoneId.systemDefault()
                        )
                );
            }
            dto.setStartedAt(booking.getStartedAt());

            dto.setProviderServiceId(
                    booking.getProviderService().getId()
            );



            if (payment != null) {
                dto.setPaymentStatus(payment.getStatus().name());
            } else {
                dto.setPaymentStatus(null);
            }

            return dto;

        }).toList();
    }


}
