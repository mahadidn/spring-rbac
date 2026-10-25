package com.test.mdn.domain.repository

import com.test.mdn.domain.model.Role

interface RoleRepository {
    fun findById(id: Long): Role?

    fun findByName(name: String): Role?

    fun findAll(): List<Role>

    fun save(role: Role): Role

    fun assignPermission(roleId: Long, permissionId: Long)
}