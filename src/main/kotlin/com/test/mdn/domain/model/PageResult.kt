package com.test.mdn.domain.model

data class PageResult<T>(
    val content: List<T>,     // data di halaman ini saja
    val totalFiltered: Int,   // jumlah data yang cocok dengan filter
    val totalAll: Long,       // jumlah SEMUA data tanpa filter
)
