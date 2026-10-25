package com.test.mdn.application.usecase

import java.math.BigDecimal

data class ProductCommand(
    val name: String,
    val category: String,
    val price: BigDecimal,
    val stock: Int,
)
