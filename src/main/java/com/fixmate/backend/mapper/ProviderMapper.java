package com.fixmate.backend.mapper;

import com.fixmate.backend.dto.response.ProviderProfileDTO;
import com.fixmate.backend.dto.response.ProviderServiceCardResponse;
import com.fixmate.backend.entity.ProviderService;
import com.fixmate.backend.entity.ServiceProvider;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface ProviderMapper {


    // MAIN PROFILE MAPPING
    @Mapping(target = "providerId", source = "serviceProviderId")
    @Mapping(target = "phone", source = "user.phone")
    @Mapping(target = "experience", source = "experience")
    @Mapping(
            target = "fullName",
            expression = "java(provider.getUser().getFirstName() + \" \" + provider.getUser().getLastName())"
    )
    @Mapping(
            target = "services",
            expression = "java(mapProviderServices(provider.getProviderServices()))"
    )

    @Mapping(target = "profileImage", source = "user.profilePic")

    @Mapping(target = "idFrontUrl", source = "idFrontUrl")
    @Mapping(target = "idBackUrl", source = "idBackUrl")
    @Mapping(target = "workPdfUrl", source = "workPdfUrl")

    ProviderProfileDTO toProfileDTO(ServiceProvider provider);

    // MAP LIST OF PROVIDER SERVICES
    default List<ProviderServiceCardResponse> mapProviderServices(
            Set<ProviderService> providerServices) {

        if (providerServices == null) {
            return List.of();
        }

        return providerServices.stream()
                .map(this::toProviderServiceDTO)
                .toList();
    }

    // MAP SINGLE PROVIDER SERVICE
    default ProviderServiceCardResponse toProviderServiceDTO(
            ProviderService ps) {

        return ProviderServiceCardResponse.builder()
                .providerServiceId(ps.getId())
                .serviceId(ps.getService().getServiceId())
                .serviceTitle(ps.getService().getTitle())
                .categoryName(ps.getService().getCategory().getName())
                .description(ps.getDescription())
                .fixedPriceAvailable(ps.getIsFixedPrice())
                .hourlyRate(ps.getHourlyRate())
                .verificationStatus(ps.getVerificationStatus())
                .isActive(ps.getIsActive())
                .qualificationDoc(ps.getQualificationDoc())
                .build();
    }

}
