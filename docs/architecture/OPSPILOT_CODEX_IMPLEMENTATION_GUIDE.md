# OpsPilot — IntelliJ Codex Implementation Guide

> **Project:** OpsPilot — AI-Assisted Incident Management Platform  
> **Current state:** Spring Boot starts successfully, PostgreSQL 18.4 runs in Docker, Flyway migration `V001__create_monitored_services.sql` is applied, and `/actuator/health` returns `UP`.  
> **Stack:** Java 25, Spring Boot 4.1, PostgreSQL, Flyway, Spring Data JPA, Spring Security, Kafka, Spring AI, Ollama, Docker, JUnit 5, Mockito, Testcontainers.  
> **Architecture:** Modular monolith with boundaries that support later microservice extraction.

---

## 1. How to use this document

Use this file as the source of truth for IntelliJ Codex.

For every task:

1. Ask Codex to read this file.
2. Ask it to inspect the repository before editing.
3. Ask for a file-by-file plan.
4. Approve only one small task at a time.
5. Review the diff.
6. Run `./mvnw clean test`.
7. Commit only after understanding the change.

Never ask Codex to “build the entire project.” Small vertical slices are safer and easier to review.

---

## 2. Product goal

OpsPilot receives operational alerts, groups related alerts into incidents, tracks deployments, and uses AI to help engineers understand probable causes and next steps.

```text
Register service
  → record deployment
  → generate alert scenario
  → publish Kafka events
  → consume and store alerts
  → correlate into incident
  → request AI analysis
  → acknowledge and resolve incident
```

The project must demonstrate strong Java, Spring Boot, PostgreSQL, Spring Security, Kafka, AI integration, testing, observability, and clean architecture.

---

## 3. Initial non-goals

Do not add these until the core release is complete:

- Kubernetes
- Cloud services
- Real Datadog/PagerDuty integrations
- Autonomous remediation
- Redis
- Elasticsearch/OpenSearch
- Vector database or RAG
- React/Angular frontend
- WebSockets
- OAuth social login
- Multiple deployable microservices
- Multi-region or multi-tenant design

---

## 4. Current repository state

Expected structure:

```text
opspilot/
├── compose.yaml
├── pom.xml
├── mvnw
├── src/
│   ├── main/
│   │   ├── java/com/opspilot/OpsPilotApplication.java
│   │   └── resources/
│   │       ├── application.yaml
│   │       └── db/migration/
│   │           └── V001__create_monitored_services.sql
│   └── test/
└── target/
```

Infrastructure checks:

```bash
docker compose ps
curl http://localhost:8080/actuator/health
./mvnw clean test
```

These must remain green after every phase.

---

## 5. Architecture

### 5.1 Modular monolith

```text
com.opspilot
├── auth
├── servicecatalog
├── deployment
├── alert
├── incident
├── simulator
├── investigation
├── audit
└── shared
```

Each module uses:

```text
module/
├── api/
├── application/
├── domain/
└── infrastructure/
    └── persistence/
```

### 5.2 Responsibilities

**api**
- Controllers
- Request/response records
- HTTP validation
- API mapping

Must not contain business rules or repository calls.

**application**
- Use-case orchestration
- Transaction boundaries
- Commands and queries

Must not contain HTTP or JPA details.

**domain**
- Business models
- Enums
- State transitions
- Domain exceptions
- Repository interfaces

Should remain independent from Spring, JPA, Kafka, and HTTP.

**infrastructure**
- JPA entities
- Spring Data repositories
- Repository adapters
- Kafka adapters
- External clients

### 5.3 Dependency rules

Allowed:

```text
auth            → shared
servicecatalog  → shared
deployment      → servicecatalog public API, shared
alert           → servicecatalog public API, incident public API, shared
incident        → servicecatalog public API, shared
simulator       → alert publishing API, servicecatalog public API
investigation   → incident public API, deployment public API, shared
audit           → shared
```

Rules:

- Never import another module’s `infrastructure` package.
- Never access another module’s JPA repository.
- Never return a JPA entity through REST.
- Never create circular dependencies.
- Keep `shared` small and technical.

---

## 6. Coding standards

### Java

Use:

- Java records for immutable DTOs, commands, queries, and events.
- `UUID` for IDs.
- `Instant` for timestamps.
- `Clock` for testable current time.
- Constructor injection.
- `final` fields.
- Enums for controlled values.
- Explicit domain methods for state changes.

Avoid:

- Lombok
- Field injection
- Generic `RuntimeException`
- Public setters on domain models
- Mutable static state
- Large utility classes
- `Optional` as a field or method parameter

### Spring

Use:

