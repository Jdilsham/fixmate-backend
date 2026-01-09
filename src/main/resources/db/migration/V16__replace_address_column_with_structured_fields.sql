
-- Add new structured address columns
ALTER TABLE address
    ADD COLUMN address_line_1 VARCHAR(255) NOT NULL,
ADD COLUMN address_line_2 VARCHAR(255),
ADD COLUMN province VARCHAR(100) NOT NULL;
