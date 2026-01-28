package com.fixmate.backend.dto.response;

import com.fixmate.backend.entity.Booking;
import com.fixmate.backend.enums.BookingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerBookingResponse {

    private Long bookingId;
    private String serviceName;
    private String providerName;

    private String customerName;
    private String phone;
    private String address;
    private String city;

    private BookingStatus status;
    private BigDecimal amount;
    private LocalDateTime scheduledAt;
    private String pricingType;
    private String description;

    private String rejectionReason;
    private LocalDateTime rejectedAt;


    public static CustomerBookingResponse from(@NotNull Booking booking) {
        return CustomerBookingResponse.builder()
                .bookingId(booking.getBookingId())
                .serviceName(
                        booking.getProviderService()
                                .getService()
                                .getTitle()
                )
                .providerName(
                        booking.getServiceProvider() != null
                                ? booking.getServiceProvider().getUser().getFirstName() + " " +
                                booking.getServiceProvider().getUser().getLastName()
                                : "Not assigned"
                )
                .customerName(
                        booking.getUser().getFirstName() + " " +
                                booking.getUser().getLastName()
                )
                .phone(booking.getContactInfo().getPhone())
                .address(booking.getContactInfo().getAddress())
                .city(booking.getContactInfo().getCity())
                .status(booking.getStatus())
                .amount(booking.getTotalPrice())
                .scheduledAt(
                        booking.getScheduledAt()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime()
                )

                .pricingType(booking.getPricingType().name())
                .description(booking.getDescription())

                .rejectionReason(booking.getRejectionReason())
                .rejectedAt(booking.getRejectedAt())


                .build();
    }
}

