ALTER TABLE provider_service
    ADD COLUMN fixed_price NUMERIC(10,2),
ADD COLUMN hourly_rate NUMERIC(10,2),
ADD COLUMN qualification_doc VARCHAR(255),
ADD COLUMN verification_status VARCHAR(20) NOT NULL DEFAULT 'PENDING';

