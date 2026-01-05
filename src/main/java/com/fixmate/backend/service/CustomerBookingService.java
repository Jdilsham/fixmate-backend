package com.fixmate.backend.service;

import com.fixmate.backend.dto.request.BookingRequest;
import com.fixmate.backend.dto.response.CustomerBookingResponse;

public interface CustomerBookingService {
    CustomerBookingResponse createBooking(String email, BookingRequest dto);
}
