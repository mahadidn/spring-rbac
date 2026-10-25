package com.test.mdn.domain.exception

class ProductNotFoundException(
    id: Long,
) : RuntimeException("Produk dengan id $id tidak ditemukan")