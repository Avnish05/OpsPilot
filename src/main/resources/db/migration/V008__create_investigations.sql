CREATE TABLE investigations (
    id UUID PRIMARY KEY,
    incident_id UUID NOT NULL REFERENCES incidents(id),
    question VARCHAR(2000) NOT NULL,
    summary VARCHAR(4000) NOT NULL,
    probable_cause VARCHAR(4000) NOT NULL,
    confidence DOUBLE PRECISION NOT NULL CHECK (confidence >= 0 AND confidence <= 1),
    recommended_actions JSONB NOT NULL,
    limitations JSONB NOT NULL,
    created_at TIMESTAMPTZ NOT NULL
);
CREATE INDEX ix_investigations_incident_created_at ON investigations(incident_id, created_at DESC);
