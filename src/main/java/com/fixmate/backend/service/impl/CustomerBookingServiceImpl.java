package com.fixmate.backend.service.impl;

import com.fixmate.backend.dto.request.BookingRequest;
import com.fixmate.backend.dto.response.CustomerBookingResponse;
import com.fixmate.backend.entity.*;
import com.fixmate.backend.enums.BookingStatus;
import com.fixmate.backend.exception.ResourceNotFoundException;
import com.fixmate.backend.repository.*;
import com.fixmate.backend.service.CustomerBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
import com.fixmate.backend.dto.request.SmartBookingRequest;

@Service
@RequiredArgsConstructor
public class CustomerBookingServiceImpl implements CustomerBookingService {

    private static final long BOOKING_DURATION_SECONDS = 2 * 60 * 60; // 2 hours

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ProviderServiceRepository providerServiceRepository;
    private final EmailService emailService;



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

        /* Prevent provider booking their own service */
        if (provider.getUser() != null && provider.getUser().getId().equals(customer.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "You cannot book your own service"
            );
        }

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

        // Send booking confirmation email
        emailService.sendBookingConfirmationEmail(
                customer.getEmail(),
                customer.getFirstName(),
                providerService.getService().getTitle(),
                saved.getBookingId().toString(),
                saved.getScheduledAt()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime(),
                provider.getUser().getFirstName() + " " + provider.getUser().getLastName(),
                provider.getUser().getPhone(),
                fullAddress.toString()
        );

        /* Provider notification email */
        emailService.sendProviderNewBookingEmail(
                provider.getUser().getEmail(),
                provider.getUser().getFirstName(),
                customer.getFirstName() + " " + customer.getLastName(),
                providerService.getService().getTitle(),
                saved.getBookingId().toString(),
                saved.getScheduledAt()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime(),
                fullAddress.toString(),
                phone
        );

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

    @Override
    @Transactional
    public CustomerBookingResponse createSmartBooking(String email, SmartBookingRequest dto) {

        validateSmartBookingRequest(dto);

        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<ProviderService> candidates =
                providerServiceRepository.findSmartBookingCandidates(dto.getServiceId());

        if (candidates.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No available providers found for this service"
            );
        }

        Instant scheduledAt = dto.getScheduledAt();

        ProviderService bestProviderService;

        List<ProviderService> availableCandidates = candidates.stream()
                .filter(ps -> ps.getServiceProvider() != null)
                .filter(ps -> !hasTimeConflict(ps.getServiceProvider(), scheduledAt))
                .toList();

