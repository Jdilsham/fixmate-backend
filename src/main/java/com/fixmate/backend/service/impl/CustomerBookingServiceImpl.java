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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

import static org.springframework.http.HttpStatus.CONFLICT;

@Service
@RequiredArgsConstructor
public class CustomerBookingServiceImpl implements CustomerBookingService {

    private static final long BOOKING_DURATION_SECONDS = 2 * 60 * 60; // 2 hours

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

        // =======================
        // VALIDATE BOOKING TIME
        // =======================
        Instant scheduledAt = dto.getScheduledAt();

        if (scheduledAt == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Service date and time are required"
            );
        }

        LocalDateTime utcDateTime =
                LocalDateTime.ofInstant(scheduledAt, ZoneOffset.UTC);

        // Reject date-only bookings (00:00 UTC)
        if (utcDateTime.getHour() == 0 && utcDateTime.getMinute() == 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Please select a service time"
            );
        }

        // USER
        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // PROVIDER SERVICE
        ProviderService providerService =
                providerServiceRepository.findById(dto.getProviderServiceId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Provider service not found")
                        );

        ServiceProvider provider = providerService.getServiceProvider();

        if (!Boolean.TRUE.equals(provider.getIsAvailable())) {
            throw new ResponseStatusException(CONFLICT, "Service Provider not available");
        }

        // =======================
        // CHECK TIME OVERLAP
        // =======================

        // 2-hour booking slot
                Instant startTime = scheduledAt;
                Instant endTime = scheduledAt.plusSeconds(BOOKING_DURATION_SECONDS);

        // Expand search window to catch overlaps
                Instant startBoundary = startTime.minusSeconds(BOOKING_DURATION_SECONDS);

                List<Booking> conflicts =
                        bookingRepository.findPotentialOverlaps(
                                provider.getServiceProviderId(),
                                startBoundary,
                                endTime
                        );

                if (!conflicts.isEmpty()) {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Service provider is already booked for this time"
                    );
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


    @Override
    public List<Booking> getMyBookings(Long customerId) {
        return bookingRepository
                .findByUserIdOrderByCreatedAtDesc(customerId);
    }


    @Override
    public List<Booking> getMyBookingsByEmail(String email) {

        User customer = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        return bookingRepository
                .findByUserIdOrderByCreatedAtDesc(customer.getId());

    }

}
