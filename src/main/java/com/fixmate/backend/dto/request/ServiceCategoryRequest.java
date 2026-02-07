package com.fixmate.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ServiceCategoryRequest {
    @NotBlank(message = "Category name is required")
    private String name;
    private String description;
}
