package com.test.mdn.domain.repository

import com.test.mdn.domain.model.PageResult
import com.test.mdn.domain.model.Product
import com.test.mdn.domain.model.ProductSearchCriteria

interface ProductRepository {

    fun findPage(criteria: ProductSearchCriteria): PageResult<Product>

    fun findById(id: Long): Product?

    fun save(product: Product): Product

    fun deleteById(id: Long): Boolean
}