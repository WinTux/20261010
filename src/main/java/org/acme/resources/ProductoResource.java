package org.acme.resources;

import java.util.Optional;

import org.acme.models.Producto;
import org.acme.repositories.ProductoRepository;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;
@Path("/producto")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductoResource {
     @Inject
    ProductoRepository repository;

    @GET
    @Path("/{nom}")
    public Response getByName(@PathParam("nom") String nombre) {
        try {
            Optional<Producto> producto = repository.findByName(nombre);
            return producto
                .map(p -> Response.ok(p).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
        } catch (Exception e) {
            return Response.serverError()
                .entity("Error tras agotar reintentos: " + e.getMessage())
                .build();
        }
    }
}
