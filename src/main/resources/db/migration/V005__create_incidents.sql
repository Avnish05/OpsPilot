CREATE TABLE incidents (
    id UUID PRIMARY KEY,
    service_id UUID NOT NULL REFERENCES monitored_services(id),
    fingerprint VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    severity VARCHAR(10) NOT NULL,
    title VARCHAR(200) NOT NULL,
    resolution_summary VARCHAR(2000),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_incidents_status CHECK (status IN ('OPEN','ACKNOWLEDGED','INVESTIGATING','RESOLVED')),
    CONSTRAINT ck_incidents_severity CHECK (severity IN ('INFO','WARNING','HIGH','CRITICAL'))
);
CREATE INDEX ix_incidents_service_status ON incidents(service_id, status, created_at DESC);
