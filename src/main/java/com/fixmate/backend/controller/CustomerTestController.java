package com.fixmate.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer/test")
public class CustomerTestController {

    @GetMapping
    public String customerOnly() {
        return "CUSTOMER ACCESS OK";
    }
}
