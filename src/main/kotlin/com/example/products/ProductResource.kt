package com.example.products

import com.example.products.dto.ProductCreateRequest
import com.example.products.dto.ProductDto
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class ProductResource(private val service: ProductService) {

    @GET
    fun list(): List<ProductDto> = service.findAll().map { it.toDto() }

    @GET
    @Path("/{id}")
    fun get(@PathParam("id") id: Long): Response {
        val product = service.findById(id)
            ?: return Response.status(Response.Status.NOT_FOUND).build()
        return Response.ok(product.toDto()).build()
    }

    @POST
    fun create(request: ProductCreateRequest): Response {
        return try {
            val created = service.create(name = request.name, price = request.price)
            Response.status(Response.Status.CREATED).entity(created.toDto()).build()
        } catch (e: IllegalArgumentException) {
            Response.status(Response.Status.BAD_REQUEST)
                .entity(mapOf("error" to (e.message ?: "Invalid request")))
                .build()
        }
    }
}

private fun Product.toDto() = ProductDto(id = id, name = name, price = price)
