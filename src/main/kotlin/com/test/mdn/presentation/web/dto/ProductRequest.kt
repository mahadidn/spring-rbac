package com.test.mdn.presentation.web.dto

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Digits
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class ProductRequest(

    @NotBlank(message = "Nama produk wajib diisi")
    @Size(max = 150, message = "Nama produk maksimal 150 karakter")
    val name: String,

    @NotBlank(message = "Kategori wajib diisi")
    @Size(max = 50, message = "Kategori maksimal 50 karakter")
    val category: String,

    @DecimalMin(value = "0.00", message = "Harga tidak boleh negatif")
    @Digits(integer = 13, fraction = 2, message = "Harga maksimal 13 digit dengan 2 angka desimal")
    val price: BigDecimal,

    @Min(value = 0, message = "Stok tidak boleh negatif")
    val stock: Int,
)