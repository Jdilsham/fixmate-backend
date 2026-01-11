ALTER TABLE booking
    ADD COLUMN provider_service_id BIGINT;

UPDATE booking b
SET provider_service_id = ps.id
    FROM provider_service ps
WHERE b.service_provider_id = ps.service_provider_id
  AND b.service_id = ps.service_id;
