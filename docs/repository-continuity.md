# Repository continuity

`0.1.3` remains immutable at `com.lolmeida.platform:platform-observability-quarkus`. The `0.2.0` extension keeps that runtime coordinate and adds the deployment coordinate without requiring consumers to declare it.

The GitHub repository is canonical at `platform-quarkus`; GitHub redirects the former repository URL. Verify GitHub Packages Maven resolution for the old endpoint before changing consumer repository URLs. If the old endpoint is not reliable, publish `0.2.0` to the new endpoint and migrate consumers in separate pull requests. No consumer migration belongs in this change.
