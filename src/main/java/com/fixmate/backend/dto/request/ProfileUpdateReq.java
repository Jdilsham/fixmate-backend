package com.fixmate.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
public class ProfileUpdateReq {

    @NotBlank
    private String skill;

    @NotBlank
    private String experience;

    private String profileImageUrl;

    private String address;
    private String city;
    private String description;
    private BigDecimal rating;

    private MultipartFile workPdf;
}
