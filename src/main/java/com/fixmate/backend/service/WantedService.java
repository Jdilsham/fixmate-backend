package com.fixmate.backend.service;

import com.fixmate.backend.dto.request.WantedPostRequest;
import com.fixmate.backend.entity.*;
import com.fixmate.backend.repository.*;
import com.fixmate.backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WantedService {
    private final WantedPostRepository wantedPostRepository;
    private final WantedApplicationRepository wantedApplicationRepository;
    private final ServiceProviderRepository providerRepository;

    public WantedPost createPost(WantedPostRequest req, User user) {
        WantedPost post = new WantedPost();
        post.setUser(user);
        post.setProfession(req.getProfession());
        post.setDescription(req.getDescription());
        post.setRequiredCount(req.getRequiredCount());
        post.setLocation(req.getLocation());
        return wantedPostRepository.save(post);
    }

    public List<WantedPost> getAllOpenPosts() {
        return wantedPostRepository.findByStatusOrderByCreatedAtDesc("OPEN");
    }

    public void applyToPost(Long postId, User user_id) {
        WantedPost post = wantedPostRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        // Find the provider profile associated with this user
        ServiceProvider provider = providerRepository.findByUserId(user_id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found"));

        // Prevent duplicate applications
        if (wantedApplicationRepository.existsByWantedPostIdAndServiceProviderServiceProviderId(postId, provider.getServiceProviderId())) {
            throw new IllegalStateException("You have already applied for this work");
        }

        WantedApplication application = new WantedApplication(post, provider);
        wantedApplicationRepository.save(application);
    }
}