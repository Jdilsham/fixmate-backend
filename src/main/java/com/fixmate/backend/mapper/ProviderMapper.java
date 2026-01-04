package com.fixmate.backend.mapper;

import com.fixmate.backend.dto.response.ProviderProfileDTO;
import com.fixmate.backend.entity.ServiceProvider;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProviderMapper {

    @Mapping(target = "fullName", expression = "java(sp.getUser().getFirstName() + \" \" + sp.getUser().getLastName())")
    ProviderProfileDTO toProfileDTO(ServiceProvider sp);
}

