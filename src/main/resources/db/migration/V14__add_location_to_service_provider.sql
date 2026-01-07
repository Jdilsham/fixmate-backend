ALTER TABLE service_provider
    ADD COLUMN service_location  VARCHAR(255);

ALTER TABLE service_provider
    ADD COLUMN latitude NUMERIC(10, 7);

ALTER TABLE service_provider
    ADD COLUMN longitude NUMERIC(10, 7);
