package com.example.products

import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class ProductService(private val repository: ProductRepository) {

    fun findAll(): List<Product> = repository.findAll()

    fun findById(id: Long): Product? = repository.findById(id)

    fun create(name: String, price: Int): Product {
        require(name.isNotBlank()) { "Name must not be blank" }
        require(price >= 0) { "Price must be non-negative" }
        return repository.save(Product(id = 0L, name = name, price = price))
    }

    // NOTE: updatePrice is intentionally NOT implemented.
    // The workshop hands-on (§6) asks participants to add it.
    // See docs/course/hands-on.md for requirements.
}
