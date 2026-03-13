INSERT INTO provider_service (
    service_provider_id,
    service_id,
    base_price,
    description,
    estimated_time_minutes,
    is_active
)
SELECT
    sps.service_provider_id,
    sps.service_id,
    COALESCE(s.base_price, 0),
    s.description,
    60,         -- default estimate (adjust later per provider)
    TRUE
FROM service_provider_service sps
         JOIN service s ON s.service_id = sps.service_id;
