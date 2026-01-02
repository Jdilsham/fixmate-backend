package com.fixmate.backend.mapper;

import com.fixmate.backend.dto.request.CustomerUpdateReq;
import com.fixmate.backend.dto.response.CustomerProfileResponse;
import com.fixmate.backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;



@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerProfileResponse toProfileResponse(User user);

    void updateCustomerFromReq(CustomerUpdateReq req, @MappingTarget User user);

}
