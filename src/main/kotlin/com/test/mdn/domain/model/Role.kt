package com.test.mdn.domain.model

data class Role(
    val id: Long? = null,
    val name: String,
    val description: String? = null,
    val permissions: Set<Permission> = emptySet(),
)
