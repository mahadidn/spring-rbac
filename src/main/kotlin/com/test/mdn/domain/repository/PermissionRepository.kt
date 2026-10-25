package com.test.mdn.domain.repository

import com.test.mdn.domain.model.Permission

interface PermissionRepository {
    fun findById(id: Long): Permission?

    fun findByName(name: String): Permission?

    fun findAll(): List<Permission>

    fun save(permission: Permission): Permission
}