package com.example.products

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ProductServiceTest {

    private val repository = mockk<ProductRepository>()
    private val service = ProductService(repository)

    @Test
    fun `create with valid input saves and returns product`() {
        val savedProduct = Product(id = 1L, name = "Apple", price = 100)
        every { repository.save(any()) } returns savedProduct

        val result = service.create(name = "Apple", price = 100)

        assertEquals(savedProduct, result)
        verify { repository.save(Product(id = 0L, name = "Apple", price = 100)) }
    }

    @Test
    fun `create with blank name throws IllegalArgumentException`() {
        assertThrows<IllegalArgumentException> {
            service.create(name = "  ", price = 100)
        }
        verify(exactly = 0) { repository.save(any()) }
    }

    @Test
    fun `create with negative price throws IllegalArgumentException`() {
        assertThrows<IllegalArgumentException> {
            service.create(name = "Apple", price = -1)
        }
        verify(exactly = 0) { repository.save(any()) }
    }

    @Test
    fun `findById returns null when product does not exist`() {
        every { repository.findById(99L) } returns null

        assertNull(service.findById(99L))
    }

    @Test
    fun `findAll delegates to repository`() {
        val products = listOf(
            Product(id = 1L, name = "Apple", price = 100),
            Product(id = 2L, name = "Banana", price = 50),
        )
        every { repository.findAll() } returns products

        assertEquals(products, service.findAll())
    }
}
