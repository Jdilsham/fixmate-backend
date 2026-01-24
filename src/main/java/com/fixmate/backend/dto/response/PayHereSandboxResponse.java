package com.fixmate.backend.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class PayHereSandboxResponse {
    private String checkoutUrl;
    private Map<String, String> fields;
}
