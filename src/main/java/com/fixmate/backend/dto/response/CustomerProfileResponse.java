package com.fixmate.backend.dto.response;


import lombok.Data;

@Data
public class CustomerProfileResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String profilePic;
}
