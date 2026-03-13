package com.fixmate.backend.dto.response;

import lombok.Data;

@Data
public class UserSummaryDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String phone;
}
