package com.test.mdn.presentation.web.dto

data class PaginationListResponse<T>(
    val content: List<T>,
    val totalPages: Int,
    val currentPage: Int,
    val size: Int,
    val totalFiltered: Int,
    val totalAll: Long,
    val isFirst: Boolean,
    val isLast: Boolean,
    val isEmpty: Boolean,
) {
    companion object {
        fun <T> from(
            content: List<T>,
            totalPages: Int,
            currentPage: Int,
            size: Int,
            totalFiltered: Int,
            totalAll: Long,
            isFirst: Boolean,
            isLast: Boolean,
            isEmpty: Boolean,
        ): PaginationListResponse<T> {
            return PaginationListResponse(
                content = content,
                totalPages = totalPages,
                currentPage = currentPage + 1,
                size = size,
                totalFiltered = totalFiltered,
                totalAll = totalAll,
                isFirst = isFirst,
                isLast = isLast,
                isEmpty = isEmpty,
            )
        }
    }
}
