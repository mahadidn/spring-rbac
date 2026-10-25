package com.test.mdn.domain.model

import java.time.OffsetDateTime

data class User(
    val id: Long? = null,
    val username: String,
    val email: String,
    val passwordHash: String,
    val enabled: Boolean = true,
    val roles: Set<Role> = emptySet(),
    val createdAt: OffsetDateTime? = null,
    val updatedAt: OffsetDateTime? = null,
)
