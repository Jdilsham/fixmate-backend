package com.fixmate.backend.service;

import com.fixmate.backend.entity.Booking;

import java.util.List;

public interface ProviderBookingService {

    List<Booking> getProviderBookings(Long ServiceProviderId);

    void confirmBookings(Long bookingId, Long serviceProviderId);

    void cancelBookings(Long bookingId, Long serviceProviderId, String reason);
}
