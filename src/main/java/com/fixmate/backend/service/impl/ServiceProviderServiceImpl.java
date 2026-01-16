package com.fixmate.backend.service.impl;

import com.fixmate.backend.dto.request.AddressRequest;
import com.fixmate.backend.dto.request.ProfileUpdateReq;
import com.fixmate.backend.dto.response.AddressResponse;
import com.fixmate.backend.dto.response.ProviderBookingResponse;
import com.fixmate.backend.dto.response.EarningSummaryDTO;
import com.fixmate.backend.dto.response.ProviderProfileDTO;
import com.fixmate.backend.entity.*;
import com.fixmate.backend.enums.VerificationStatus;
import com.fixmate.backend.mapper.ProviderMapper;
import com.fixmate.backend.repository.AddressRepository;
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
    private final AddressRepository addressRepository;


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

        User user = provider.getUser();

        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setPhone(req.getPhone());
    }

    @Override
    public void updateProfilePicture(Long userId, MultipartFile profilePic) {

        if (profilePic == null || profilePic.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Profile picture is required"
            );
        }

        if (!profilePic.getContentType().startsWith("image/")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only image files are allowed"
            );
        }

        ServiceProvider provider = serviceProviderRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Service provider profile not found"
                        )
                );

        String imageUrl = fileStorageService.upload(profilePic);

        provider.setProfileImage(imageUrl); // ✅ FIXED
    }





    @Override
    @Transactional
    public AddressResponse addProviderAddress(Long userId, AddressRequest request) {

        ServiceProvider provider = serviceProviderRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Service provider profile not found"
                        )
                );

        if (addressRepository.findByUserId(userId).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Address already exists. Use update."
            );
        }

        Address address = new Address();
        address.setUser(provider.getUser());
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setLatitude(request.getLatitude());
        address.setLongitude(request.getLongitude());

        Address saved = addressRepository.save(address);

        return mapToResponse(saved);
    }

    @Transactional
    public AddressResponse updateProviderAddress(Long userId, AddressRequest request) {

        ServiceProvider provider = serviceProviderRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Service provider profile not found"
                        )
                );

        Address address = addressRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Address not found. Create it first."
                        )
                );

        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setLatitude(request.getLatitude());
        address.setLongitude(request.getLongitude());

        Address saved = addressRepository.save(address);

        return mapToResponse(saved);
    }

    private AddressResponse mapToResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getAddressId())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .province(address.getProvince())
                .city(address.getCity())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .build();
    }

    public void uploadVerificationPdf(Long userId, MultipartFile pdf) {

        if (pdf == null || pdf.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Verification PDF is required"
            );
        }

        if (pdf.getContentType() == null ||
                !pdf.getContentType().equalsIgnoreCase("application/pdf")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only PDF files are allowed"
            );
        }

        ServiceProvider provider = serviceProviderRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Service provider profile not found"
                        )
                );

        String pdfUrl;
        try {
            pdfUrl = fileStorageService.upload(pdf);
            System.out.println("PDF uploaded to: " + pdfUrl);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "PDF upload failed"
            );
        }

        provider.setWorkPdfUrl(pdfUrl);
        provider.setVerificationStatus(VerificationStatus.PENDING);
        provider.setIsVerified(false);
        provider.setIsAvailable(false);
    }

    @Override
    public List<ProviderBookingResponse> getBookings(Long userId) {


        ServiceProvider provider = getVerifiedProviderByUserId(userId);

        return bookingRepository
                .findByProviderService_ServiceProvider_ServiceProviderId(
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
