ALTER TABLE booking
    ALTER COLUMN provider_service_id SET NOT NULL;

ALTER TABLE booking
    ADD CONSTRAINT fk_booking_provider_service
        FOREIGN KEY (provider_service_id)
            REFERENCES provider_service(id);
