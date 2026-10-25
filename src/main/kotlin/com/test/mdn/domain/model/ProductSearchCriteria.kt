package com.test.mdn.domain.model

data class ProductSearchCriteria(
    val search: String?,             // boleh null → tidak difilter
    val category: String?,           // boleh null → tidak difilter
    val pageIndex: Int,              // berbasis 0
    val size: Int,
    val sortField: ProductSortField,
    val sortDirection: SortDirection,
)
