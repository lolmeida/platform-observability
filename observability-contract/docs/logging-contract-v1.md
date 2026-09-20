# Observability logging contract v1

Version `1.0` defines the JSON event envelope emitted by each application to `stdout`. A collector remains responsible for transport, retention and indexing; this project does not provide a central logging service.

## Envelope

Fields use camelCase. `timestamp`, `level`, `service`, `environment`, `version`, `logger`, `event` and `message` are emitted by the logging backend or adapter when available. HTTP events may include `requestId`, `traceId`, `spanId`, `httpMethod`, `httpPath`, `httpStatus`, `durationMs`, `outcome` and `errorCategory`.

`event` uses dot notation. `outcome` is one of `success`, `failure`, `rejected`, `cached`, or `stale`. Stable error categories are `timeout`, `upstream_4xx`, `upstream_5xx`, `authentication`, `authorization`, `validation`, `conflict`, and `unexpected`. Domain event names belong to the consumer (for example `keycloak.scope-fetch.failed`).

## Data policy

Never emit request or response bodies, authorization headers, cookies, tokens, passwords, client secrets, credentials, private keys, email addresses, usernames or other PII. Pass only bounded, non-sensitive metadata. Query parameters are not part of `httpPath`. The adapter rejects metadata keys containing `authorization`, `cookie`, `token`, `secret`, `password`, `credential` or `privateKey`, and rejects `email` and `username`.

## Compatibility

Within major version 1, fields may be added but existing fields cannot be renamed or removed. A rename, removal or incompatible enum change requires a new major contract. Consumers should tolerate unknown fields.

## Metrics and tracing

Metric names and tags use low-cardinality values. Never tag Prometheus metrics with realm, client ID, username or request ID. The adapter may provide helpers, but the consumer chooses domain metrics, registry activation, scrape endpoints and exporters. OpenTelemetry IDs are correlation fields only; exporters and endpoints remain consumer configuration.
