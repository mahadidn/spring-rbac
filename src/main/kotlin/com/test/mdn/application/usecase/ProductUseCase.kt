package com.test.mdn.application.usecase

import com.test.mdn.domain.exception.ProductNotFoundException
import com.test.mdn.domain.model.PageResult
import com.test.mdn.domain.model.Product
import com.test.mdn.domain.model.ProductSearchCriteria
import com.test.mdn.domain.repository.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductUseCase(
    private val productRepository: ProductRepository,
) {

    @Transactional(transactionManager = "inventoryTransactionManager", readOnly = true)
    fun getProducts(criteria: ProductSearchCriteria): PageResult<Product> {
        return productRepository.findPage(criteria)
    }

    @Transactional(transactionManager = "inventoryTransactionManager", readOnly = true)
    fun getProduct(id: Long): Product {
        return productRepository.findById(id) ?: throw ProductNotFoundException(id)
    }

    @Transactional(transactionManager = "inventoryTransactionManager")
    fun createProduct(command: ProductCommand): Product {
        val newProduct = Product(
            name = command.name,
            category = command.category,
            price = command.price,
            stock = command.stock,
        )
        return productRepository.save(newProduct)
    }

    @Transactional(transactionManager = "inventoryTransactionManager")
    fun updateProduct(id: Long, command: ProductCommand): Product {
        val existingProduct = productRepository.findById(id) ?: throw ProductNotFoundException(id)

        val updatedProduct = existingProduct.copy(
            name = command.name,
            category = command.category,
            price = command.price,
            stock = command.stock,
        )
        return productRepository.save(updatedProduct)
    }

    @Transactional(transactionManager = "inventoryTransactionManager")
    fun deleteProduct(id: Long) {
        val deleted = productRepository.deleteById(id)
        if (!deleted) {
            throw ProductNotFoundException(id)
        }
    }
}