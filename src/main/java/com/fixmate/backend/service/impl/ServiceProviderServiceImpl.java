package com.fixmate.backend.service.impl;

import com.fixmate.backend.dto.request.ProfileUpdateReq;
import com.fixmate.backend.dto.response.ProviderBookingResponse;
import com.fixmate.backend.dto.response.EarningSummaryDTO;
import com.fixmate.backend.dto.response.ProviderProfileDTO;
import com.fixmate.backend.entity.*;
import com.fixmate.backend.mapper.ProviderMapper;
import com.fixmate.backend.repository.BookingRepository;
import com.fixmate.backend.repository.ServiceProviderRepository;
import com.fixmate.backend.service.FileStorageService;
import com.fixmate.backend.service.ServiceProviderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
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
    private final FileStorageService fileStorageService;

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
        public boolean toggleAvailability(String email) {

            ServiceProvider provider = serviceProviderRepository.findByUserEmail(email)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Service provider profile not found"
                    ));

            if (!Boolean.TRUE.equals(provider.getIsVerified())) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Your provider account is not verified"
                );
            }

            boolean current = Boolean.TRUE.equals(provider.getIsAvailable());
            provider.setIsAvailable(!current);

            return provider.getIsAvailable();
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
        //provider.setAddress(req.getAddress());
        provider.setDescription(req.getDescription());
        provider.setCity(req.getCity());
        provider.setRating(req.getRating());
       // provider.setIsVerified(false);
        if (req.getPhone() != null) {
            provider.getUser().setPhone(req.getPhone());
        }

        MultipartFile pdf = req.getWorkPdf();

        if (pdf != null && !pdf.isEmpty()) {

            if (!"application/pdf".equalsIgnoreCase(pdf.getContentType())) {
                throw new IllegalArgumentException("Only PDF files are allowed");
            }

            String pdfUrl = fileStorageService.upload(pdf);
            provider.setWorkPdfUrl(pdfUrl);
        }
        serviceProviderRepository.save(provider);

    }

    @Override
    public List<ProviderBookingResponse> getBookings(Long userId) {


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

        BigDecimal total = bookingRepository.sumConfirmedAmounts(
                provider.getServiceProviderId()
        );

        return new EarningSummaryDTO(
                total != null ? total : BigDecimal.ZERO
        );
    }

    @Override
    public ProviderProfileDTO getProfileById(Long providerId, Long currentUserId) {

        ServiceProvider provider = serviceProviderRepository
                .findByServiceProviderIdAndIsVerifiedTrue(providerId)
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        ProviderProfileDTO dto = providerMapper.toProfileDTO(provider);

        boolean isOwner = currentUserId != null &&
                provider.getUser().getId().equals(currentUserId);

        dto.setIsOwner(isOwner);

        return dto;
    }

    @Override
    public void updateDescription(Long providerId, Long userId, String description) {

        ServiceProvider provider = serviceProviderRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        if (!provider.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Not profile owner");
        }

        provider.setDescription(description);
    }


    private ProviderBookingResponse mapToBookingDetail(Booking booking) {

        ProviderBookingResponse dto = new ProviderBookingResponse();

        dto.setBookingId(booking.getBookingId());
        dto.setCustomerName(booking.getUser().getFirstName());
        dto.setCustomerPhone(booking.getUser().getPhone());
        dto.setServiceTitle(booking.getProviderService().getService().getTitle());
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
                payment != null && payment.getPaymentMethod() != null
                        ? payment.getPaymentMethod().name()
                        : "N/A"
        );

//        dto.setAddress(
//                booking.getAddresses().stream()
//                        .findFirst()
//                        .map(a -> a.getCity())
//                        .orElse("N/A")
//        );

        return dto;
    }
}
