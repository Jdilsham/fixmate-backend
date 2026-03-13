package com.fixmate.backend.service.impl;

import com.fixmate.backend.dto.response.PublicServiceCardResponse;
import com.fixmate.backend.dto.response.ServiceProviderCardDTO;
import com.fixmate.backend.entity.ServiceProvider;
import com.fixmate.backend.entity.User;
import com.fixmate.backend.enums.VerificationStatus;
import com.fixmate.backend.repository.BookingRepository;
import com.fixmate.backend.repository.ProviderServiceRepository;
import com.fixmate.backend.repository.ServiceProviderRepository;
import com.fixmate.backend.repository.UserRepository;
import com.fixmate.backend.service.ClientViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientViewServiceImpl implements ClientViewService {
    private final ServiceProviderRepository serviceProviderRepository;
    private final ProviderServiceRepository providerServiceRepository;
    private final BookingRepository  bookingRepository;

    @Override
    public List<ServiceProviderCardDTO> getAllVerifiedAndAvailableProviders() {
        return serviceProviderRepository
                .findByIsVerifiedTrueAndIsAvailableTrue()
                .stream()
                .map(this::mapToCardDTO)
                .toList();
    }

    private ServiceProviderCardDTO mapToCardDTO(ServiceProvider provider) {
        User user = provider.getUser();

        String fullName = user.getFirstName() + " " + user.getLastName();

        String defaultImage = "https://cdn-icons-png.flaticon.com/512/149/149071.png";

        String imageUrl = user.getProfilePic() != null
                ? user.getProfilePic()
                : defaultImage;


        return ServiceProviderCardDTO.builder()
                .id(provider.getServiceProviderId())
                .fullName(fullName)
                .skill(provider.getSkill())
                .rating(provider.getRating())
                .description(provider.getDescription())
                .location(provider.getCity())
                .imageUrl(imageUrl)
                .isVerified(provider.getIsVerified())
                .build();

    }

    @Override
    public PublicServiceCardResponse getPublicServiceById(Long providerServiceId) {

        return providerServiceRepository
                .findPublicServiceById(
                        providerServiceId,
                        VerificationStatus.APPROVED
                )
                .orElseThrow(() ->
                        new RuntimeException("Service not found")
                );
    }

    @Override
    public List<String> getAvailableSlots(Long providerServiceId, String date) {

        ZoneId SRI_LANKA = ZoneId.of("Asia/Colombo");

        LocalDate localDate = LocalDate.parse(date);

        // Day boundaries in Sri Lanka time → convert to UTC
        Instant dayStart = localDate
                .atStartOfDay(SRI_LANKA)
                .toInstant();

        Instant dayEnd = localDate
                .plusDays(1)
                .atStartOfDay(SRI_LANKA)
                .toInstant();

        // Fetch booked slots (UTC)
        List<Instant> bookedTimes =
                bookingRepository.findBookedSlotsForDay(
                        providerServiceId,
                        dayStart,
                        dayEnd
                );

        // Convert bookings to Sri Lanka time
        List<LocalDateTime> bookedDateTimes = bookedTimes.stream()
                .map(i -> LocalDateTime.ofInstant(i, SRI_LANKA))
                .toList();

        List<String> availableSlots = new ArrayList<>();

        // Business hours in Sri Lanka
        LocalDateTime slotStart = localDate.atTime(8, 0);
        LocalDateTime slotEndLimit = localDate.atTime(18, 0);

        while (slotStart.plusHours(2).isBefore(slotEndLimit.plusSeconds(1))) {

            LocalDateTime slotEnd = slotStart.plusHours(2);

            final LocalDateTime checkStart = slotStart;
            final LocalDateTime checkEnd = slotEnd;

            boolean overlaps = bookedDateTimes.stream().anyMatch(booked ->
                    booked.isBefore(checkEnd) &&
                            booked.plusHours(2).isAfter(checkStart)
            );

            if (!overlaps) {
                availableSlots.add(
                        checkStart.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))
                );
            }

            slotStart = slotStart.plusHours(2);
        }

        return availableSlots;
    }



}
