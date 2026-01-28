package com.fixmate.backend.service;

import com.fixmate.backend.dto.request.BookingRequest;
import com.fixmate.backend.dto.response.CustomerBookingResponse;
import com.fixmate.backend.entity.Booking;
import com.fixmate.backend.repository.BookingRepository;

import java.util.List;

public interface CustomerBookingService {

    CustomerBookingResponse createBooking(String email, BookingRequest dto);

    List<Booking> getMyBookings(Long customerId);

    List<Booking> getMyBookingsByEmail(String email);

}
