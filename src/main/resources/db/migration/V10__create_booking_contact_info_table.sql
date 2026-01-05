CREATE TABLE booking_contact_info (
         id BIGSERIAL PRIMARY KEY,

         booking_id BIGINT NOT NULL UNIQUE,

         address TEXT NOT NULL,
         city VARCHAR(255) NOT NULL,
         phone VARCHAR(20),

         latitude NUMERIC(10, 7),
         longitude NUMERIC(10, 7),

         CONSTRAINT fk_booking_contact_info_booking
            FOREIGN KEY (booking_id)
            REFERENCES booking (booking_id)
            ON DELETE CASCADE
);
