package com.fixmate.backend.service.impl;

import com.fixmate.backend.dto.request.BookingRequest;
import com.fixmate.backend.dto.request.CustomerUpdateReq;
import com.fixmate.backend.dto.response.BookingResponse;
import com.fixmate.backend.dto.response.CustomerProfileResponse;
import com.fixmate.backend.entity.Booking;
import com.fixmate.backend.entity.User;
import com.fixmate.backend.enums.BookingStatus;
import com.fixmate.backend.enums.Role;
import com.fixmate.backend.mapper.CustomerMapper;
import com.fixmate.backend.repository.BookingRepository;
import com.fixmate.backend.repository.UserRepository;
import com.fixmate.backend.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZoneId;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl  implements CustomerService {
    private final UserRepository userRepository;
    private final CustomerMapper mapper;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional(readOnly = true)
    public CustomerProfileResponse getProfile(String email) {
       User user = getUserByEmail(email);
       return mapper.toProfileResponse(user);
    }

    @Override
    public CustomerProfileResponse updateProfile(String email, CustomerUpdateReq req){
        User user = getUserByEmail(email);
        mapper.updateCustomerFromReq(req,user);
        return mapper.toProfileResponse(userRepository.save(user));
    }

    @Override
    public BookingResponse createBooking(String email, BookingRequest req){
        User user = getUserByEmail(email);

        if (user.getRole() != Role.CUSTOMER) {
            throw new ResponseStatusException(FORBIDDEN, "Only customers can create bookings");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setScheduledAt(req.getScheduledTime().atZone(ZoneId.systemDefault()).toInstant());
        booking.setStatus(BookingStatus.PENDING);

        return mapper.toBookingResponse(bookingRepository.save(booking));
    }


    //Ensure user exists
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,"User not found"));
    }


}


