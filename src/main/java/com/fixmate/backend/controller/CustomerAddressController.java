package com.fixmate.backend.controller;

import com.fixmate.backend.dto.request.AddressRequest;
import com.fixmate.backend.dto.request.CustomerUpdateReq;
import com.fixmate.backend.dto.response.AddressResponse;
import com.fixmate.backend.dto.response.CustomerProfileResponse;
import com.fixmate.backend.entity.User;
import com.fixmate.backend.service.AddressService;
import com.fixmate.backend.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/customer/address")
@RequiredArgsConstructor
public class CustomerAddressController {

    private final AddressService addressService;

    private Long getUserId(Authentication auth) {
        return ((User) auth.getPrincipal()).getId();
    }

    @GetMapping
    public ResponseEntity<AddressResponse> getCustomerAddress(Authentication auth) {

        AddressResponse response =
                addressService.getProfileAddress(getUserId(auth));

        if (response == null) {
            return ResponseEntity.noContent().build(); // 204
        }

        return ResponseEntity.ok(response); // 200
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AddressResponse addCustomerAddress(
            Authentication auth,
            @Valid @RequestBody AddressRequest request
    ) {
        return addressService.addProfileAddress(getUserId(auth), request);
    }

    @PutMapping
    public AddressResponse updateCustomerAddress(
            Authentication auth,
            @Valid @RequestBody AddressRequest request
    ) {
        return addressService.updateProfileAddress(getUserId(auth), request);
    }
}
