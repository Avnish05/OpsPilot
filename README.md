# OpsPilot

OpsPilot is an AI-assisted incident-management platform for operational teams. It is being built as a Java modular monolith: services are registered, deployments are recorded, alerts are correlated into incidents, and future AI investigations will help engineers understand probable causes and next actions.

> Current delivery: **Phase 6 complete** — alert and incident management, Kafka ingestion, and local AI investigations are available.

## What works today

- Register, retrieve, list, and update monitored services.
- Record deployments against an existing service.
- Retrieve a deployment and view paginated service deployment history.
- Register engineers, authenticate with RSA-signed JWTs, and retrieve the current user.
- Create alerts manually or ingest simulator alert events through Kafka; duplicate event IDs are ignored.
- Correlate alerts into incidents and manage their acknowledgement, investigation, and resolution lifecycle.
- Generate persisted AI investigation recommendations from trusted incident, alert, service, and deployment context.
- PostgreSQL schema management through Flyway.
- PostgreSQL Testcontainers integration tests.

## Tech stack

| Area | Technology |
| --- | --- |
| Runtime | Java 25, Spring Boot 4.1 |
| API | Spring MVC, Jakarta Validation, Problem Details |
| Persistence | PostgreSQL, Spring Data JPA, Flyway |
| Testing | JUnit 5, Mockito, Testcontainers |
| Messaging | Apache Kafka, Spring for Apache Kafka |
| Local AI | Ollama |
| Local infrastructure | Docker Compose |

## Quick start

### Prerequisites

- Java 25
- Docker Desktop (running)

### 1. Start PostgreSQL and Kafka

```bash
docker compose up -d
docker compose ps
```

### 2. Run the application

```bash
./mvnw spring-boot:run
```

The API starts at `http://localhost:8080`. Flyway applies the schema migrations automatically.

To enable the local-only simulator endpoints, start with the `local` profile:

```bash
SPRING_PROFILES_ACTIVE=local ./mvnw spring-boot:run
```

### 3. Check health

```bash
curl http://localhost:8080/actuator/health
```

Expected response:

```json
{"status":"UP"}
```

## API walkthrough

> All business endpoints require a Bearer token. Only health, registration, and login are public.

### Register and authenticate

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H 'Content-Type: application/json' \
  -d '{
    "email": "engineer@example.com",
    "password": "very-secure-password"
  }'
```

Registration always creates an `ENGINEER`. Log in to receive a 15-minute access token:

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{
    "email": "engineer@example.com",
    "password": "very-secure-password"
  }'
```

Copy `accessToken` into `TOKEN`, then use it with business endpoints:

```bash
TOKEN='your-jwt'
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/v1/auth/me
```

### Create a monitored service

```bash
curl -X POST http://localhost:8080/api/v1/services \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "payment-service",
    "description": "Processes customer payments",
    "environment": "PRODUCTION",
    "ownerTeam": "platform"
  }'
```

Copy the response `id` into `SERVICE_ID`:

```bash
SERVICE_ID='your-service-uuid'
```

### List monitored services

```bash
curl -H "Authorization: Bearer $TOKEN" "http://localhost:8080/api/v1/services?page=0&size=20"
```

### Update a monitored service

```bash
curl -X PATCH "http://localhost:8080/api/v1/services/$SERVICE_ID" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "ownerTeam": "payments-platform",
    "active": true
  }'
```

### Record a deployment

```bash
curl -X POST "http://localhost:8080/api/v1/services/$SERVICE_ID/deployments" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "releaseVersion": "2.4.1",
    "status": "SUCCEEDED",
    "deployedAt": "2026-08-07T05:55:00Z"
  }'
```

Deployment statuses: `STARTED`, `SUCCEEDED`, `FAILED`, `ROLLED_BACK`.

### View deployment history

```bash
curl -H "Authorization: Bearer $TOKEN" "http://localhost:8080/api/v1/services/$SERVICE_ID/deployments?page=0&size=20"
```

Results are paginated and sorted by `deployedAt` descending.

### Retrieve a deployment

```bash
DEPLOYMENT_ID='your-deployment-uuid'
curl -H "Authorization: Bearer $TOKEN" "http://localhost:8080/api/v1/deployments/$DEPLOYMENT_ID"
```

### Simulate an alert locally

With the `local` profile enabled, publish a Kafka alert for a registered service:

```bash
curl -X POST "http://localhost:8080/api/v1/simulator/scenarios/deployment-regression?serviceId=$SERVICE_ID" \
  -H "Authorization: Bearer $TOKEN"
```

