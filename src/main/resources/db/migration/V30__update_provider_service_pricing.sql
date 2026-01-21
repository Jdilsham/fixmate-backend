ALTER TABLE provider_service
DROP COLUMN IF EXISTS fixed_price;

ALTER TABLE provider_service
    ADD COLUMN is_fixed_price BOOLEAN NOT NULL DEFAULT false;