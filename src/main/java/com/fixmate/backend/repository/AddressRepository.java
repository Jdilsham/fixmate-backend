package com.fixmate.backend.repository;

import com.fixmate.backend.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address,Long> {

    //Fetch the most recently added address
    Optional<Address> findFirstByUserIdOrderByAddressIdDesc(Long userId);
    Optional<Address> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
