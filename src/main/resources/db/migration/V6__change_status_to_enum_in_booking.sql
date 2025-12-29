CREATE TYPE booking_status_enum AS ENUM (
    'PENDING',
    'ACCEPTED',
    'IN_PROGRESS',
    'COMPLETED',
    'CANCELLED',
    'REJECTED'
);

ALTER TABLE booking ALTER COLUMN status DROP DEFAULT;

ALTER TABLE booking
ALTER COLUMN status TYPE booking_status_enum
USING UPPER(status)::booking_status_enum;

ALTER TABLE booking ALTER COLUMN status SET DEFAULT 'PENDING'::booking_status_enum;