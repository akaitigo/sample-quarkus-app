package com.example.products

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

@QuarkusTest
class ProductResourceTest {

    @Test
    fun `POST products creates a new product and returns 201`() {
        given()
            .contentType(ContentType.JSON)
            .body("""{"name": "Apple", "price": 100}""")
            .`when`()
            .post("/products")
            .then()
            .statusCode(201)
            .body("name", equalTo("Apple"))
            .body("price", equalTo(100))
    }

    @Test
    fun `POST products with blank name returns 400`() {
        given()
            .contentType(ContentType.JSON)
            .body("""{"name": "", "price": 100}""")
            .`when`()
            .post("/products")
            .then()
            .statusCode(400)
    }

    @Test
    fun `POST products with negative price returns 400`() {
        given()
            .contentType(ContentType.JSON)
            .body("""{"name": "Banana", "price": -1}""")
            .`when`()
            .post("/products")
            .then()
            .statusCode(400)
    }

    @Test
    fun `GET products by id returns 404 when product does not exist`() {
        given()
            .`when`()
            .get("/products/99999")
            .then()
            .statusCode(404)
    }

    @Test
    fun `GET products by id returns 200 after creating a product`() {
        val createResponse = given()
            .contentType(ContentType.JSON)
            .body("""{"name": "Cherry", "price": 200}""")
            .`when`()
            .post("/products")
            .then()
            .statusCode(201)
            .extract()
            .response()

        val id = createResponse.jsonPath().getLong("id")

        given()
            .`when`()
            .get("/products/$id")
            .then()
            .statusCode(200)
            .body("name", equalTo("Cherry"))
            .body("price", equalTo(200))
    }
}
