package com.fixmate.backend.service;

import com.fixmate.backend.entity.Booking;

import java.util.List;

public interface ProviderBookingService {

    List<Booking> getProviderBookings(Long ServiceProviderId);

    void confirmBooking(Long bookingId, Long serviceProviderId, Long providerServiceId);

    void cancelBooking(Long bookingId, Long serviceProviderId, Long providerServiceId, String reason);
}
