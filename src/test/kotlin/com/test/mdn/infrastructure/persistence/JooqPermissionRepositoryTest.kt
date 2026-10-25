package com.test.mdn.infrastructure.persistence

import com.test.mdn.domain.model.Permission
import com.test.mdn.domain.repository.PermissionRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest
@Transactional
class JooqPermissionRepositoryTest {

    @Autowired
    lateinit var permissionRepository: PermissionRepository

    @Test
    fun findByNameShouldReturnPermission() {
        val permission = permissionRepository.findByName("user:read")

        assertNotNull(permission)
        assertEquals("Melihat data user", permission.description)
    }

    @Test
    fun findByNameShouldReturnNullWhenNotFound() {
        val permission = permissionRepository.findByName("tidak:ada")

        assertNull(permission)
    }

    @Test
    fun findAllShouldReturnAllPermissions() {
        val permissions = permissionRepository.findAll()

        println(permissions)
        assertTrue(permissions.size >= 2)
    }

    @Test
    fun saveShouldInsertNewPermission() {
        val newPermission = Permission(
            name = "role:assign",
            description = "Memberikan role ke user",
        )

        val savedPermission = permissionRepository.save(newPermission)

        println(savedPermission)
        assertNotNull(savedPermission.id)
        assertEquals("role:assign", savedPermission.name)
    }
}