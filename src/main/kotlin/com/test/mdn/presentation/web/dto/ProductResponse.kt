package com.test.mdn.presentation.web.dto

import com.test.mdn.domain.model.Product
import java.math.BigDecimal
import java.time.OffsetDateTime

data class ProductResponse(
    val id: Long,
    val name: String,
    val category: String,
    val price: BigDecimal,
    val stock: Int,
    val createdAt: OffsetDateTime?,
    val updatedAt: OffsetDateTime?,
) {
    companion object {
        fun fromDomain(product: Product): ProductResponse {
            return ProductResponse(
                id = checkNotNull(product.id) { "Produk dari database pasti punya id" },
                name = product.name,
                category = product.category,
                price = product.price,
                stock = product.stock,
                createdAt = product.createdAt,
                updatedAt = product.updatedAt,
            )
        }
    }
}