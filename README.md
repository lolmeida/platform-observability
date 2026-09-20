# platform-observability

Versioned observability contract and reusable Quarkus adapter for PEAH services.

## Modules

- `observability-contract`: v1 schema, examples and data policy; no Quarkus dependency.
- `observability-quarkus`: request correlation, safe structured events and application-owned failure logging.

Supported baseline: Quarkus `3.20.3`, Java `21`. Version `0.1.0` is immutable and contains no `SNAPSHOT` dependency.

## Consumer setup

Add `com.lolmeida.platform:platform-observability-quarkus:0.1.0` and configure the GitHub Packages Maven repository:

```xml
<repositories><repository><id>github</id><url>https://maven.pkg.github.com/lolmeida/platform-observability</url></repository></repositories>
```

Use a read-only `GITHUB_TOKEN` or GitHub App token with `packages: read`; keep credentials in Maven `settings.xml`, never in source. Consumer workflows need `permissions: {contents: read, packages: read}` and must pass the token to Maven authentication.

Configuration:

```properties
peah.observability.enabled=true
peah.observability.service=example-api
peah.observability.environment=prod
peah.observability.version=sha-abc123
peah.observability.http.slow-threshold-ms=1000
peah.observability.request-id.header=X-Request-ID
```

Production should enable the consumer's JSON backend (`quarkus-logging-json`). Dev/test remain readable unless the consumer chooses JSON. The adapter does not configure exporters, secrets, URLs, realms, metrics registries or endpoints.

## Build and release

```sh
./mvnw -B -ntp test
./mvnw -B -ntp spotless:check
./mvnw -B -ntp -DskipTests deploy
```

Publication is manual through the release workflow or a GitHub Release. Only immutable SemVer releases are accepted; snapshots are rejected. The workflow uses `packages: write` and `GITHUB_TOKEN`.

See [`observability-contract/docs/logging-contract-v1.md`](observability-contract/docs/logging-contract-v1.md) and [`docs/consumer-migration.md`](docs/consumer-migration.md).
