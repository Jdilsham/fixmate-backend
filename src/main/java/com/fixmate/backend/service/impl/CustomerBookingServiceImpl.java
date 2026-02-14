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

        Instant scheduledAt = dto.getScheduledAt();

        if (scheduledAt == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Service date and time are required"
            );
        }

        LocalDateTime utcDateTime = LocalDateTime.ofInstant(scheduledAt, ZoneOffset.UTC);

        // Reject date-only bookings (00:00 UTC)
        if (utcDateTime.getHour() == 0 && utcDateTime.getMinute() == 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Please select a service time"
            );
        }

        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ProviderService providerService =
                providerServiceRepository.findById(dto.getProviderServiceId())
                        .orElseThrow(() -> new ResourceNotFoundException("Provider service not found"));

        ServiceProvider provider = providerService.getServiceProvider();

        if (!Boolean.TRUE.equals(provider.getIsAvailable())) {
            throw new ResponseStatusException(CONFLICT, "Service Provider not available");
        }

        Instant startTime = scheduledAt;
        Instant endTime = scheduledAt.plusSeconds(BOOKING_DURATION_SECONDS);
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

        if (dto.getLatitude() == null || dto.getLongitude() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Please pick your location on the map"
            );
        }

        if (dto.getAddressLine1() == null || dto.getAddressLine1().trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Address is required (from map pin)"
            );
        }

        if (dto.getProvince() == null || dto.getProvince().trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Province is required (from map pin)"
            );
        }

        String addressLine1 = dto.getAddressLine1().trim();
        String addressLine2 = (dto.getAddressLine2() == null || dto.getAddressLine2().trim().isEmpty())
                ? null
                : dto.getAddressLine2().trim();

        String city = (dto.getCity() == null || dto.getCity().trim().isEmpty())
                ? null
                : dto.getCity().trim();

        String province = dto.getProvince().trim();

        BigDecimal latitude = dto.getLatitude();
        BigDecimal longitude = dto.getLongitude();

        String phone = (dto.getPhone() == null || dto.getPhone().trim().isEmpty())
                ? customer.getPhone()
                : dto.getPhone().trim();

        StringBuilder fullAddress = new StringBuilder();
        fullAddress.append(addressLine1);

        if (addressLine2 != null) {
            fullAddress.append(", ").append(addressLine2);
        }
        if (city != null) {
            fullAddress.append(", ").append(city);
        }
        fullAddress.append(", ").append(province);

        Booking booking = new Booking();
        booking.setUser(customer);
        booking.setServiceProvider(provider);
        booking.setProviderService(providerService);
        booking.setScheduledAt(dto.getScheduledAt());
        booking.setDescription(dto.getDescription());
        booking.setStatus(BookingStatus.PENDING);
        booking.setPricingType(dto.getPricingType());


        BookingContactInfo contactInfo = BookingContactInfo.builder()
                .address(fullAddress.toString())
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
