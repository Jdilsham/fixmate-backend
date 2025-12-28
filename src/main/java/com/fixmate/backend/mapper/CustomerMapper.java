package com.fixmate.backend.mapper;

import com.fixmate.backend.dto.response.CustomerProfileResponse;
import com.fixmate.backend.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerProfileResponse toProfileResponse(User user);
}
