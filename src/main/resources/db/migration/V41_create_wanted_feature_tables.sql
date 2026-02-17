-- The job advertisement
CREATE TABLE wanted_posts (
                              post_id BIGINT PRIMARY KEY,
                              user_id BIGINT NOT NULL,
                              profession VARCHAR(100) NOT NULL,
                              description TEXT NOT NULL,
                              required_count INT NOT NULL DEFAULT 1,
                              location VARCHAR(255) NOT NULL,
                              status VARCHAR(50) DEFAULT 'OPEN',
                              created_at TIMESTAMP DEFAULT NOW(),
                              CONSTRAINT fk_wanted_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Tracking provider sign-ups
CREATE TABLE wanted_applications (
                                     application_id BIGINT PRIMARY KEY,
                                     wanted_post_id BIGINT NOT NULL,
                                     provider_id BIGINT NOT NULL,
                                     applied_at TIMESTAMP DEFAULT NOW(),
                                     status VARCHAR(50) DEFAULT 'PENDING',
                                     CONSTRAINT fk_wanted_post FOREIGN KEY (wanted_post_id) REFERENCES wanted_posts(id),
                                     CONSTRAINT fk_wanted_provider FOREIGN KEY (provider_id) REFERENCES service_provider(service_provider_id)
);