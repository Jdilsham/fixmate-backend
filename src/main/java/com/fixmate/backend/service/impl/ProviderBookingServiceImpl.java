package com.fixmate.backend.service.impl;

import com.fixmate.backend.dto.request.FinalizeBookingRequest;
import com.fixmate.backend.dto.response.ProviderBookingResponse;
import com.fixmate.backend.entity.Booking;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProviderBookingServiceImpl implements ProviderBookingService {

    private final BookingRepository bookingRepository;
    private final NotificationService notificationService;

    @Override
    public List<Booking> getProviderBookings(Long serviceProviderId){
        return bookingRepository.findByProviderService_ServiceProvider_ServiceProviderId(serviceProviderId);
    }

    @Override
    public void confirmBooking(Long bookingId, Long serviceProviderId, Long providerServiceId){
        Booking booking = bookingRepository.findProviderBookingById
                        (bookingId, providerServiceId, serviceProviderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking notFound."));

        if(booking.getStatus() != BookingStatus.PENDING){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only pending bookings can be confirmed.");
        }


        booking.setStatus(BookingStatus.ACCEPTED);
        booking.getServiceProvider().setIsAvailable(false);

        notificationService.notifyCustomer(booking.getUser(), "Your booking has been CONFIRMED by the service provider.");
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
                    request.getHoursWorked() == null) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Hourly rate and hours worked are required"
                );
            }

            finalAmount = request.getHourlyRate()
                    .multiply(request.getHoursWorked());
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

        // Ensure provider finalized job
        if (booking.getTotalPrice() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Booking not finalized yet"
            );
        }

        // Complete booking
        booking.setStatus(BookingStatus.COMPLETED);

        // Make provider available again
        booking.getServiceProvider().setIsAvailable(true);
    }

    @Override
    public List<ProviderBookingResponse> getProviderBookingResponses(Long serviceProviderId) {

        List<Booking> bookings =
                bookingRepository.findByProviderService_ServiceProvider_ServiceProviderId(serviceProviderId);

        return bookings.stream().map(booking -> {

            ProviderBookingResponse dto = new ProviderBookingResponse();

            dto.setBookingId(booking.getBookingId());
            dto.setStatus(booking.getStatus());
            dto.setDescription(booking.getDescription());
            dto.setPaymentAmount(booking.getTotalPrice());
            dto.setPaymentType(booking.getPricingType().name());


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
            dto.setProviderServiceId(
                    booking.getProviderService().getId()
            );



            return dto;

        }).toList();
    }


}
