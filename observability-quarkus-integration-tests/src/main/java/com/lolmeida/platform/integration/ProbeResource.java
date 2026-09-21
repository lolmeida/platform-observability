package com.lolmeida.platform.integration;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/probe")
public class ProbeResource {
  @GET
  public String get() {
    return "ok";
  }

  @GET
  @Path("failure")
  public Response failure() {
    return Response.serverError().build();
  }
}
