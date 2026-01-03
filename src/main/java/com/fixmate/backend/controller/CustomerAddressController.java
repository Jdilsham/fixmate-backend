package com.fixmate.backend.controller;

import com.fixmate.backend.dto.request.AddressRequest;
import com.fixmate.backend.dto.response.AddressResponse;
import com.fixmate.backend.entity.User;
import com.fixmate.backend.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mapstruct.control.MappingControl;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer/addresses")
@RequiredArgsConstructor
public class CustomerAddressController {
    private final AddressService addressService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AddressResponse addAddress(
            Authentication authentication,
            @Valid @RequestBody AddressRequest request

            ){
        User user = (User)authentication.getPrincipal();
        return addressService.addProfileAddress(user.getId(), request);

    }
}
