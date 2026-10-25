package com.test.mdn.domain.model

import java.math.BigDecimal
import java.time.OffsetDateTime

data class Product(
    val id: Long? = null,
    val name: String,
    val category: String,
    val price: BigDecimal,
    val stock: Int,
    val createdAt: OffsetDateTime? = null,
    val updatedAt: OffsetDateTime? = null,
)
