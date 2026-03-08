
package com.fixmate.backend.repository;

import com.fixmate.backend.entity.WantedPost;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WantedPostRepository extends JpaRepository<WantedPost, Long> {
    List<WantedPost> findByStatusOrderByCreatedAtDesc(String status);
}

