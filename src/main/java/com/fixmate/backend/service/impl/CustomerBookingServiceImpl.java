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

        // Resolve snapshot fields
        String addressLine1 = dto.getAddressLine1();
        String addressLine2 = dto.getAddressLine2();
        String city = dto.getCity();
        String province = dto.getProvince();
        String phone = dto.getPhone();
        var latitude = dto.getLatitude();
        var longitude = dto.getLongitude();

        // Fallback to profile address if missing
        if (addressLine1 == null || city == null || province == null) {

            Address profileAddress = addressRepository
                    .findFirstByUserIdOrderByAddressIdDesc(customer.getId())
                    .orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.BAD_REQUEST,
                                    "Please provide an address"
                            )
                    );

            addressLine1 = profileAddress.getAddressLine1();
            addressLine2 = profileAddress.getAddressLine2();
            city = profileAddress.getCity();
            province = profileAddress.getProvince();
            latitude = profileAddress.getLatitude();
            longitude = profileAddress.getLongitude();
            phone = customer.getPhone();
        }

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
        booking.setTotalPrice(providerService.getBasePrice()); // ✅ CORRECT
        booking.setDescription(dto.getDescription());
        booking.setStatus(BookingStatus.PENDING);

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
