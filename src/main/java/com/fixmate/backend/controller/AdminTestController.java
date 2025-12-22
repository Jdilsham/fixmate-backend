package com.fixmate.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/test")
public class AdminTestController {

    @GetMapping
    public String adminOnly() {
        return "ADMIN ACCESS OK";
    }
}
