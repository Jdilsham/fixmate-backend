-- Ensure role column exists and is correct
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS role VARCHAR(50) NOT NULL DEFAULT 'CUSTOMER';
