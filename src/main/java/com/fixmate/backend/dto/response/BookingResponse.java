package com.fixmate.backend.dto.response;

import com.fixmate.backend.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingResponse {
    private Long bookingId;
    private BookingStatus status;

}
