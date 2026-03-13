ALTER TABLE users
    ADD COLUMN otp_expires_at TIMESTAMPTZ,
ADD COLUMN last_otp_sent_at TIMESTAMPTZ;
