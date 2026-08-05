CREATE TABLE monitored_services (
                                    id UUID PRIMARY KEY,
                                    name VARCHAR(100) NOT NULL,
                                    description VARCHAR(500),
                                    environment VARCHAR(20) NOT NULL,
                                    owner_team VARCHAR(100) NOT NULL,
                                    active BOOLEAN NOT NULL DEFAULT TRUE,
                                    created_at TIMESTAMPTZ NOT NULL,
                                    updated_at TIMESTAMPTZ NOT NULL,
                                    version BIGINT NOT NULL DEFAULT 0,

                                    CONSTRAINT ck_monitored_services_environment
                                        CHECK (
                                            environment IN (
                                                            'DEVELOPMENT',
                                                            'STAGING',
                                                            'PRODUCTION'
                                                )
                                            )
);

CREATE UNIQUE INDEX ux_monitored_services_name_environment
    ON monitored_services (LOWER(name), environment);