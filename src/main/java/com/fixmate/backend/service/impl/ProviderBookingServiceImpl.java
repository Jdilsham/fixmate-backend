package com.fixmate.backend.service.impl;

import com.fixmate.backend.entity.Booking;
import com.fixmate.backend.enums.BookingStatus;
import com.fixmate.backend.repository.BookingRepository;
import com.fixmate.backend.service.ProviderBookingService;
import com.fixmate.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProviderBookingServiceImpl implements ProviderBookingService {

    private final BookingRepository bookingRepository;
    private final NotificationService notificationService;

    @Override
    public List<Booking> getProviderBookings(Long serviceProviderId){
        return bookingRepository.findByServiceProvider_ServiceProviderId(serviceProviderId);
    }

    @Override
    public void confirmBookings(Long bookingId, Long serviceProviderId){
        Booking booking = bookingRepository.findByBookingIdAndServiceProvider_ServiceProviderId(bookingId, serviceProviderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking notFound."));

        if(booking.getStatus() != BookingStatus.PENDING){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only pending bookings can be confirmed.");
        }


        booking.setStatus(BookingStatus.ACCEPTED);

        notificationService.notifyCustomer(booking.getUser(), "Your booking has been CONFIRMED by the service provider.");
    }

    @Override
    public void cancelBookings(Long bookingId, Long serviceProviderId, String reason) {
        if(reason == null || reason.isBlank()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please provide a reason.");
        }

        Booking booking = bookingRepository.findByBookingIdAndServiceProvider_ServiceProviderId(bookingId, serviceProviderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found."));

        if(booking.getStatus() != BookingStatus.PENDING){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only pending bookings can be cancelled.");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelReason(reason);

        notificationService.notifyCustomer(booking.getUser(), "Your booking has been cancelled by the service provider.");
    }
}
