package com.test.mdn.infrastructure.persistence

import com.test.mdn.domain.model.PageResult
import com.test.mdn.domain.model.Product
import com.test.mdn.domain.model.ProductSearchCriteria
import com.test.mdn.domain.model.ProductSortField
import com.test.mdn.domain.model.SortDirection
import com.test.mdn.domain.repository.ProductRepository
import com.test.mdn.infrastructure.persistence.jooq.inventory.Tables.PRODUCTS
import com.test.mdn.infrastructure.persistence.jooq.inventory.tables.records.ProductsRecord
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.SortField
import org.jooq.impl.DSL
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class JooqProductRepository(
    @Qualifier("inventory") private val dslContext: DSLContext,
) : ProductRepository {

    // =====================================================
    // LIST + FILTER DINAMIS + PAGINATION + SORT
    // =====================================================

    override fun findPage(criteria: ProductSearchCriteria): PageResult<Product> {
        val condition = buildCondition(criteria)

        // 1. Total semua data (tanpa filter apa pun)
        val totalAll = dslContext.fetchCount(PRODUCTS)

        // 2. Total data yang cocok dengan filter
        val totalFiltered = dslContext.fetchCount(PRODUCTS, condition)

        // 3. Ambil data untuk halaman ini saja
        val offset = criteria.pageIndex.toLong() * criteria.size

        val content = dslContext
            .selectFrom(PRODUCTS)
            .where(condition)
            .orderBy(
                toSortField(criteria.sortField, criteria.sortDirection),
                PRODUCTS.ID.asc(),
            )
            .limit(criteria.size)
            .offset(offset)
            .fetch()
            .map { productsRecord -> toProduct(productsRecord) }

        return PageResult(
            content = content,
            totalFiltered = totalFiltered,
            totalAll = totalAll.toLong(),
        )
    }

    /**
     * ★ Versi jOOQ dari pola CI3:
     *   if (isset($search)) { $this->db->like(...); }
     */
    private fun buildCondition(criteria: ProductSearchCriteria): Condition {
        // Mulai dari "tidak ada syarat"
        var condition: Condition = DSL.noCondition()

        // Kalau search diisi → cari di nama ATAU kategori (ILIKE '%...%')
        val search = criteria.search
        if (!search.isNullOrBlank()) {
            condition = condition.and(
                PRODUCTS.NAME.containsIgnoreCase(search)
                    .or(PRODUCTS.CATEGORY.containsIgnoreCase(search)),
            )
        }

        // Kalau category diisi → kategori harus sama persis
        val category = criteria.category
        if (!category.isNullOrBlank()) {
            condition = condition.and(PRODUCTS.CATEGORY.eq(category))
        }

        return condition
    }

    private fun toSortField(sortField: ProductSortField, sortDirection: SortDirection): SortField<*> {
        val field: Field<*> = when (sortField) {
            ProductSortField.ID -> PRODUCTS.ID
            ProductSortField.NAME -> PRODUCTS.NAME
            ProductSortField.CATEGORY -> PRODUCTS.CATEGORY
            ProductSortField.PRICE -> PRODUCTS.PRICE
            ProductSortField.STOCK -> PRODUCTS.STOCK
            ProductSortField.CREATED_AT -> PRODUCTS.CREATED_AT
        }

        return when (sortDirection) {
            SortDirection.ASC -> field.asc()
            SortDirection.DESC -> field.desc()
        }
    }

    // =====================================================
    // DETAIL
    // =====================================================

    override fun findById(id: Long): Product? {
        val productsRecord: ProductsRecord = dslContext
            .selectFrom(PRODUCTS)
            .where(PRODUCTS.ID.eq(id))
            .fetchOne()
            ?: return null

        return toProduct(productsRecord)
    }

    // =====================================================
    // CREATE / UPDATE
    // =====================================================

    override fun save(product: Product): Product {
        val productId = product.id

        val savedRecord: ProductsRecord = if (productId == null) {
            dslContext
                .insertInto(PRODUCTS)
                .set(PRODUCTS.NAME, product.name)
                .set(PRODUCTS.CATEGORY, product.category)
                .set(PRODUCTS.PRICE, product.price)
                .set(PRODUCTS.STOCK, product.stock)
                .returning()
                .fetchSingle()
        } else {
            dslContext
                .update(PRODUCTS)
                .set(PRODUCTS.NAME, product.name)
                .set(PRODUCTS.CATEGORY, product.category)
                .set(PRODUCTS.PRICE, product.price)
                .set(PRODUCTS.STOCK, product.stock)
                .set(PRODUCTS.UPDATED_AT, DSL.currentOffsetDateTime())
                .where(PRODUCTS.ID.eq(productId))
                .returning()
                .fetchSingle()
        }

        return toProduct(savedRecord)
    }

    // =====================================================
    // DELETE
    // =====================================================

    override fun deleteById(id: Long): Boolean {
        val deletedRowCount = dslContext
            .deleteFrom(PRODUCTS)
            .where(PRODUCTS.ID.eq(id))
            .execute()

        return deletedRowCount > 0
    }

    // =====================================================
    // PENERJEMAH
    // =====================================================

    private fun toProduct(productsRecord: ProductsRecord): Product {
        return Product(
            id = productsRecord.id,
            name = productsRecord.name,
            category = productsRecord.category,
            price = productsRecord.price,
            stock = productsRecord.stock,
            createdAt = productsRecord.createdAt,
            updatedAt = productsRecord.updatedAt,
        )
    }
}