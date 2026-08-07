CREATE TABLE deployments (
    id UUID PRIMARY KEY,
    service_id UUID NOT NULL REFERENCES monitored_services (id),
    release_version VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL,
    deployed_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT ck_deployments_status CHECK (
        status IN ('STARTED', 'SUCCEEDED', 'FAILED', 'ROLLED_BACK')
    )
);

CREATE INDEX ix_deployments_service_id_deployed_at
    ON deployments (service_id, deployed_at DESC, id DESC);
