package com.fixmate.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

public class BookingRejectRequest {

    @NotBlank(message = "Rejection reason is required")
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