- `@RestController`
- `@Service`
- `@Repository`
- `@Transactional` in application services
- `@Transactional(readOnly = true)` for reads
- Jakarta validation at API boundaries
- `ProblemDetail` for errors

Avoid:

- Controllers calling repositories directly
- External calls inside database transactions
- `ddl-auto=update`
- Custom security infrastructure when Spring provides a standard implementation

### JPA

Use:

- Separate domain model and JPA entity
- Package-private JPA entities
- `@Version` for optimistic locking
- PostgreSQL Testcontainers tests
- Pagination for unbounded lists

Avoid:

- Exposing entities
- `CascadeType.ALL` by default
- Eager collections
- H2 as a PostgreSQL substitute
- Editing an applied Flyway migration

### REST

Base path:

```text
/api/v1
```

Status codes:

- `201` creation
- `200` reads
- `204` successful command without body
- `400` validation
- `401` unauthenticated
- `403` forbidden
- `404` missing resource
- `409` duplicate or invalid state
- `503` unavailable external dependency

Use centralized `ProblemDetail` responses and never expose stack traces.

---

## 7. Database roadmap

Migrations:

```text
V001__create_monitored_services.sql
V002__create_users.sql
V003__create_deployments.sql
V004__create_alerts.sql
V005__create_incidents.sql
V006__create_incident_alerts.sql
V007__create_investigations.sql
V008__create_processed_events.sql
```

Core entities:

- User
- MonitoredService
- Deployment
- Alert
- Incident
- IncidentAlert
- Investigation
- ProcessedEvent

Database rules:

- IDs: `uuid`
- Timestamps: `timestamptz`
- Flexible metadata: `jsonb`
- Names: lowercase `snake_case`
- Constraints must exist in both Java validation and PostgreSQL where appropriate
- Never modify an applied migration; create a new one

---

## 8. Phase 1 — Service Catalog

### Goal

Implement:

```text
POST  /api/v1/services
GET   /api/v1/services/{serviceId}
GET   /api/v1/services
PATCH /api/v1/services/{serviceId}
```

### Package structure

```text
servicecatalog/
├── api/
│   ├── MonitoredServiceController.java
│   ├── CreateMonitoredServiceRequest.java
│   ├── UpdateMonitoredServiceRequest.java
│   ├── MonitoredServiceResponse.java
│   └── MonitoredServiceApiMapper.java
├── application/
│   ├── MonitoredServiceApplicationService.java
│   ├── MonitoredServiceQueryService.java
│   ├── CreateMonitoredServiceCommand.java
│   └── UpdateMonitoredServiceCommand.java
├── domain/
│   ├── MonitoredService.java
│   ├── MonitoredServiceRepository.java
│   ├── ServiceEnvironment.java
│   ├── ServiceAlreadyExistsException.java
│   └── MonitoredServiceNotFoundException.java
└── infrastructure/
    └── persistence/
        ├── MonitoredServiceJpaEntity.java
        ├── SpringDataMonitoredServiceRepository.java
        ├── JpaMonitoredServiceRepository.java
        └── MonitoredServicePersistenceMapper.java
```

### Validation

- `name`: required, max 100
- `description`: optional, max 500
- `environment`: required
- `ownerTeam`: required, max 100
- Duplicate `(lower(name), environment)` returns `409`

### Definition of Done

- All four endpoints work.
- No JPA entity is returned through REST.
- Duplicate detection exists in application code and DB constraint.
- Domain unit tests exist.
- Repository integration tests use PostgreSQL Testcontainers.
- Controller tests cover validation, duplicate, and not found.
- `./mvnw clean test` passes.

---

## 9. Phase 2 — Deployments

Endpoints:

```text
POST /api/v1/services/{serviceId}/deployments
GET  /api/v1/services/{serviceId}/deployments
GET  /api/v1/deployments/{deploymentId}
```

Statuses:

```text
STARTED
SUCCEEDED
FAILED
ROLLED_BACK
```

Rules:

- Deployment references an existing service.
- History is paginated and sorted by `deployedAt` descending.
- Unknown service returns `404`.
- Investigation module later uses a public application interface to query recent deployments.

---

## 10. Phase 3 — Security

Endpoints:

```text
POST /api/v1/auth/register
POST /api/v1/auth/login
GET  /api/v1/auth/me
```

Roles:

```text
ENGINEER
ADMIN
```

Rules:

- Registration always creates `ENGINEER`.
- Passwords use BCrypt.
- JWT is RSA-signed.
- Access token lifetime: 15 minutes.
- Use Spring Security OAuth2 Resource Server.
- Avoid a custom JWT filter unless a real limitation requires it.
- Public: auth register/login and health.
- Business endpoints require authentication.
- Admin operations require `ADMIN`.
- Never commit signing keys.

