package com.fixmate.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "wanted_posts")
@Getter
@Setter
@NoArgsConstructor
public class WantedPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    private String profession;
    private String description;
    private Integer requiredCount;
    private String location;
    private String status = "OPEN";
    private Instant createdAt = Instant.now();

    @JsonIgnore
    @OneToMany(mappedBy = "wantedPost",cascade = CascadeType.ALL)
    private Set<WantedApplication> applications = new HashSet<>();
}
