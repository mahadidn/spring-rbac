package com.test.mdn.presentation.web.controller

import com.test.mdn.application.usecase.ProductCommand
import com.test.mdn.application.usecase.ProductUseCase
import com.test.mdn.domain.model.PageResult
import com.test.mdn.domain.model.Product
import com.test.mdn.domain.model.ProductSearchCriteria
import com.test.mdn.domain.model.ProductSortField
import com.test.mdn.domain.model.SortDirection
import com.test.mdn.presentation.web.dto.PaginationListResponse
import com.test.mdn.presentation.web.dto.ProductRequest
import com.test.mdn.presentation.web.dto.ProductResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/products")
class ProductController(
    private val productService: ProductUseCase,
) {

    // =====================================================
    // GET /api/products?search=baja&category=PLAT&page=1&limit=5&sort=price,desc
    // =====================================================
    @GetMapping
    fun getProducts(
        @RequestParam(required = false) search: String?,
        @RequestParam(required = false) category: String?,
        @RequestParam(defaultValue = "1") @Min(1) page: Int,
        @RequestParam(defaultValue = "10") @Min(1) @Max(100) limit: Int,
        @RequestParam(defaultValue = "id,asc") sort: String,
    ): PaginationListResponse<ProductResponse> {
        val pageIndex = page - 1

        val criteria = ProductSearchCriteria(
            search = search,
            category = category,
            pageIndex = pageIndex,
            size = limit,
            sortField = parseSortField(sort),
            sortDirection = parseSortDirection(sort),
        )

        val pageResult = productService.getProducts(criteria)

        return toPaginationResponse(pageResult, pageIndex, limit)
    }

    // =====================================================
    // GET /api/products/{id}
    // =====================================================
    @GetMapping("/{id}")
    fun getProduct(@PathVariable id: Long): ProductResponse {
        val product = productService.getProduct(id)
        return ProductResponse.fromDomain(product)
    }

    // =====================================================
    // POST /api/products
    // =====================================================
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createProduct(@Valid @RequestBody request: ProductRequest): ProductResponse {
        val createdProduct = productService.createProduct(toCommand(request))
        return ProductResponse.fromDomain(createdProduct)
    }

    // =====================================================
    // PUT /api/products/{id}
    // =====================================================
    @PutMapping("/{id}")
    fun updateProduct(
        @PathVariable id: Long,
        @Valid @RequestBody request: ProductRequest,
    ): ProductResponse {
        val updatedProduct = productService.updateProduct(id, toCommand(request))
        return ProductResponse.fromDomain(updatedProduct)
    }

    // =====================================================
    // DELETE /api/products/{id}
    // =====================================================
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteProduct(@PathVariable id: Long) {
        productService.deleteProduct(id)
    }

    // =====================================================
    // FUNGSI BANTU
    // =====================================================

    /** "price,desc" → ProductSortField.PRICE */
    private fun parseSortField(sort: String): ProductSortField {
        val fieldName = sort.split(",")[0].trim()

        return ProductSortField.fromApiName(fieldName)
            ?: throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Kolom sort '$fieldName' tidak didukung. Pilihan: " +
                        ProductSortField.entries.joinToString(", ") { sortField -> sortField.apiName },
            )
    }

    /** "price,desc" → SortDirection.DESC (default ASC kalau tidak ditulis) */
    private fun parseSortDirection(sort: String): SortDirection {
        val parts = sort.split(",")
        val directionText = if (parts.size > 1) parts[1].trim().lowercase() else "asc"

        return when (directionText) {
            "asc" -> SortDirection.ASC
            "desc" -> SortDirection.DESC
            else -> throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Arah sort '$directionText' tidak valid. Pilihan: asc, desc",
            )
        }
    }

    private fun toPaginationResponse(
        pageResult: PageResult<Product>,
        pageIndex: Int,
        size: Int,
    ): PaginationListResponse<ProductResponse> {
        val totalPages = Math.ceilDiv(pageResult.totalFiltered, size)

        return PaginationListResponse.from(
            content = pageResult.content.map { product -> ProductResponse.fromDomain(product) },
            totalPages = totalPages,
            currentPage = pageIndex,
            size = size,
            totalFiltered = pageResult.totalFiltered,
            totalAll = pageResult.totalAll,
            isFirst = pageIndex == 0,
            isLast = pageIndex + 1 >= totalPages,
            isEmpty = pageResult.content.isEmpty(),
        )
    }

    private fun toCommand(request: ProductRequest): ProductCommand {
        return ProductCommand(
            name = request.name.trim(),
            category = request.category.trim().uppercase(),
            price = request.price,
            stock = request.stock,
        )
    }
}