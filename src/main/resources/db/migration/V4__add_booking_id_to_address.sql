ALTER TABLE address
    ADD COLUMN booking_id BIGINT;

ALTER TABLE address
    ADD CONSTRAINT fk_address_booking
        FOREIGN KEY (booking_id)
            REFERENCES booking(id);