Security tests:

- Missing token
- Invalid token
- Expired token
- Wrong role
- Disabled user
- Role escalation attempt
- Password/hash never returned

---

## 11. Phase 4 — Alerts and Incidents through REST

Alert types:

```text
HIGH_ERROR_RATE
HIGH_LATENCY
DATABASE_TIMEOUT
MEMORY_PRESSURE
AUTH_FAILURE_SPIKE
KAFKA_CONSUMER_LAG
```

Severity:

```text
INFO
WARNING
HIGH
CRITICAL
```

Incident status:

```text
OPEN
ACKNOWLEDGED
INVESTIGATING
RESOLVED
```

Endpoints:

```text
POST /api/v1/alerts/manual
GET  /api/v1/alerts
GET  /api/v1/alerts/{alertId}
GET  /api/v1/incidents
GET  /api/v1/incidents/{incidentId}
POST /api/v1/incidents/{incidentId}/acknowledge
POST /api/v1/incidents/{incidentId}/start-investigation
POST /api/v1/incidents/{incidentId}/resolve
```

Rules:

- Unique alert event ID.
- Deterministic fingerprinting.
- Deterministic correlation.
- Incident severity is the highest linked-alert severity.
- Resolved incidents cannot reopen in version one.
- Resolution requires a summary.
- Invalid state transitions return `409`.
- Use optimistic locking.

---

## 12. Phase 5 — Kafka and Simulator

Topics:

```text
ops.alerts.raw.v1
ops.alerts.raw.v1-dlt
```

Message key:

```text
serviceId
```

Event envelope:

```json
{
  "eventId": "uuid",
  "eventType": "alert.received",
  "eventVersion": 1,
  "occurredAt": "2026-08-05T06:00:00Z",
  "producer": "opspilot-simulator",
  "correlationId": "uuid",
  "payload": {
    "serviceId": "uuid",
    "source": "SIMULATOR",
    "alertType": "HIGH_ERROR_RATE",
    "severity": "CRITICAL",
    "fingerprint": "payment-service:http-500",
    "title": "HTTP 500 error rate increased",
    "message": "HTTP 500 responses exceeded the threshold",
    "attributes": {
      "errorRate": 0.24
    }
  }
}
```

Consumer flow:

```text
Receive
→ validate
→ check processed_events
→ store alert
→ correlate incident
→ store processed event
→ commit
→ acknowledge Kafka record
```

Delivery claim:

```text
At-least-once delivery with idempotent processing
```

Do not claim exactly-once.

Simulator endpoints:

```text
POST /api/v1/simulator/scenarios/deployment-regression
POST /api/v1/simulator/scenarios/database-outage
POST /api/v1/simulator/scenarios/kafka-lag
POST /api/v1/simulator/alerts
```

Simulator is local-profile only.

---

## 13. Phase 6 — AI Investigation

Endpoints:

```text
POST /api/v1/incidents/{incidentId}/investigations
GET  /api/v1/incidents/{incidentId}/investigations
GET  /api/v1/investigations/{investigationId}
```

AI context:

- Incident details
- Ordered linked alerts
- Recent deployments
- Alert fingerprints
- Service details
- User question

Structured response:

```json
{
  "summary": "The incident began shortly after deployment 2.4.1.",
  "probableCause": "A regression may have increased database connection usage.",
  "confidence": 0.81,
  "recommendedActions": [
    {
      "action": "Compare connection pool configuration with the previous release.",
      "rationale": "Database timeouts started after the deployment.",
      "risk": "LOW"
    }
  ],
  "limitations": [
    "Database metrics were unavailable."
  ]
}
```

Rules:

- AI output is untrusted input.
- Validate JSON and confidence range.
- AI cannot change incident state.
- AI cannot access repositories directly.
- AI receives trusted context built by application services.
- AI call occurs outside a database transaction.
- One correction attempt for invalid JSON.
- Return `503` when Ollama is unavailable.
- Core APIs remain usable without AI.
- Do not assert exact model wording in tests.

---

## 14. Phase 7 — Hardening

Add:

- Correlation ID filter
- Structured logging
- Custom metrics
- PostgreSQL/Kafka/Ollama health checks
- JaCoCo
- Architecture tests
- CI workflow
- Load testing
- Security review
- README and diagrams

Metrics:

```text
opspilot.alerts.received
opspilot.alerts.duplicates
opspilot.alerts.rejected
opspilot.alerts.correlated
opspilot.incidents.created
opspilot.incidents.resolved
opspilot.ai.investigations
opspilot.ai.failures
opspilot.ai.duration
```

