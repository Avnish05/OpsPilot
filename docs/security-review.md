# Security review checklist

- Verify protected endpoints reject missing, malformed, expired, and cross-restart JWTs.
- Confirm passwords and tokens are never logged or returned by user APIs.
- Confirm database and Kafka credentials are supplied through environment variables in non-local environments.
- Review actuator exposure before deployment; do not expose health details publicly.
- Rotate persistent JWT signing keys and store them in a secret manager before production.
- Run dependency and container-image scanning in the deployment pipeline.
