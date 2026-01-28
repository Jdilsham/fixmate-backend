ALTER TABLE review
    ALTER COLUMN rating SET NOT NULL;

ALTER TABLE review
    ALTER COLUMN comment SET NOT NULL;

ALTER TABLE review
    ADD CONSTRAINT chk_review_rating_range
        CHECK (rating >= 1 AND rating <= 5);

ALTER TABLE review
    ADD CONSTRAINT uq_review_booking UNIQUE (booking_id);

CREATE INDEX idx_review_service_provider
    ON review(service_provider_id);

CREATE INDEX idx_review_booking
    ON review(booking_id);
