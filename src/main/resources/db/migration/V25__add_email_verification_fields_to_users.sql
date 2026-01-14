-- Add email verification fields to users table

ALTER TABLE users
    ADD COLUMN verification_code VARCHAR(6),
    ADD COLUMN is_verified BOOLEAN NOT NULL DEFAULT FALSE;
