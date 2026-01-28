ALTER TABLE booking
ADD COLUMN rejection_reason VARCHAR(500),
ADD COLUMN rejected_at TIMESTAMP;

ALTER TABLE booking
DROP COLUMN cancel_reason;