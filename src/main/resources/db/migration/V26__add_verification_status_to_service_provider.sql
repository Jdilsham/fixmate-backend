ALTER TABLE service_provider
    ADD COLUMN verification_status VARCHAR(20) DEFAULT 'NOT_SUBMITTED';
