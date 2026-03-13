package com.fixmate.backend.service;

import com.fixmate.backend.dto.request.ServiceCategoryRequest;
import com.fixmate.backend.dto.response.*;
import com.fixmate.backend.entity.ProviderService;
import com.fixmate.backend.entity.ServiceCategory;
import com.fixmate.backend.entity.ServiceProvider;
import com.fixmate.backend.entity.User;
import com.fixmate.backend.enums.Role;
import com.fixmate.backend.enums.VerificationStatus;
import com.fixmate.backend.repository.AddressRepository;
import com.fixmate.backend.repository.BookingRepository;
import com.fixmate.backend.repository.ProviderServiceRepository;
import com.fixmate.backend.repository.ServiceCategoryRepository;
import com.fixmate.backend.repository.ServiceProviderRepository;
import com.fixmate.backend.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    private final ServiceProviderRepository serviceProviderRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;
    private final ProviderServiceRepository providerServiceRepository;
    private final AddressRepository addressRepository;

    @PersistenceContext
    private EntityManager entityManager;

    // this get the admin stats for the component in the dashboard
    public AdminDashboardStats getDashboardStats() {
        return new AdminDashboardStats(
                userRepository.count(),
                serviceProviderRepository.count(),
                serviceProviderRepository.findByIsVerifiedFalse().size(),
                bookingRepository.count(),
                bookingRepository.sumConfirmedAmounts(null)
        );
    }

    // this is used to list all the users in the users tab
    public List<AdminUserView> getAllUsers() {
        return userRepository.findAll().stream().map(user -> new AdminUserView(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole().name(),
                user.isBanned(),
                user.getCreatedAt()
        )).toList();
    }

    // this will help with user's Ban status
    public void toggleUserBan(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        );

        User targetUser = userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        );

        String currentAdminEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        // prevent self banning
        if (targetUser.getEmail().equals(currentAdminEmail)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are Can't ban yourself");
        }

        if (targetUser.getRole() == Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are Can't ban other admins");
        }

        user.setBanned(!user.isBanned());
        userRepository.save(user);
    }

    // View pending providers
    public List<AdminPendingProvider> getPendingProviders() {
        return serviceProviderRepository.findPendingProvidersForAdmin();
    }

    // Approve provider
    public void approveProvider(Long providerId) {
        ServiceProvider provider = serviceProviderRepository.findById(providerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Provider not found"
                ));

        if (provider.getVerificationStatus() != VerificationStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Provider is not pending verification"
            );
        }

        if (!provider.isProfileComplete()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Provider is not complete profile"
            );
        }

        provider.setVerificationStatus(VerificationStatus.APPROVED);
        provider.setIsVerified(true);
        provider.setIsAvailable(true);
        provider.setRejectionReason(null);

        serviceProviderRepository.save(provider);
    }

    // Reject provider and clear all provider-submitted data
    public void rejectProvider(Long providerId, String reason) {
        ServiceProvider provider = serviceProviderRepository.findById(providerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Provider not found"
                ));

        if (provider.getVerificationStatus() != VerificationStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Provider is not pending verification"
            );
        }

        // delete provider uploaded files
        deleteFileIfExists(provider.getIdFrontUrl());
        deleteFileIfExists(provider.getIdBackUrl());
        deleteFileIfExists(provider.getWorkPdfUrl());

        // get all provider services
        List<ProviderService> providerServices =
                providerServiceRepository.findByServiceProvider_ServiceProviderId(
                        provider.getServiceProviderId()
                );

        // booked services cannot be physically deleted because bookings reference them
        Set<Long> bookedServiceIds = Set.copyOf(
                providerServiceRepository.findBookedProviderServiceIds(
                        provider.getServiceProviderId()
                )
        );

        for (ProviderService ps : providerServices) {
            // delete qualification file from disk
            deleteFileIfExists(ps.getQualificationDoc());

            if (bookedServiceIds.contains(ps.getId())) {
                // keep DB row, but disable it
                ps.setIsActive(false);
                ps.setVerificationStatus(VerificationStatus.REJECTED);
                ps.setQualificationDoc(null);
            } else {
                // no booking reference -> safe to delete
                providerServiceRepository.delete(ps);
            }
        }

        // remove user addresses directly from DB
        User user = provider.getUser();
        addressRepository.deleteByUserId(user.getId());
        entityManager.flush();

        // clear provider profile data
        provider.setSkill(null);
        provider.setExperience(null);
        provider.setLicenseNumber(null);
        provider.setDescription(null);
        provider.setCity(null);
        provider.setLatitude(null);
        provider.setLongitude(null);
        provider.setIdFrontUrl(null);
        provider.setIdBackUrl(null);
        provider.setWorkPdfUrl(null);
        provider.setRating(null);

        // update provider state
        provider.setVerificationStatus(VerificationStatus.REJECTED);
        provider.setIsVerified(false);
        provider.setIsAvailable(false);
        provider.setRejectionReason(reason);

        serviceProviderRepository.save(provider);
    }

    private void deleteFileIfExists(String path) {
        if (path == null || path.isBlank()) {
            return;
        }

        try {
            Path filePath = Paths.get(path);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + path, e);
        }
    }

    // get all categories to a list
    public List<ServiceCategoryResponse> gatAllCategories() {
        return serviceCategoryRepository.findAll().stream()
                .map(category -> new ServiceCategoryResponse(
                        category.getCategoryId(),
                        category.getName()
                )).toList();
    }

    // admin can create categories
    public void createCategory(ServiceCategoryRequest request) {
        if (serviceCategoryRepository.findByNameIgnoreCase(request.getName()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Service category already exists"
            );
        }

        ServiceCategory category = new ServiceCategory();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        serviceCategoryRepository.save(category);
    }

    // edit categories by id
    public void updateCategory(Long id, @Valid ServiceCategoryRequest req) {
        ServiceCategory category = serviceCategoryRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found")
        );

        if (!category.getName().equalsIgnoreCase(req.getName()) &&
                serviceCategoryRepository.findByNameIgnoreCase(req.getName()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Service category already exists"
            );
        }

        category.setName(req.getName());
        category.setDescription(req.getDescription());
        serviceCategoryRepository.save(category);
    }

    // delete categories by id
    public void deleteCategory(Long id) {
        ServiceCategory category = serviceCategoryRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found")
        );

        if (!category.getServices().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Bad Request"
            );
        }

        serviceCategoryRepository.delete(category);
    }

    public AdminProviderDetailResponse getProviderDetails(Long providerId) {
        ServiceProvider provider = serviceProviderRepository.findById(providerId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Provider not found")
        );

        User user = provider.getUser();

        return AdminProviderDetailResponse.builder()
                .providerId(provider.getServiceProviderId())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .skill(provider.getSkill())
                .experience(provider.getExperience())
                .licenceNumber(provider.getLicenseNumber())
                .description(provider.getDescription())
                .city(provider.getCity())
                .profileImage(user.getProfilePic())
                .workPdf(provider.getWorkPdfUrl())
                .idFrontUrl(provider.getIdFrontUrl())
                .idBackUrl(provider.getIdBackUrl())
                .verificationStatus(provider.getVerificationStatus())
                .isProfileComplete(provider.isProfileComplete())
                .joinedAt(user.getCreatedAt())
                .build();
    }
}