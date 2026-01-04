ALTER TABLE payment
    ADD COLUMN provider_id BIGINT,
ADD COLUMN customer_id BIGINT,
ADD COLUMN worked_time VARCHAR(100);

ALTER TABLE payment
    ALTER COLUMN status DROP DEFAULT;

ALTER TABLE payment
    ADD CONSTRAINT fk_payment_provider FOREIGN KEY (provider_id)
        REFERENCES service_provider(service_provider_id);

ALTER TABLE payment
    ADD CONSTRAINT fk_payment_customer FOREIGN KEY (customer_id)
        REFERENCES users(id);
