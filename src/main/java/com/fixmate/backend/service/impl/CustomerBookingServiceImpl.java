package com.fixmate.backend.service.impl;

import com.fixmate.backend.dto.request.BookingRequest;
import com.fixmate.backend.dto.response.CustomerBookingResponse;
import com.fixmate.backend.entity.*;
import com.fixmate.backend.enums.BookingStatus;
import com.fixmate.backend.exception.ResourceNotFoundException;
import com.fixmate.backend.mapper.CustomerMapper;
import com.fixmate.backend.repository.*;
import com.fixmate.backend.service.CustomerBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.CONFLICT;

@Service
@RequiredArgsConstructor
public class CustomerBookingServiceImpl implements CustomerBookingService {
    private final UserRepository userRepository;
    private final CustomerMapper mapper;
    private final BookingRepository bookingRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final ServiceRepository serviceRepository;
    private final PasswordEncoder passwordEncoder;
    private final AddressRepository addressRepository;



    @Override
    @Transactional
    public CustomerBookingResponse createBooking(String email, BookingRequest dto) {

        //USER
        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        //PROVIDER
        ServiceProvider provider = serviceProviderRepository.findById(dto.getProviderId())
                .orElseThrow(() -> new ResourceNotFoundException("Service Provider not found"));

        if (!Boolean.TRUE.equals((provider.getIsAvailable()))){
            throw new ResponseStatusException(CONFLICT,"Service Provider not available");
        }

        //SERVICE
        Services service = serviceRepository.findById(dto.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));


        //Resolve address
        String resolvedAddress = dto.getAddress();
        String resolvedCity = dto.getCity();
        String resolvedPhone = dto.getPhone();
        var resolvedLatitude = dto.getLatitude();
        var resolvedLongitude = dto.getLongitude();

        if (resolvedAddress == null || resolvedCity == null) {
            Address profileAddress = addressRepository.findFirstByUserIdOrderByAddressIdDesc(customer.getId())
                    .orElse(null);

            if (profileAddress == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please provide an address");
            }

            resolvedAddress = profileAddress.getAddress();
            resolvedCity = profileAddress.getCity();
            resolvedLatitude = profileAddress.getLatitude();
            resolvedLongitude = profileAddress.getLongitude();
            resolvedPhone = customer.getPhone();

        }

        //Booking
        Booking booking = new Booking();
        booking.setUser(customer);
        booking.setServiceProvider(provider);
        booking.setService(service);
        booking.setScheduledAt(dto.getScheduledAt());
        booking.setTotalPrice(service.getBasePrice());
        booking.setDescription(dto.getDescription());
        booking.setStatus(BookingStatus.PENDING);

        //snapshot entity
        BookingContactInfo contactInfo = BookingContactInfo.builder()
                .address(resolvedAddress)
                .city(resolvedCity)
                .phone(resolvedPhone)
                .latitude(resolvedLatitude)
                .longitude(resolvedLongitude)
                .booking(booking)
                .build();

        booking.setContactInfo(contactInfo);

        Booking saved =  bookingRepository.save(booking);

        return CustomerBookingResponse.builder()
                .bookingId(saved.getBookingId())
                .status(saved.getStatus())
                .build();


    }


}