Avoid high-cardinality tags such as incident ID.

---

## 15. Testing strategy

### Unit tests

Test domain rules without Spring where possible:

- Creation and validation
- Incident transitions
- Severity escalation
- Correlation scoring
- Fingerprinting
- AI response validation

### Repository integration tests

Use PostgreSQL Testcontainers for:

- Flyway migrations
- Constraints
- Repository queries
- Optimistic locking
- JSONB
- Timestamp behavior
- Pagination

### Kafka integration tests

Use Kafka Testcontainers for:

- Serialization/deserialization
- Duplicate events
- Retry and DLT
- Multiple alerts forming one incident
- Ordering for same service key

### Controller tests

Test:

- Validation
- Status codes
- ProblemDetail
- Authentication and authorization
- JSON contracts

### AI tests

Separate:

- Context-construction tests
- JSON-validation tests
- Optional Ollama integration tests
- Scenario evaluation tests

Never assert exact AI sentences.

---

## 16. Exception handling

Create:

```text
com.opspilot.shared.api.GlobalExceptionHandler
```

Use `@RestControllerAdvice` and `ProblemDetail`.

Map:

```text
MethodArgumentNotValidException → 400
ConstraintViolationException    → 400
ResourceNotFoundException       → 404
DuplicateResourceException      → 409
InvalidStateTransitionException → 409
AccessDeniedException           → 403
AuthenticationException         → 401
AI unavailable                  → 503
Unexpected exception            → 500
```

Never expose stack traces or internal exception names.

---

## 17. Transaction rules

Correct database flow:

```text
Begin transaction
→ validate state
→ save aggregate
→ commit
```

AI flow:

```text
Create PENDING investigation
→ commit
→ call Ollama outside transaction
→ new transaction
→ save COMPLETED or FAILED
→ commit
```

Kafka flow:

```text
Store alert
→ create/update incident
→ store processed_event
→ commit
→ acknowledge Kafka record
```

---

## 18. Git workflow

Branches:

```text
main
phase/02-service-catalog
phase/03-deployments
phase/04-security
phase/05-alerts-incidents
phase/06-kafka
phase/07-ai
phase/08-hardening
```

Commit examples:

```text
feat(servicecatalog): add monitored service creation
feat(deployment): add deployment history
feat(alert): consume raw alert events
feat(incident): add deterministic correlation
feat(investigation): add local AI analysis
fix(security): reject expired JWT
test(alert): cover duplicate Kafka events
docs(architecture): document Kafka event contract
```

Before commit:

```bash
./mvnw clean test
git status
git diff
```

Never commit:

```text
.env
private keys
JWT keys
database passwords
target/
.idea/
```

---

## 19. Codex master prompt

Paste this at the start of a Codex session:

```text
You are helping me build OpsPilot, an AI-assisted production incident
management backend.

Before changing code:

1. Read docs/architecture/OPSPILOT_CODEX_IMPLEMENTATION_GUIDE.md.
2. Inspect the current repository structure.
3. Inspect pom.xml, application.yaml, Flyway migrations, and tests.
4. Explain the current phase and current project state.
5. Propose the smallest implementation plan.
6. Wait for approval before editing files.

Architecture rules:

- Java 25 and Spring Boot 4.1.
- Modular monolith.
- Package by business capability.
- Modules use api/application/domain/infrastructure.
- Controllers must not access repositories directly.
- JPA entities must never be returned through REST.
- Domain layer must not depend on JPA or HTTP.
- One module must not access another module's infrastructure package.
- Use constructor injection.
- Use Java records for DTOs and immutable messages.
- Use Instant, UUID, and Clock.
- Use Flyway for every schema change.
- Do not modify an applied migration.
- Use PostgreSQL Testcontainers for repository integration tests.
- Do not add Lombok or MapStruct.
- Do not add dependencies without explaining why.
- Do not add Kafka, security, or AI before its planned phase.
- Do not place secrets in code or committed configuration.
- Use ProblemDetail for REST errors.
- Run ./mvnw clean test after each task.

When writing code:

- Keep changes scoped to the requested task.
- Add tests with the implementation.
- Explain important design decisions.
- Report every file added or modified.
- Report commands I should run.
- Do not commit changes.
```

---

## 20. Immediate Codex prompt — inspect only

Use this now:

```text
Read docs/architecture/OPSPILOT_CODEX_IMPLEMENTATION_GUIDE.md.

We are beginning Phase 1: Service Catalog.

Do not edit anything yet.

Inspect the repository and present:

1. Current project state.
2. Existing dependencies.
3. Existing Flyway migrations.
4. Proposed package structure.
5. File-by-file implementation plan.
6. Unit, repository, and controller test plan.
7. Any risks or inconsistencies you detect.

Wait for my approval before writing code.
```

