CREATE TABLE incident_alerts (
    incident_id UUID NOT NULL REFERENCES incidents(id),
    alert_id UUID NOT NULL REFERENCES alerts(id),
    PRIMARY KEY (incident_id, alert_id),
    UNIQUE (alert_id)
);
