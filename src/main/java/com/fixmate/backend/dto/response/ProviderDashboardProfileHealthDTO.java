package com.fixmate.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ProviderDashboardProfileHealthDTO {
    private int completionPercent;

    private boolean hasProfilePic;
    private boolean hasPhone;
    private boolean hasAddress;

    private boolean hasSkill;
    private boolean hasExperience;
    private boolean hasDescription;

    private boolean hasIdFront;
    private boolean hasIdBack;
    private boolean hasWorkPdf;

    private int servicesCount;
    private boolean isAvailable;
    private boolean isVerified;
}