package com.lolmeida.platform.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ObservabilityExtensionTest {
  @Test
  void generatesCorrelationIdWithoutConsumerScaffolding() {
    given()
        .when()
        .get("/probe")
        .then()
        .statusCode(200)
        .header("X-Request-ID", matchesPattern("[0-9a-f-]{36}"));
  }

  @Test
  void preservesValidCorrelationId() {
    given()
        .header("X-Request-ID", "req_01:valid")
        .when()
        .get("/probe")
        .then()
        .statusCode(200)
        .header("X-Request-ID", equalTo("req_01:valid"));
  }

  @Test
  void replacesInvalidCorrelationId() {
    given()
        .header("X-Request-ID", "invalid id")
        .when()
        .get("/probe")
        .then()
        .statusCode(200)
        .header("X-Request-ID", matchesPattern("[0-9a-f-]{36}"));
  }
}
