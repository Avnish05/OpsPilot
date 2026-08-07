# OpsPilot

OpsPilot is an AI-assisted incident-management platform for operational teams. It is being built as a Java modular monolith: services are registered, deployments are recorded, alerts are correlated into incidents, and future AI investigations will help engineers understand probable causes and next actions.

> Current delivery: **Phase 2 complete** — Service Catalog and Deployment History are available.

## What works today

- Register, retrieve, list, and update monitored services.
- Record deployments against an existing service.
- Retrieve a deployment and view paginated service deployment history.
- PostgreSQL schema management through Flyway.
- PostgreSQL Testcontainers integration tests.

## Tech stack

| Area | Technology |
| --- | --- |
| Runtime | Java 25, Spring Boot 4.1 |
| API | Spring MVC, Jakarta Validation, Problem Details |
| Persistence | PostgreSQL, Spring Data JPA, Flyway |
| Testing | JUnit 5, Mockito, Testcontainers |
| Local infrastructure | Docker Compose |

## Quick start

### Prerequisites

- Java 25
- Docker Desktop (running)

### 1. Start PostgreSQL

```bash
docker compose up -d postgres
docker compose ps
```

### 2. Run the application

```bash
./mvnw spring-boot:run
```

The API starts at `http://localhost:8080`. Flyway applies the schema migrations automatically.

### 3. Check health

```bash
curl http://localhost:8080/actuator/health
```

Expected response:

```json
{"status":"UP"}
```

## API walkthrough

> Authentication is intentionally open during Phases 1–2. Phase 3 adds JWT-based security; do not expose this development configuration publicly.

### Create a monitored service

```bash
curl -X POST http://localhost:8080/api/v1/services \
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
curl "http://localhost:8080/api/v1/services?page=0&size=20"
```

### Update a monitored service

```bash
curl -X PATCH "http://localhost:8080/api/v1/services/$SERVICE_ID" \
  -H 'Content-Type: application/json' \
  -d '{
    "ownerTeam": "payments-platform",
    "active": true
  }'
```

### Record a deployment

```bash
curl -X POST "http://localhost:8080/api/v1/services/$SERVICE_ID/deployments" \
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
curl "http://localhost:8080/api/v1/services/$SERVICE_ID/deployments?page=0&size=20"
```

Results are paginated and sorted by `deployedAt` descending.

### Retrieve a deployment

```bash
DEPLOYMENT_ID='your-deployment-uuid'
curl "http://localhost:8080/api/v1/deployments/$DEPLOYMENT_ID"
```

<details>
<summary><strong>API reference</strong></summary>

| Method | Endpoint | Success |
| --- | --- | --- |
| `POST` | `/api/v1/services` | `201 Created` |
| `GET` | `/api/v1/services/{serviceId}` | `200 OK` |
| `GET` | `/api/v1/services?page=0&size=20` | `200 OK` |
| `PATCH` | `/api/v1/services/{serviceId}` | `200 OK` |
| `POST` | `/api/v1/services/{serviceId}/deployments` | `201 Created` |
| `GET` | `/api/v1/services/{serviceId}/deployments?page=0&size=20` | `200 OK` |
| `GET` | `/api/v1/deployments/{deploymentId}` | `200 OK` |

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

Never edit a migration that has been applied to a shared database. Add a new migration instead.

## Roadmap

- [x] Phase 1 — Service Catalog
- [x] Phase 2 — Deployments
- [ ] Phase 3 — Security: registration, JWT login, roles, API protection
- [ ] Phase 4 — Alerts and Incidents through REST
- [ ] Phase 5 — Kafka and local alert simulator
- [ ] Phase 6 — AI Investigation with Ollama

The detailed architecture and delivery guide is available in [OPSPILOT_CODEX_IMPLEMENTATION_GUIDE.md](docs/architecture/OPSPILOT_CODEX_IMPLEMENTATION_GUIDE.md).

## Development notes

- IDs are UUIDs and timestamps are UTC `Instant` values.
- JPA entities never cross the REST boundary.
- Database constraints and Java validation both protect business input.
- Pagination is required for collection endpoints.
