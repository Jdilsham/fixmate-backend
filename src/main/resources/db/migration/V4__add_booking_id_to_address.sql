-- 1. Add column (nullable first)
ALTER TABLE address
    ADD COLUMN booking_id BIGINT;

-- 2. Backfill existing data
UPDATE address
SET booking_id = (
    SELECT b.booking_id
    FROM booking b
    WHERE b.user_id = address.user_id
    LIMIT 1
    );

-- 3. Add foreign key
ALTER TABLE address
    ADD CONSTRAINT fk_address_booking
        FOREIGN KEY (booking_id)
            REFERENCES booking (booking_id);

-- 4. Enforce NOT NULL
ALTER TABLE address
    ALTER COLUMN booking_id SET NOT NULL;
