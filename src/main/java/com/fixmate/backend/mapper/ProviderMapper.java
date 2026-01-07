package com.fixmate.backend.mapper;

import com.fixmate.backend.dto.response.ProviderProfileDTO;
import com.fixmate.backend.dto.response.ServiceResponseDTO;
import com.fixmate.backend.entity.ServiceProvider;
import com.fixmate.backend.entity.Services;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface ProviderMapper {

    @Mapping(target = "providerId", source = "serviceProviderId")
    @Mapping(target = "fullName", expression = "java(sp.getUser().getFirstName() + \" \" + sp.getUser().getLastName())")
    @Mapping(target = "services", expression = "java(mapServices(sp.getServices()))")
    ProviderProfileDTO toProfileDTO(ServiceProvider sp);

    @Mapping(target = "serviceId", source = "serviceId")
    //@Mapping(target = "categoryName", source = "category.name")
    ServiceResponseDTO toServiceDTO(Services service);

    default List<ServiceResponseDTO> mapServices(Set<Services> services) {
        return services.stream().map(this::toServiceDTO).toList();
    }
}
