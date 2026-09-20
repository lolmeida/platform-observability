package com.lolmeida.platform.integration;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/probe")
public class ProbeResource {
  @GET
  public String get() {
    return "ok";
  }
}
