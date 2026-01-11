CREATE TABLE provider_service (
                                  id BIGSERIAL PRIMARY KEY,

                                  service_provider_id BIGINT NOT NULL,
                                  service_id BIGINT NOT NULL,

                                  base_price DECIMAL(10,2) NOT NULL,
                                  description TEXT,
                                  estimated_time_minutes INT NOT NULL,
                                  is_active BOOLEAN DEFAULT TRUE,

                                  CONSTRAINT uq_provider_service UNIQUE (service_provider_id, service_id),

                                  CONSTRAINT fk_provider_service_provider
                                      FOREIGN KEY (service_provider_id)
                                          REFERENCES service_provider(service_provider_id)
                                          ON DELETE CASCADE,

                                  CONSTRAINT fk_provider_service_service
                                      FOREIGN KEY (service_id)
                                          REFERENCES service(service_id)
                                          ON DELETE CASCADE
);
