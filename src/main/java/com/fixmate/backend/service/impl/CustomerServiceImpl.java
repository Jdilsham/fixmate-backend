package com.fixmate.backend.service.impl;

import com.fixmate.backend.dto.request.ChangePasswordRequest;
import com.fixmate.backend.dto.request.CustomerUpdateReq;
import com.fixmate.backend.dto.response.CustomerProfileResponse;
import com.fixmate.backend.entity.*;
import com.fixmate.backend.mapper.CustomerMapper;
import com.fixmate.backend.repository.*;
import com.fixmate.backend.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;



import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl  implements CustomerService {
    private final UserRepository userRepository;
    private final CustomerMapper mapper;

    //get profile
    @Override
    @Transactional(readOnly = true)
    public CustomerProfileResponse getProfile(String email) {
       User user = getUserByEmail(email);
       return mapper.toProfileResponse(user);
    }

    //update profile
    @Override
    public CustomerProfileResponse updateProfile(String email, CustomerUpdateReq req){
        User user = getUserByEmail(email);
        mapper.updateCustomerFromReq(req,user);
        return mapper.toProfileResponse(userRepository.save(user));
    }




//===============================HELPERS========================================
    //Ensure user exists
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,"User not found"));
    }



}


