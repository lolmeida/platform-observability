# Consumer migration and rollback

1. Publish and verify a released `0.x.y` package before changing a consumer.
2. Add the dependency and GitHub Packages read permission; configure `peah.observability.*` explicitly in each environment.
3. Replace only generic request correlation, structured envelope and failure logging. Keep domain event names and application-owned mappers local.
4. Run the consumer's full Maven/frontend/Helm validation and verify request-ID propagation, `/q/metrics`, JSON output and immutable image metadata.
5. Roll back by reverting the dependency/configuration commit and restoring the previous application logging classes. No database, Keycloak or runtime state migration is required.

The library has no global exception mappers and no central logging service. It does not alter HTTP problem details, caching, discovery, realm state or secret resolution.
