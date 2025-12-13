-- Add booking_id column to address table
ALTER TABLE address
    ADD COLUMN booking_id BIGINT;

-- Add foreign key constraint
ALTER TABLE address
    ADD CONSTRAINT fk_address_booking
        FOREIGN KEY (booking_id)
            REFERENCES booking(booking_id);
