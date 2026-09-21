# Extension guidelines

Platform modules provide reusable technical behavior only. Acceptable modules include observability, technical configuration and common resilience. Domain rules, Keycloak Admin API, tenants, business tables, domain models, service URLs and secrets are out of scope.

Each extension must keep runtime and deployment artifacts separate, preserve explicit consumer overrides, document build-time and runtime configuration, and include a real Quarkus application integration test. It must not impose exporters, registries, endpoints or infrastructure credentials.
