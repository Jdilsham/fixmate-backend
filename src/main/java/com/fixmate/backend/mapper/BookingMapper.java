package com.fixmate.backend.mapper;

import com.fixmate.backend.dto.response.BookingResponseDTO;
import com.fixmate.backend.dto.response.UserSummaryDTO;
import com.fixmate.backend.entity.Booking;
import com.fixmate.backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    @Mapping(target = "status", expression = "java(booking.getStatus().name())")
    @Mapping(target = "user", source = "user", qualifiedByName = "toUserSummary")
    @Mapping(target = "cancelReason", source = "cancelReason")
    BookingResponseDTO toDto(Booking booking);

    List<BookingResponseDTO> toDtoList(List<Booking> bookings);

    @Named("toUserSummary")
    default UserSummaryDTO toUserSummary(User user) {
        if (user == null)
            return null;

        UserSummaryDTO dto = new UserSummaryDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhone(user.getPhone());
        return dto;
    }
}
