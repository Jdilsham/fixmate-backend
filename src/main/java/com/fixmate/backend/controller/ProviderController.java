package com.fixmate.backend.controller;

import com.fixmate.backend.dto.request.AddServiceRequestDTO;
import com.fixmate.backend.dto.request.AddressRequest;
import com.fixmate.backend.dto.request.ProfileUpdateReq;
import com.fixmate.backend.dto.response.*;
import com.fixmate.backend.entity.Booking;
import com.fixmate.backend.entity.User;
import com.fixmate.backend.mapper.BookingMapper;
import com.fixmate.backend.service.ProviderBookingService;
import com.fixmate.backend.service.ProviderServiceService;
import com.fixmate.backend.service.ServiceProviderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/provider")
@RequiredArgsConstructor
public class ProviderController {

    private final ServiceProviderService providerService;
    private final ProviderBookingService bookingService;
    private final ProviderServiceService providerServiceService;
    private final BookingMapper bookingMapper;


    @GetMapping("/profile")
    public ProviderProfileDTO profile(Authentication auth) {
        return providerService.getProfile(getUserId(auth));
    }

    @GetMapping("/{id}")
    public ProviderProfileDTO getProviderProfile(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long currentUserId = null;

        if (authentication != null &&
                authentication.getPrincipal() instanceof User user) {
            currentUserId = user.getId();
        }

        return providerService.getProfileById(id, currentUserId);
    }


    @PutMapping("/profile")
    public void updateProfile(
            Authentication auth,
            @Valid @RequestBody ProfileUpdateReq req
    ) {
        providerService.updateProfile(getUserId(auth), req);
    }


    @PutMapping(
            value = "/profile/picture",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public void updateProfilePicture(
            Authentication auth,
            @RequestParam(value = "profilePic", required = false) MultipartFile profilePic
    ) {
        if (profilePic == null || profilePic.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Profile picture file is required"
            );
        }

        providerService.updateProfilePicture(getUserId(auth), profilePic);
    }



    @PostMapping("/address")
    public AddressResponse createAddress(
            Authentication auth,
            @RequestBody AddressRequest request
    ) {
        Long userId = getUserId(auth);
        return providerService.addProviderAddress(userId, request);
    }

    @PutMapping("/address")
    public AddressResponse updateAddress(
            Authentication auth,
            @RequestBody AddressRequest request
    ) {
        Long userId = getUserId(auth);
        return providerService.updateProviderAddress(userId, request);
    }

    @PutMapping(value = "/verification/pdf",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    ) public void uploadVerificationPdf(

            Authentication auth,
            @RequestParam("pdf") MultipartFile pdf
    ) {
        providerService.uploadVerificationPdf(getUserId(auth), pdf);
    }


    @PatchMapping("/availability")
    public Map<String, Boolean> toggleAvailability(Authentication authentication) {

        String email = authentication.getName();
        boolean status = providerService.toggleAvailability(email);

        return Map.of("isAvailable", status);
    }


    @PostMapping("/verify")
    public void requestVerification(Authentication auth) {
        providerService.requestVerification(getUserId(auth));
    }

    @GetMapping("/{serviceProviderId}/bookings")
    public ResponseEntity<List<BookingResponseDTO>>getProviderBookings(
            @PathVariable Long serviceProviderId
    ){
        return ResponseEntity.ok(
          bookingMapper.toDtoList(
             bookingService.getProviderBookings(serviceProviderId)
          )
        );
    }

    @PostMapping("/bookings/{bookingId}/confirm")
    public ResponseEntity<Void> confirmBooking(
            @PathVariable Long bookingId,
            @RequestParam Long providerServiceId,
            Authentication auth
    ) {
        bookingService.confirmBooking(
                bookingId,
                getUserId(auth),
                providerServiceId
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/bookings/{bookingId}/cancel")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable Long bookingId,
            @RequestParam Long providerServiceId,
            @RequestParam String reason,
            Authentication auth
    ) {
        bookingService.cancelBooking(
                bookingId,
                getUserId(auth),
                providerServiceId,
                reason
        );
        return ResponseEntity.ok().build();
    }

    @GetMapping("/earnings")
    public EarningSummaryDTO earnings(Authentication auth) {
        return providerService.getEarnings(getUserId(auth));
    }

    @PostMapping("/services")
    public ResponseEntity<?> addServiceToProvider(
            @RequestBody @Valid AddServiceRequestDTO dto,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();

        providerServiceService.addServiceToProvider(
                user.getId(),
                dto
        );

        return ResponseEntity.ok("Service added successfully");
    }


    private Long getUserId(Authentication auth) {
        return ((User) auth.getPrincipal()).getId();
    }
}
