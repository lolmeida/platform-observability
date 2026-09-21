package com.lolmeida.platform.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.nullValue;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import java.util.Map;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestProfile(DisabledObservabilityExtensionTest.Profile.class)
class DisabledObservabilityExtensionTest {
  @Test
  void disablesCorrelationWhenConfiguredOff() {
    given().when().get("/probe").then().statusCode(200).header("X-Request-ID", nullValue());
  }

  public static class Profile implements QuarkusTestProfile {
    @Override
    public Map<String, String> getConfigOverrides() {
      return Map.of("peah.observability.enabled", "false");
    }
  }
}
