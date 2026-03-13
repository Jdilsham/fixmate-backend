package com.fixmate.backend.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDashboardProfileHealthDTO {
    private boolean hasPhone;
    private boolean hasProfilePic;
    private int score; // 0-100
}