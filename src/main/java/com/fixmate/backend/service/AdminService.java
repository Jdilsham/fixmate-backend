package com.fixmate.backend.service;

import com.fixmate.backend.dto.response.AdminDashboardStats;
import com.fixmate.backend.dto.response.AdminPendingProvider;
import com.fixmate.backend.dto.response.AdminUserView;
import com.fixmate.backend.entity.ServiceProvider;
import com.fixmate.backend.entity.User;
import com.fixmate.backend.enums.VerificationStatus;
import com.fixmate.backend.repository.BookingRepository;
import com.fixmate.backend.repository.ServiceProviderRepository;
import com.fixmate.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    private final ServiceProviderRepository serviceProviderRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;


//    this get the admin stats for the component in the dashboard
    public AdminDashboardStats getDashboardStats() {
        return new AdminDashboardStats(
                userRepository.count(),
                serviceProviderRepository.count(),
                serviceProviderRepository.findByIsVerifiedFalse().size(),
                bookingRepository.count(),
                bookingRepository.sumConfirmedAmounts(null)
        );
    }

//    this is used to list all the users in the users tab
    public List<AdminUserView> getAllUsers(){
        return userRepository.findAll().stream().map(user -> new AdminUserView(
                user.getId() , user.getFirstName(), user.getLastName(), user.getEmail() , user.getRole().name(),user.isBanned(), user.getCreatedAt()
        )).toList();
    }

//    this will help with user's Ban status
    public void toggleUserBan(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        );
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

        if (!provider.isProfileComplete()){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Provider is not complete profile"
            );
        }

        provider.setVerificationStatus(VerificationStatus.APPROVED);
        provider.setIsVerified(true);
        provider.setIsAvailable(true);
        serviceProviderRepository.save(provider);
    }


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

        provider.setVerificationStatus(VerificationStatus.REJECTED);
        provider.setIsVerified(false);
        provider.setIsAvailable(false);

        // optional: log / store rejection reason later
    }

}
