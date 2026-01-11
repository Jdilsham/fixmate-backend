package com.fixmate.backend.service.impl;

import com.fixmate.backend.dto.request.BookingRequest;
import com.fixmate.backend.dto.request.ChangePasswordRequest;
import com.fixmate.backend.dto.request.CustomerUpdateReq;
import com.fixmate.backend.dto.response.CustomerBookingResponse;
import com.fixmate.backend.dto.response.CustomerProfileResponse;
import com.fixmate.backend.entity.*;
import com.fixmate.backend.enums.BookingStatus;
import com.fixmate.backend.exception.InvalidPasswordException;
import com.fixmate.backend.exception.ResourceNotFoundException;
import com.fixmate.backend.mapper.CustomerMapper;
import com.fixmate.backend.repository.*;
import com.fixmate.backend.service.CustomerService;
import com.fixmate.backend.util.FileStorageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;



import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl  implements CustomerService {
    private final UserRepository userRepository;
    private final CustomerMapper mapper;
    private final BookingRepository bookingRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final ServiceRepository serviceRepository;
    private final PasswordEncoder passwordEncoder;
    private final AddressRepository addressRepository;
    private final FileStorageUtil fileStorageUtil;


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


    //change password
    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(() ->  new ResourceNotFoundException("User not found"));

        //verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())){
            throw new InvalidPasswordException("Invalid current password");
        }
        //confirm new password
        if (request.getConfirmationPassword() != null && !request.getNewPassword().equals(request.getConfirmationPassword())){
            throw new InvalidPasswordException("Confirmation password not match");
        }

        //encode and update new pasword
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);
    }

    @Override
    public String uploadProfileImage(MultipartFile  file) {
        User user = getCurrentUser();

        //validate and store file
        String imageUrl = fileStorageUtil.storeProfileImage(file, user.getId());

        //update user
        user.setProfilePic(imageUrl);
        userRepository.save(user);

        return imageUrl;

    }

//===============================HELPERS========================================
    //Ensure user exists
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,"User not found"));
    }

    private User getCurrentUser() {
        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (!(principal instanceof User user)) {
            throw new ResponseStatusException(UNAUTHORIZED, "Unauthorized");
        }

        return user;
    }




}


