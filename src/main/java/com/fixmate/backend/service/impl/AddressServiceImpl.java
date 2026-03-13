package com.fixmate.backend.service.impl;

import com.fixmate.backend.dto.request.AddressRequest;
import com.fixmate.backend.dto.response.AddressResponse;
import com.fixmate.backend.entity.Address;
import com.fixmate.backend.entity.ServiceProvider;
import com.fixmate.backend.entity.User;
import com.fixmate.backend.repository.AddressRepository;
import com.fixmate.backend.repository.ServiceProviderRepository;
import com.fixmate.backend.repository.UserRepository;
import com.fixmate.backend.service.AddressService;
import com.fixmate.backend.service.ServiceProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Override
    public AddressResponse getProfileAddress(Long userId) {
        Address address = addressRepository.findByUserId(userId)
                .orElse(null);

        if (address == null) return null;

        return AddressResponse.builder()
                .id(address.getAddressId())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .province(address.getProvince())
                .city(address.getCity())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .build();
    }

    @Override
    public AddressResponse addProfileAddress(Long userId, AddressRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
                );

        Address address = addressRepository.findByUserId(userId)
                .orElse(null);

        if (address == null) {
            // CREATE
            address = new Address();
            address.setUser(user);
        }

        // UPDATE fields (common for create + update)
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setLatitude(request.getLatitude());
        address.setLongitude(request.getLongitude());

        Address saved = addressRepository.save(address);

        return mapToResponse(saved);
    }

    @Override
    public AddressResponse updateProfileAddress(Long userId, AddressRequest request) {
        Address address = addressRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found")
                );

        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setLatitude(request.getLatitude());
        address.setLongitude(request.getLongitude());

        Address updated = addressRepository.save(address);

        return mapToResponse(updated);
    }

    private AddressResponse mapToResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getAddressId())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .province(address.getProvince())
                .city(address.getCity())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .build();
    }
}
