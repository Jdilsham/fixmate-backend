package com.fixmate.backend.mapper;

import com.fixmate.backend.dto.response.ProviderProfileResponse;
import com.fixmate.backend.dto.response.ServiceDetailResponse;
import com.fixmate.backend.entity.Services;
import com.fixmate.backend.entity.ServiceProvider;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface ProviderMapper {

    @Mapping(target = "fullName",
            expression = "java(provider.getUser().getFirstName() + \" \" + provider.getUser().getLastName())")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "phone", source = "user.phone")
    @Mapping(target = "services", source = "services")
    ProviderProfileResponse toProfile(ServiceProvider provider);

    @Mapping(target = "categoryName", source = "category.name")
    ServiceDetailResponse toServiceDetail(Services service);

    List<ServiceDetailResponse> toServiceDetails(Set<Services> services);
}
