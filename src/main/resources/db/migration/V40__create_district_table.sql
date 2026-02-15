CREATE TABLE district (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(80) UNIQUE NOT NULL
);

INSERT INTO district(name) VALUES
('Colombo'),
('Gampaha'),
('Kalutara'),
('Kandy'),
('Matale'),
('Nuwara Eliya'),
('Galle'),
('Matara'),
('Hambantota'),
('Jaffna'),
('Kilinochchi'),
('Mannar'),
('Mullaitivu'),
('Vavuniya'),
('Puttalam'),
('Kurunegala'),
('Anuradhapura'),
('Polonnaruwa'),
('Badulla'),
('Moneragala'),
('Ratnapura'),
('Kegalle'),
('Trincomalee'),
('Batticaloa'),
('Ampara');

ALTER TABLE provider_service
    ADD COLUMN district_id BIGINT;

ALTER TABLE provider_service
    ADD CONSTRAINT fk_provider_service_district
        FOREIGN KEY (district_id) REFERENCES district(id);

CREATE INDEX idx_provider_service_district_id
    ON provider_service(district_id);