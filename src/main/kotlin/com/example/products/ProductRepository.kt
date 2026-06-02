package com.example.products

import jakarta.enterprise.context.ApplicationScoped
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

@ApplicationScoped
class ProductRepository {
    private val store: MutableMap<Long, Product> = ConcurrentHashMap()
    private val nextId = AtomicLong(1)

    fun findAll(): List<Product> = store.values.sortedBy { it.id }

    fun findById(id: Long): Product? = store[id]

    fun save(product: Product): Product {
        val toStore = if (product.id == 0L) {
            product.copy(id = nextId.getAndIncrement())
        } else {
            product
        }
        store[toStore.id] = toStore
        return toStore
    }
}
