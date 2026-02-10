package com.fixmate.backend.repository;

import com.fixmate.backend.entity.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, Long> {

//    boolean findByNameIgnoreCase(String name);
    Optional<ServiceCategory> findByNameIgnoreCase(String name);
}
