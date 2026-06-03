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

    // NOTE: updatePrice is intentionally NOT implemented — it is the §6 instructor-demo target.
    //   Participants implement it in post-session self-study (docs/course/hands-on.md), not live.
    // The request DTO `dto/PriceUpdateRequest` is ALREADY provided (it carries the Jackson
    // single-property fix, so you don't need to touch it). Your task — match the existing
    // create()/get() patterns above:
    //   1. fun updatePrice(id: Long, newPrice: Int): Product?
    //        require(newPrice >= 0) { ... }      // 0円未満は IllegalArgumentException
    //        repository.findById(id) ?: return null   // 不存在は null（HTTP例外を投げない）
    //        return repository.save(product.copy(price = newPrice))
    //   2. ProductResource に PATCH /products/{id}/price を追加
    //        IllegalArgumentException → 400 / null → 404（get() と同じ流儀）
    //   3. テスト 3 ケース（200 / 400 / 404）
    // 詳細は docs/course/hands-on.md。
}
