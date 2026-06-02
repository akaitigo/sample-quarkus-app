package com.example.products

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ProductRepositoryTest {

    private val repository = ProductRepository()

    @Test
    fun `save assigns a positive id when product id is zero`() {
        val saved = repository.save(Product(id = 0L, name = "Apple", price = 100))

        assertNotNull(saved)
        assertTrue(saved.id > 0L) { "id should be assigned a positive value, was ${saved.id}" }
        assertEquals("Apple", saved.name)
        assertEquals(100, saved.price)
    }

    @Test
    fun `save preserves id when product id is not zero`() {
        val saved = repository.save(Product(id = 42L, name = "Apple", price = 100))

        assertEquals(42L, saved.id)
    }

    @Test
    fun `findById returns null when product does not exist`() {
        assertNull(repository.findById(999L))
    }

    @Test
    fun `findAll returns saved products sorted by id`() {
        repository.save(Product(id = 0L, name = "Banana", price = 50))
        repository.save(Product(id = 0L, name = "Apple", price = 100))

        val all = repository.findAll()
        assertEquals(2, all.size)
        assertTrue(all[0].id < all[1].id) { "results should be sorted by id ascending" }
    }
}
