package com.fixmate.backend.service.impl;

import com.fixmate.backend.dto.request.BookingRequest;
import com.fixmate.backend.dto.request.ChangePasswordRequest;
import com.fixmate.backend.dto.request.CustomerUpdateReq;
import com.fixmate.backend.dto.request.ProviderSearchRequest;
import com.fixmate.backend.dto.response.BookingResponse;
import com.fixmate.backend.dto.response.CustomerProfileResponse;
import com.fixmate.backend.dto.response.ProviderSearchResponse;
import com.fixmate.backend.entity.*;
import com.fixmate.backend.enums.BookingStatus;
import com.fixmate.backend.enums.Role;
import com.fixmate.backend.exception.InvalidPasswordException;
import com.fixmate.backend.exception.ResourceNotFoundException;
import com.fixmate.backend.mapper.CustomerMapper;
import com.fixmate.backend.repository.BookingRepository;
import com.fixmate.backend.repository.ServiceProviderRepository;
import com.fixmate.backend.repository.ServiceRepository;
import com.fixmate.backend.repository.UserRepository;
import com.fixmate.backend.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZoneId;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl  implements CustomerService {
    private final UserRepository userRepository;
    private final CustomerMapper mapper;
    private final BookingRepository bookingRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final ServiceRepository serviceRepository;
    private final PasswordEncoder passwordEncoder;


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
    @Transactional
    public BookingResponse createBooking(String email, BookingRequest dto) {

        //  CUSTOMER FROM JWT
        User customer = getUserByEmail(email);

        //  PROVIDER
        ServiceProvider provider = serviceProviderRepository.findById(dto.getProviderId())
                .orElseThrow(() -> new ResourceNotFoundException("Service Provider not found"));

        if (!Boolean.TRUE.equals(provider.getIsAvailable())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Service Provider is not available"
            );
        }

        // SERVICE
        Services service = serviceRepository.findById(dto.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        //  BOOKING
        Booking booking = new Booking();
        booking.setUser(customer);
        booking.setServiceProvider(provider);
        booking.setService(service);
        booking.setScheduledAt(dto.getScheduledAt());
        booking.setTotalPrice(service.getBasePrice());
        booking.setStatus(BookingStatus.PENDING);

        // ADDRESS
        Address address = new Address();
        address.setAddress(dto.getAddress());
        address.setCity(dto.getCity());
        address.setUser(customer);
        address.setBooking(booking);

        booking.getAddresses().add(address);

        Booking saved = bookingRepository.save(booking);

        return new BookingResponse(saved.getBookingId(), saved.getStatus());
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(() ->  new ResourceNotFoundException("User not found"));

        //verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())){
            throw new InvalidPasswordException("Invalid current password");
        }
        //confirm new password
        if (request.getConfirmationPassword() != null && !request.getNewPassword().equals(request.getConfirmationPassword())){
            throw new InvalidPasswordException("Confirmation password not match");
        }

        //encode and update new pasword
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);
    }


    //Ensure user exists
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,"User not found"));
    }


}


