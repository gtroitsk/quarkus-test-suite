package io.quarkus.ts.many.extensions;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/resource/panoramic")
public class ResourcePanoramic {
    @Inject
    ServicePanoramic service;

    @GET
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Hello process(@PathParam("name") String name) {
        return new Hello(service.process(name));
    }
}
