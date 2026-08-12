CREATE TABLE alerts (
    id UUID PRIMARY KEY,
    event_id UUID NOT NULL UNIQUE,
    service_id UUID NOT NULL REFERENCES monitored_services(id),
    source VARCHAR(20) NOT NULL,
    alert_type VARCHAR(30) NOT NULL,
    severity VARCHAR(10) NOT NULL,
    fingerprint VARCHAR(255) NOT NULL,
    title VARCHAR(200) NOT NULL,
    message VARCHAR(2000) NOT NULL,
    occurred_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_alerts_source CHECK (source IN ('MANUAL', 'SIMULATOR')),
    CONSTRAINT ck_alerts_type CHECK (alert_type IN ('HIGH_ERROR_RATE','HIGH_LATENCY','DATABASE_TIMEOUT','MEMORY_PRESSURE','AUTH_FAILURE_SPIKE','KAFKA_CONSUMER_LAG')),
    CONSTRAINT ck_alerts_severity CHECK (severity IN ('INFO','WARNING','HIGH','CRITICAL'))
);
CREATE INDEX ix_alerts_service_occurred_at ON alerts(service_id, occurred_at DESC, id DESC);
