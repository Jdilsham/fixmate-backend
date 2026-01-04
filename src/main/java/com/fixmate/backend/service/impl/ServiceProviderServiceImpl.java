package com.fixmate.backend.service.impl;

import com.fixmate.backend.dto.request.ProfileUpdateReq;
import com.fixmate.backend.dto.response.BookingDetailResponse;
import com.fixmate.backend.dto.response.EarningSummaryDTO;
import com.fixmate.backend.dto.response.ProviderProfileDTO;
import com.fixmate.backend.entity.Booking;
import com.fixmate.backend.entity.Payment;
import com.fixmate.backend.entity.ServiceProvider;
import com.fixmate.backend.mapper.ProviderMapper;
import com.fixmate.backend.repository.BookingRepository;
import com.fixmate.backend.repository.ServiceProviderRepository;
import com.fixmate.backend.service.ServiceProviderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.ZoneId;


import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ServiceProviderServiceImpl implements ServiceProviderService {

    private final ServiceProviderRepository serviceProviderRepository;
    private final BookingRepository bookingRepository;
    private final ProviderMapper providerMapper;

    @Override
    public void requestVerification(Long userId) {

        ServiceProvider provider = serviceProviderRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Service provider profile not found"
                        )
                );


        provider.setIsVerified(false);


        System.out.println(
                "Verification requested for provider ID: " +
                        provider.getServiceProviderId()
        );
    }


    @Override
    public ServiceProvider getVerifiedProviderByUserId(Long userId) {

        ServiceProvider provider = serviceProviderRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Service provider profile not found"
                        )
                );

        if (!Boolean.TRUE.equals(provider.getIsVerified())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Your service provider account is not approved yet"
            );
        }

        return provider;
    }


    @Override
    public ProviderProfileDTO getProfile(Long userId) {
        ServiceProvider provider = getVerifiedProviderByUserId(userId);
        return providerMapper.toProfileDTO(provider);
    }

    @Override
    public void updateProfile(Long userId, ProfileUpdateReq req) {

        ServiceProvider provider = serviceProviderRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Service provider profile not found"
                        )
                );

        provider.setSkill(req.getSkill());
        provider.setExperience(req.getExperience());
        provider.setProfileImage(req.getProfileImageUrl());


        provider.setIsVerified(false);
    }

    @Override
    public List<BookingDetailResponse> getBookings(Long userId) {


        ServiceProvider provider = getVerifiedProviderByUserId(userId);

        return bookingRepository
                .findByServiceProvider_ServiceProviderId(
                        provider.getServiceProviderId()
                )
                .stream()
                .map(this::mapToBookingDetail)
                .toList();
    }


    @Override
    public EarningSummaryDTO getEarnings(Long userId) {

        ServiceProvider provider = getVerifiedProviderByUserId(userId);

        BigDecimal total = bookingRepository.sumPaidAmounts(
                provider.getServiceProviderId()
        );

        return new EarningSummaryDTO(
                total != null ? total : BigDecimal.ZERO
        );
    }


    private BookingDetailResponse mapToBookingDetail(Booking booking) {

        BookingDetailResponse dto = new BookingDetailResponse();

        dto.setBookingId(booking.getBookingId());
        dto.setCustomerName(booking.getUser().getFirstName());
        dto.setCustomerPhone(booking.getUser().getPhone());
        dto.setServiceTitle(booking.getService().getTitle());
        dto.setDescription(booking.getDescription());
        dto.setScheduledAt(
                booking.getScheduledAt()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
        );

        //dto.setScheduledAt(booking.getScheduledAt());

        Payment payment = booking.getPayment();

        dto.setPaymentAmount(
                payment != null ? payment.getAmount() : BigDecimal.ZERO
        );

        dto.setPaymentType(
                payment != null ? payment.getPaymentMethod() : "N/A"
        );

        dto.setAddress(
                booking.getAddresses().stream()
                        .findFirst()
                        .map(a -> a.getCity())
                        .orElse("N/A")
        );

        return dto;
    }
}
