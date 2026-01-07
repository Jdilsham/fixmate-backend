package com.fixmate.backend.dto.response;

public class UserMeResponse {

    private String email;
    private String firstName;
    private String lastName;
    private String role;

    public UserMeResponse(String email, String firstName, String lastName, String role) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getRole() {
        return role;
    }
}
