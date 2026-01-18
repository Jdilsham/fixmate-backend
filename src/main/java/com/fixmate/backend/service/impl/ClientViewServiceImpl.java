package com.fixmate.backend.service.impl;

import com.fixmate.backend.dto.response.ServiceProviderCardDTO;
import com.fixmate.backend.entity.ServiceProvider;
import com.fixmate.backend.entity.User;
import com.fixmate.backend.repository.ServiceProviderRepository;
import com.fixmate.backend.service.ClientViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientViewServiceImpl implements ClientViewService {
    private final ServiceProviderRepository serviceProviderRepository;

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
}