---

## 21. Phase 1 Codex implementation prompt

After approving the plan, use:

```text
We are starting Phase 1: Service Catalog.

Read docs/architecture/OPSPILOT_CODEX_IMPLEMENTATION_GUIDE.md before making changes.

Implement the complete monitored-service vertical slice.

Endpoints:

1. POST /api/v1/services
2. GET /api/v1/services/{serviceId}
3. GET /api/v1/services
4. PATCH /api/v1/services/{serviceId}

Required package structure:

com.opspilot.servicecatalog.api
com.opspilot.servicecatalog.application
com.opspilot.servicecatalog.domain
com.opspilot.servicecatalog.infrastructure.persistence

Requirements:

- ServiceEnvironment enum: DEVELOPMENT, STAGING, PRODUCTION.
- Framework-independent MonitoredService domain model.
- MonitoredServiceRepository domain interface.
- Package-private JPA entity.
- Package-private Spring Data repository.
- Persistence mapper.
- JPA repository adapter.
- Request and response Java records.
- Application service and query service.
- Clock injection for timestamps.
- Domain exceptions for not found and duplicate service.
- Centralized ProblemDetail handling if absent.
- Validate name, description, environment, and ownerTeam.
- Enforce uniqueness by name and environment.
- Return 201 for creation.
- Return 404 for unknown ID.
- Return 409 for duplicate name and environment.
- Never expose JPA entities.
- Do not add Lombok or MapStruct.
- Do not add security, Kafka, or AI.

Testing:

- Unit test domain creation and updates.
- Unit test duplicate detection.
- PostgreSQL Testcontainers repository integration test.
- Controller tests for creation, validation, duplicate conflict, and not found.
- Run ./mvnw clean test.

Before editing:

1. Inspect the current code.
2. Present the exact files to add or modify.
3. Identify any required dependency or test configuration changes.
4. Wait for approval.
```

---

## 22. Codex review prompt

After Codex generates code:

```text
Review the current uncommitted changes as a senior Java backend engineer.

Check for:

- Violations of OPSPILOT_CODEX_IMPLEMENTATION_GUIDE.md.
- Controllers accessing repositories.
- JPA entities exposed through REST.
- Incorrect transaction boundaries.
- Missing validation.
- Missing ProblemDetail handling.
- Cross-module infrastructure dependencies.
- Incorrect Optional usage.
- Public mutable state.
- Missing or brittle tests.
- N+1 query risk.
- Security vulnerabilities.
- Secrets in code.
- Unnecessary dependencies.
- Duplicate logic.
- Naming problems.
- Improper logging.
- Flyway migration issues.

Do not modify files yet.

Return:

1. Critical issues.
2. Important improvements.
3. Optional improvements.
4. Exact files involved.
5. Recommended fixes.
```

---

## 23. Test-failure prompt

```text
Analyze the failing Maven output.

Rules:

- Find the root cause, not only the top exception.
- Explain whether the problem is code, configuration, migration, Docker, or test setup.
- Do not disable tests.
- Do not replace PostgreSQL with H2.
- Do not remove Flyway validation.
- Do not weaken production configuration merely to pass tests.
- Propose the smallest correct fix.
- List files that need to change.
- Wait for approval before editing.
```

---

## 24. Definition of resume-ready

```text
[ ] Service catalog works
[ ] Deployments work
[ ] JWT authentication works
[ ] Role-based authorization works
[ ] Alerts and incidents work
[ ] Deterministic correlation works
[ ] Kafka ingestion works
[ ] Duplicate events are safely ignored
[ ] DLT behavior is tested
[ ] Simulator scenarios work
[ ] AI investigation works locally
[ ] AI output is structured and validated
[ ] PostgreSQL Testcontainers tests pass
[ ] Kafka Testcontainers tests pass
[ ] Architecture tests pass
[ ] CI passes
[ ] README is complete
[ ] Architecture diagram exists
[ ] Metrics are measured and documented
[ ] Five-minute demo is reproducible
```

---

## 25. Next action

1. Save this file at:

```text
docs/architecture/OPSPILOT_CODEX_IMPLEMENTATION_GUIDE.md
```

2. Give Codex the inspect-only prompt from Section 20.
3. Review its plan.
4. Approve the plan only if it follows the architecture.
5. Then give it the Phase 1 prompt from Section 21.
6. Review the diff and run:

```bash
./mvnw clean test
```
