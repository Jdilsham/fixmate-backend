package com.fixmate.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;

@Entity
@Table(name = "wanted_applications")
@Getter
@Setter
@NoArgsConstructor
public class WantedApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to the specific job advertisement
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wanted_post_id", nullable = false)
    private WantedPost wantedPost;

    // Link to the provider who applied
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private ServiceProvider serviceProvider;

    @Column(nullable = false)
    private Instant appliedAt = Instant.now();

    // Status could be 'PENDING', 'ACCEPTED', or 'REJECTED'
    @Column(nullable = false)
    private String status = "PENDING";

    public WantedApplication(WantedPost wantedPost, ServiceProvider serviceProvider) {
        this.wantedPost = wantedPost;
        this.serviceProvider = serviceProvider;
        this.appliedAt = Instant.now();
        this.status = "PENDING";
    }
}