The simulator returns `202 Accepted` and an event ID. Kafka preserves per-service ordering by using the service ID as the message key. Invalid events are retried twice and then sent to `ops.alerts.raw.v1-dlt`; duplicate event IDs are safely ignored.

### Generate an AI investigation

Install and run Ollama locally, then download the configured model:

```bash
ollama pull llama3.2
```

Create an investigation for an incident. Ollama receives structured, trusted context and can only provide analysis—it cannot change incident state.

```bash
INCIDENT_ID='your-incident-uuid'
curl -X POST "http://localhost:8080/api/v1/incidents/$INCIDENT_ID/investigations" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"question":"What is the most likely cause and next action?"}'
```

<details>
<summary><strong>API reference</strong></summary>

| Method | Endpoint | Success |
| --- | --- | --- |
| `POST` | `/api/v1/services` | `201 Created` |
| `POST` | `/api/v1/auth/register` | `201 Created` |
| `POST` | `/api/v1/auth/login` | `200 OK` |
| `GET` | `/api/v1/auth/me` | `200 OK` |
| `GET` | `/api/v1/services/{serviceId}` | `200 OK` |
| `GET` | `/api/v1/services?page=0&size=20` | `200 OK` |
| `PATCH` | `/api/v1/services/{serviceId}` | `200 OK` |
| `POST` | `/api/v1/services/{serviceId}/deployments` | `201 Created` |
| `GET` | `/api/v1/services/{serviceId}/deployments?page=0&size=20` | `200 OK` |
| `GET` | `/api/v1/deployments/{deploymentId}` | `200 OK` |
| `POST` | `/api/v1/alerts/manual` | `201 Created` |
| `GET` | `/api/v1/alerts` | `200 OK` |
| `GET` | `/api/v1/incidents` | `200 OK` |
| `POST` | `/api/v1/incidents/{incidentId}/acknowledge` | `200 OK` |
| `POST` | `/api/v1/incidents/{incidentId}/start-investigation` | `200 OK` |
| `POST` | `/api/v1/incidents/{incidentId}/resolve` | `200 OK` |
| `POST` | `/api/v1/simulator/alerts` | `202 Accepted` (local profile) |
| `POST` | `/api/v1/incidents/{incidentId}/investigations` | `201 Created` |
| `GET` | `/api/v1/incidents/{incidentId}/investigations` | `200 OK` |
| `GET` | `/api/v1/investigations/{investigationId}` | `200 OK` |

Common error responses use RFC 9457-style `application/problem+json` payloads:

- `400` — validation failure or malformed identifier
- `404` — service or deployment not found
- `409` — duplicate service name within the same environment

</details>

## Test the project

Docker must be running because repository integration tests start PostgreSQL with Testcontainers.

```bash
./mvnw clean test
```

## Architecture

OpsPilot uses a modular-monolith design. Each module follows four layers:

```text
module/
├── api/            HTTP controllers and request/response DTOs
├── application/    use cases and transaction boundaries
├── domain/         business models, rules, and repository ports
└── infrastructure/ JPA entities, Spring Data repositories, adapters
```

Current modules:

```text
com.opspilot
├── servicecatalog
├── deployment
└── shared
```

The modules do not access each other’s infrastructure. Deployment uses the Service Catalog application API to confirm that a referenced service exists.

## Database migrations

Flyway migrations live in [`src/main/resources/db/migration`](src/main/resources/db/migration):

| Migration | Purpose |
| --- | --- |
| `V001` | Monitored services |
| `V002` | Users — schema foundation for Phase 3 |
| `V003` | Deployments |
| `V004` | Alerts |
| `V005` | Incidents |
| `V006` | Incident-to-alert links |
| `V007` | Kafka processed-event idempotency |
| `V008` | AI investigations |

Never edit a migration that has been applied to a shared database. Add a new migration instead.

## Roadmap

- [x] Phase 1 — Service Catalog
- [x] Phase 2 — Deployments
- [x] Phase 3 — Security: registration, JWT login, roles, API protection
- [x] Phase 4 — Alerts and Incidents through REST
- [x] Phase 5 — Kafka and local alert simulator
- [x] Phase 6 — AI Investigation with Ollama

The detailed architecture and delivery guide is available in [OPSPILOT_CODEX_IMPLEMENTATION_GUIDE.md](docs/architecture/OPSPILOT_CODEX_IMPLEMENTATION_GUIDE.md).

## Development notes

- IDs are UUIDs and timestamps are UTC `Instant` values.
- JPA entities never cross the REST boundary.
- Database constraints and Java validation both protect business input.
- Pagination is required for collection endpoints.
- RSA keys are generated in memory for local development; restarting the app invalidates existing tokens. Production key storage is intentionally deferred and keys are never committed.
