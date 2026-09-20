# platform-quarkus

Reusable Quarkus extensions for cross-cutting technical concerns. The first extension provides request correlation and sanitized structured HTTP events; it does not own domain rules, infrastructure, exporters, registries, secrets, Keycloak, databases or URLs.

## Modules

- `observability-contract`: v1 schema, examples and data policy; no Quarkus dependency.
- `platform-observability-quarkus`: runtime artifact for the Quarkus extension.
- `platform-observability-quarkus-deployment`: build-time processor; consumers do not declare it.
- `observability-quarkus-integration-tests`: minimal real Quarkus application tests.
- `platform-quarkus-bom`: optional platform version management.

Supported baseline: Quarkus `3.20.3`, Java `21`. Release `0.1.3` remains immutable and supported as the previous library; `0.2.0` is the extension release.

## Consumer setup

Add only the runtime artifact and configure the GitHub Packages Maven repository:

```xml
<dependency><groupId>com.lolmeida.platform</groupId><artifactId>platform-observability-quarkus</artifactId><version>0.2.0</version></dependency>
```

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

No `beans.xml`, provider subclass, Jandex or `quarkus.index-dependency` is required. Production should enable the consumer's JSON backend (`quarkus-logging-json`); the extension does not activate JSON logging or configure exporters, secrets, URLs, realms, metrics registries or endpoints. `0.2.0` can be rolled back to `0.1.3` without database or remote-state changes.

## Build and release

```sh
./mvnw -B -ntp test
./mvnw -B -ntp spotless:check
./mvnw -B -ntp package
```

Publication is manual through the release workflow or a GitHub Release. Only immutable SemVer releases are accepted; snapshots are rejected. The workflow uses `packages: write` and `GITHUB_TOKEN`.

See [`observability-contract/docs/logging-contract-v1.md`](observability-contract/docs/logging-contract-v1.md) and [`docs/consumer-migration.md`](docs/consumer-migration.md).
