package com.fixmate.backend.service;

import com.fixmate.backend.dto.request.FinalizeBookingRequest;
import com.fixmate.backend.dto.response.ProviderBookingResponse;
import com.fixmate.backend.entity.Booking;

import java.util.List;

public interface ProviderBookingService {

    List<Booking> getProviderBookings(Long ServiceProviderId);

    void confirmBooking(Long bookingId, Long serviceProviderId, Long providerServiceId);

    void cancelBooking(Long bookingId, Long serviceProviderId, Long providerServiceId, String reason);

    void startJob(Long bookingId, Long serviceProviderId, Long providerServiceId);

    void finalizeBooking(
            Long bookingId,
            Long serviceProviderId,
            Long providerServiceId,
            FinalizeBookingRequest request
    );

    void markAsPaid(Long bookingId, String customerEmail);

    List<ProviderBookingResponse> getProviderBookingResponses(Long serviceProviderId);
}
