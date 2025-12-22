package com.fixmate.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/provider/test")
public class ProviderTestController {

    @GetMapping
    public String providerOnly() {
        return "PROVIDER ACCESS OK";
    }
}

