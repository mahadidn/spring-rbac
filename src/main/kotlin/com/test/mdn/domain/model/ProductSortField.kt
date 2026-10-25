package com.test.mdn.domain.model

/**
 * Daftar kolom yang BOLEH dipakai untuk sort.
 * apiName = nama yang dikirim client di parameter ?sort=
 */
enum class ProductSortField(val apiName: String) {
    ID("id"),
    NAME("name"),
    CATEGORY("category"),
    PRICE("price"),
    STOCK("stock"),
    CREATED_AT("createdAt"),
    ;

    companion object {
        fun fromApiName(apiName: String): ProductSortField? {
            return entries.firstOrNull { sortField -> sortField.apiName == apiName }
        }
    }
}