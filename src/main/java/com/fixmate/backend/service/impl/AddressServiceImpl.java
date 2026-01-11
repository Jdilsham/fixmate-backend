package com.fixmate.backend.service.impl;

import com.fixmate.backend.dto.request.AddressRequest;
import com.fixmate.backend.dto.response.AddressResponse;
import com.fixmate.backend.entity.Address;
import com.fixmate.backend.entity.User;
import com.fixmate.backend.repository.AddressRepository;
import com.fixmate.backend.repository.UserRepository;
import com.fixmate.backend.service.AddressService;
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
    public AddressResponse addProfileAddress(Long userId, AddressRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
                );
        Address address = new Address();
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setLatitude(request.getLatitude());
        address.setLongitude(request.getLongitude());

       // address.setBooking(null);
        address.setUser(user);

        Address saved =  addressRepository.save(address);

        return AddressResponse.builder()
                .id(saved.getAddressId())
                .addressLine1(saved.getAddressLine1())
                .addressLine2(saved.getAddressLine2())
                .province(saved.getProvince())
                .city(saved.getCity())
                .latitude(saved.getLatitude())
                .longitude(saved.getLongitude())
                .build();

    }

}
