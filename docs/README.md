# Platform Quarkus documentation

This repository owns reusable Quarkus extensions. Consumers add a published
runtime artifact; they do not copy the extension, deployment processor or
logging schema into application repositories.

## Consumer path

| Need | Reference |
| --- | --- |
| Add or upgrade the observability extension | [Consumer migration](consumer-migration.md) |
| Understand the structured event schema and data policy | [Logging contract v1](../observability-contract/docs/logging-contract-v1.md) |
| Configure the extension boundary | [Extension guidelines](extension-guidelines.md) |
| Use the platform project and CI contracts | [Project standard](https://github.com/lolmeida/platform-project-template/blob/main/docs/architecture/project-standard.md) and [workflow standard](https://github.com/lolmeida/github-actions/blob/main/docs/workflow-standard.md) |

Use a read-only GitHub Packages token with `packages: read` only where Maven
resolves the dependency. Configure the consumer's own JSON logger, exporters,
metrics, secrets and URLs; the extension deliberately does not own them.

## Maintainer path

| Need | Reference |
| --- | --- |
| Repository ownership and operational continuity | [Repository continuity](repository-continuity.md) |
| Build and test the modules | [Root README](../README.md) |
| Change the event contract | [Logging contract v1](../observability-contract/docs/logging-contract-v1.md) |

Published releases are immutable SemVer versions. Consumer migration and release
notes must describe any compatibility impact.
