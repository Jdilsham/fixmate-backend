package com.fixmate.backend.mapper;

import com.fixmate.backend.dto.response.ProviderProfileDTO;
import com.fixmate.backend.dto.response.ProviderServiceResponseDTO;
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
    ProviderProfileDTO toProfileDTO(ServiceProvider provider);

    // MAP LIST OF PROVIDER SERVICES
    default List<ProviderServiceResponseDTO> mapProviderServices(
            Set<ProviderService> providerServices) {

        if (providerServices == null) {
            return List.of();
        }

        return providerServices.stream()
                .map(this::toProviderServiceDTO)
                .toList();
    }

    // MAP SINGLE PROVIDER SERVICE
    default ProviderServiceResponseDTO toProviderServiceDTO(
            ProviderService ps) {

        return ProviderServiceResponseDTO.builder()
                .providerServiceId(ps.getId())
                .serviceId(ps.getService().getServiceId())
                .title(ps.getService().getTitle())
                .basePrice(ps.getBasePrice())
                .estimatedTimeMinutes(ps.getEstimatedTimeMinutes())
                .description(ps.getDescription())
                .build();
    }
}
