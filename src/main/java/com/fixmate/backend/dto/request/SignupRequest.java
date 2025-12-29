package com.fixmate.backend.dto.request;

import com.fixmate.backend.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class SignupRequest {
    @NotBlank(message = "First name is required")
    private String firstName;
    @NotBlank(message = "Last name is required")
    private String lastName;
    @NotBlank(message = "Email is required")
    private String email;
    @NotBlank(message = "Phone Number required")
    private String phone;
    @NotBlank(message = "Password is required")
    private String password;
    @NotNull(message = "Role is required")
    private Role role;

}