        if (availableCandidates.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No providers are available for the selected time"
            );
        }

        if (dto.getPricingType() == com.fixmate.backend.enums.PricingType.HOURLY) {

            List<ProviderService> hourlyCandidates = availableCandidates.stream()
                    .filter(ps -> ps.getHourlyRate() != null)
                    .toList();

            if (hourlyCandidates.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "No hourly-rate providers are available for the selected time"
                );
            }

            double nearestDistance = hourlyCandidates.stream()
                    .mapToDouble(ps -> {
                        Address a = getPrimaryAddress(ps.getServiceProvider().getUser());
                        return a == null
                                ? Double.MAX_VALUE
                                : calculateDistanceKm(
                                dto.getLatitude(),
                                dto.getLongitude(),
                                a.getLatitude(),
                                a.getLongitude()
                        );
                    })
                    .min()
                    .orElse(Double.MAX_VALUE);

            double distanceToleranceKm = 1.0;

            List<ProviderService> nearestCandidates = hourlyCandidates.stream()
                    .filter(ps -> {
                        Address a = getPrimaryAddress(ps.getServiceProvider().getUser());
                        double distance = a == null
                                ? Double.MAX_VALUE
                                : calculateDistanceKm(
                                dto.getLatitude(),
                                dto.getLongitude(),
                                a.getLatitude(),
                                a.getLongitude()
                        );

                        return distance <= nearestDistance + distanceToleranceKm;
                    })
                    .toList();

            bestProviderService = nearestCandidates.stream()
                    .min((ps1, ps2) -> {
                        int rateCompare = ps1.getHourlyRate().compareTo(ps2.getHourlyRate());

                        if (rateCompare != 0) {
                            return rateCompare;
                        }

                        Address a1 = getPrimaryAddress(ps1.getServiceProvider().getUser());
                        Address a2 = getPrimaryAddress(ps2.getServiceProvider().getUser());

                        double d1 = a1 == null
                                ? Double.MAX_VALUE
                                : calculateDistanceKm(
                                dto.getLatitude(),
                                dto.getLongitude(),
                                a1.getLatitude(),
                                a1.getLongitude()
                        );

                        double d2 = a2 == null
                                ? Double.MAX_VALUE
                                : calculateDistanceKm(
                                dto.getLatitude(),
                                dto.getLongitude(),
                                a2.getLatitude(),
                                a2.getLongitude()
                        );

                        return Double.compare(d1, d2);
                    })
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "No hourly-rate providers are available for the selected time"
                    ));
        }
         else {
            bestProviderService = availableCandidates.stream()
                    .min((ps1, ps2) -> {
                        Address a1 = getPrimaryAddress(ps1.getServiceProvider().getUser());
                        Address a2 = getPrimaryAddress(ps2.getServiceProvider().getUser());

                        double d1 = a1 == null
                                ? Double.MAX_VALUE
                                : calculateDistanceKm(
                                dto.getLatitude(),
                                dto.getLongitude(),
                                a1.getLatitude(),
                                a1.getLongitude()
                        );

                        double d2 = a2 == null
                                ? Double.MAX_VALUE
                                : calculateDistanceKm(
                                dto.getLatitude(),
                                dto.getLongitude(),
                                a2.getLatitude(),
                                a2.getLongitude()
                        );

                        return Double.compare(d1, d2);
                    })
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "No providers are available for the selected time"
                    ));
        }

        ServiceProvider provider = bestProviderService.getServiceProvider();

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

        String fullAddress = buildFullAddress(addressLine1, addressLine2, city, province);

        Booking booking = new Booking();
        booking.setUser(customer);
        booking.setServiceProvider(provider);
        booking.setProviderService(bestProviderService);
        booking.setScheduledAt(dto.getScheduledAt());
        booking.setDescription(dto.getDescription());
        booking.setStatus(BookingStatus.PENDING);
        booking.setPricingType(dto.getPricingType());

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

        // Send smart booking confirmation email
        emailService.sendBookingConfirmationEmail(
                customer.getEmail(),
                customer.getFirstName(),
                bestProviderService.getService().getTitle(),
                saved.getBookingId().toString(),
                saved.getScheduledAt()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime(),
                provider.getUser().getFirstName() + " " + provider.getUser().getLastName(),
                provider.getUser().getPhone(),
                fullAddress
        );

        /* Provider notification */
        emailService.sendProviderNewBookingEmail(
                provider.getUser().getEmail(),
                provider.getUser().getFirstName(),
                customer.getFirstName() + " " + customer.getLastName(),
                bestProviderService.getService().getTitle(),
                saved.getBookingId().toString(),
                saved.getScheduledAt()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime(),
                fullAddress,
                phone
        );


        return CustomerBookingResponse.builder()
                .bookingId(saved.getBookingId())
                .serviceName(bestProviderService.getService().getTitle())
                .providerName(provider.getUser().getFirstName() + " " + provider.getUser().getLastName())
                .customerName(customer.getFirstName() + " " + customer.getLastName())
                .phone(phone)
                .providerPhone(provider.getUser().getPhone())
                .address(fullAddress)
                .city(city)
                .status(saved.getStatus())
                .amount(saved.getTotalPrice())
                .scheduledAt(
                        saved.getScheduledAt()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime()
                )
                .pricingType(saved.getPricingType().name())
                .description(saved.getDescription())
                .rejectionReason(saved.getRejectionReason())
                .rejectedAt(saved.getRejectedAt())
                .build();
    }

    private void validateSmartBookingRequest(SmartBookingRequest dto) {
        Instant scheduledAt = dto.getScheduledAt();

        if (scheduledAt == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Service date and time are required"
            );
        }

        LocalDateTime utcDateTime = LocalDateTime.ofInstant(scheduledAt, ZoneOffset.UTC);

        if (utcDateTime.getHour() == 0 && utcDateTime.getMinute() == 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Please select a service time"
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
    }

    private boolean hasTimeConflict(ServiceProvider provider, Instant scheduledAt) {
        Instant startTime = scheduledAt;
        Instant endTime = scheduledAt.plusSeconds(BOOKING_DURATION_SECONDS);
        Instant startBoundary = startTime.minusSeconds(BOOKING_DURATION_SECONDS);

        List<Booking> conflicts = bookingRepository.findPotentialOverlaps(
                provider.getServiceProviderId(),
                startBoundary,
                endTime
        );

        return !conflicts.isEmpty();
    }

    private double calculateDistanceKm(
            BigDecimal lat1,
            BigDecimal lon1,
            BigDecimal lat2,
            BigDecimal lon2
    ) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            return Double.MAX_VALUE;
        }

        double earthRadiusKm = 6371.0;

        double dLat = Math.toRadians(lat2.doubleValue() - lat1.doubleValue());
        double dLon = Math.toRadians(lon2.doubleValue() - lon1.doubleValue());

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1.doubleValue()))
                * Math.cos(Math.toRadians(lat2.doubleValue()))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadiusKm * c;
    }

    private Address getPrimaryAddress(User user) {
        return user.getAddresses()
                .stream()
                .findFirst()
                .orElse(null);
    }

    private String buildFullAddress(
            String addressLine1,
            String addressLine2,
            String city,
            String province
    ) {
        StringBuilder fullAddress = new StringBuilder();
        fullAddress.append(addressLine1);

        if (addressLine2 != null && !addressLine2.isBlank()) {
            fullAddress.append(", ").append(addressLine2.trim());
        }

        if (city != null && !city.isBlank()) {
            fullAddress.append(", ").append(city.trim());
        }

        fullAddress.append(", ").append(province);

        return fullAddress.toString();
    }


}
