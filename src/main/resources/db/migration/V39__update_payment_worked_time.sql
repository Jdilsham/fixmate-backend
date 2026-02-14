-- 1️⃣ Remove old column
ALTER TABLE payment
DROP COLUMN IF EXISTS worked_time;

-- 2️⃣ Add new column
ALTER TABLE payment
    ADD COLUMN worked_seconds BIGINT;