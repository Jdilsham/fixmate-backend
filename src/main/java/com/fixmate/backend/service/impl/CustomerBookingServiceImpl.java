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

import java.math.BigDecimal;

import static org.springframework.http.HttpStatus.CONFLICT;

@Service
@RequiredArgsConstructor
public class CustomerBookingServiceImpl implements CustomerBookingService {
    private final UserRepository userRepository;
    private final CustomerMapper mapper;
    private final BookingRepository bookingRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final ProviderServiceRepository providerServiceRepository;
    private final PasswordEncoder passwordEncoder;
    private final AddressRepository addressRepository;


    @Override
    @Transactional
    public CustomerBookingResponse createBooking(String email, BookingRequest dto) {

        // USER
        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // PROVIDER SERVICE (provider + service + price)
        ProviderService providerService =
                providerServiceRepository.findById(dto.getProviderServiceId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Provider service not found")
                        );

        ServiceProvider provider = providerService.getServiceProvider();

        if (!Boolean.TRUE.equals(provider.getIsAvailable())) {
            throw new ResponseStatusException(CONFLICT, "Service Provider not available");
        }

        // Load customer profile address
        Address profileAddress = addressRepository
                .findFirstByUserIdOrderByAddressIdDesc(customer.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "No address found in profile"
                ));

        String addressLine1 = profileAddress.getAddressLine1();
        String addressLine2 = profileAddress.getAddressLine2();
        String city = profileAddress.getCity();
        String province = profileAddress.getProvince();
        BigDecimal latitude = profileAddress.getLatitude();
        BigDecimal longitude = profileAddress.getLongitude();
        String phone = customer.getPhone();

        // Override ONLY if customer sent values
        if (dto.getAddressLine1() != null) addressLine1 = dto.getAddressLine1();
        if (dto.getAddressLine2() != null) addressLine2 = dto.getAddressLine2();
        if (dto.getCity() != null) city = dto.getCity();
        if (dto.getProvince() != null) province = dto.getProvince();
        if (dto.getLatitude() != null) latitude = dto.getLatitude();
        if (dto.getLongitude() != null) longitude = dto.getLongitude();
        if (dto.getPhone() != null) phone = dto.getPhone();


        String fullAddress =
                addressLine1 +
                        (addressLine2 != null ? ", " + addressLine2 : "") +
                        ", " + city +
                        ", " + province;

        // BOOKING
        Booking booking = new Booking();
        booking.setUser(customer);
        booking.setServiceProvider(provider);
        booking.setProviderService(providerService);
        booking.setScheduledAt(dto.getScheduledAt());
        booking.setDescription(dto.getDescription());
        booking.setStatus(BookingStatus.PENDING);
        booking.setPricingType(dto.getPricingType());


        // SNAPSHOT CONTACT INFO
        BookingContactInfo contactInfo = BookingContactInfo.builder()
                .address(fullAddress)
                .city(city)
                .phone(phone)
                .latitude(latitude)
                .longitude(longitude)
                .booking(booking)
                .build();

        booking.setContactInfo(contactInfo);

        Booking saved = bookingRepository.save(booking);

        return CustomerBookingResponse.builder()
                .bookingId(saved.getBookingId())
                .status(saved.getStatus())
                .build();
    }

}
