package com.fixmate.backend.mapper;

import com.fixmate.backend.dto.request.CustomerUpdateReq;
import com.fixmate.backend.dto.response.BookingResponse;
import com.fixmate.backend.dto.response.CustomerProfileResponse;
import com.fixmate.backend.entity.Booking;
import com.fixmate.backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;


@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerProfileResponse toProfileResponse(User user);

    void updateCustomerFromReq(CustomerUpdateReq req, @MappingTarget User user);

    @Mapping(source = "bookingId", target = "bookingId")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "service.title", target = "serviceName")
    @Mapping(source = "serviceProvider.user.firstName", target = "providerName")
    @Mapping(source = "scheduledAt", target = "scheduledAt")
    BookingResponse toBookingResponse(Booking booking);

    default LocalDateTime map(Instant instant) {
        if (instant == null) {
            return null;
        }
        // Convert using System Default Timezone
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}
