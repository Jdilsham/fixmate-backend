package com.fixmate.backend.service;

import com.fixmate.backend.dto.request.WantedPostRequest;
import com.fixmate.backend.dto.response.WantedPostResponse;
import com.fixmate.backend.entity.*;
import com.fixmate.backend.repository.*;
import com.fixmate.backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WantedService {
    private final WantedPostRepository wantedPostRepository;
    private final WantedApplicationRepository wantedApplicationRepository;
    private final ServiceProviderRepository providerRepository;

    @Transactional
    public WantedPostResponse createPost(WantedPostRequest req, User user) {
        WantedPost post = new WantedPost();
        post.setUser(user);
        post.setProfession(req.getProfession());
        post.setDescription(req.getDescription());
        post.setRequiredCount(req.getRequiredCount());
        post.setLocation(req.getLocation());
        WantedPost saved = wantedPostRepository.save(post);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<WantedPostResponse> getAllOpenPosts() {
        return wantedPostRepository.findByStatusOrderByCreatedAtDesc("OPEN")
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void applyToPost(Long postId, User user) {
        WantedPost post = wantedPostRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        // Find the provider profile associated with this user
        ServiceProvider provider = providerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found"));

        // Prevent duplicate applications
        if (wantedApplicationRepository.existsByWantedPostIdAndServiceProviderServiceProviderId(postId, provider.getServiceProviderId())) {
            throw new IllegalStateException("You have already applied for this work");
        }

        WantedApplication application = new WantedApplication(post, provider);
        wantedApplicationRepository.save(application);
    }

    private WantedPostResponse toResponse(WantedPost post) {
        return WantedPostResponse.builder()
                .id(post.getId())
                .profession(post.getProfession())
                .description(post.getDescription())
                .requiredCount(post.getRequiredCount())
                .location(post.getLocation())
                .currentJoined((long) post.getApplications().size())
                .status(post.getStatus())
                .build();
    }
}