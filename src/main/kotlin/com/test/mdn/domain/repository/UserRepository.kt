package com.test.mdn.domain.repository

import com.test.mdn.domain.model.User

interface UserRepository {
    fun findById(id: Long): User?

    fun findByUsername(username: String): User?

    fun existsByUsername(username: String): Boolean

    fun existsByEmail(email: String): Boolean

    fun save(user: User): User

    fun assignRole(userId: Long, roleId: Long)
}