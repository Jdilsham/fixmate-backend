package com.fixmate.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProfileUpdateReq {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String phone;

}
