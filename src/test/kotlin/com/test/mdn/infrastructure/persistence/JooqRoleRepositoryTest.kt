package com.test.mdn.infrastructure.persistence

import com.test.mdn.domain.model.Role
import com.test.mdn.domain.repository.PermissionRepository
import com.test.mdn.domain.repository.RoleRepository
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
class JooqRoleRepositoryTest {

    @Autowired
    lateinit var roleRepository: RoleRepository

    @Autowired
    lateinit var permissionRepository: PermissionRepository

    @Test
    fun findByNameShouldReturnRoleWithPermissions() {
        val adminRole = roleRepository.findByName("ADMIN")

        println(adminRole)
        assertNotNull(adminRole)
        assertEquals(2, adminRole.permissions.size)
    }

    @Test
    fun findByNameShouldReturnNullWhenNotFound() {
        val role = roleRepository.findByName("TIDAK_ADA")

        assertNull(role)
    }

    @Test
    fun findAllShouldReturnAllRolesWithTheirPermissions() {
        val roles = roleRepository.findAll()

        println(roles)
        val roleNames = roles.map { role -> role.name }
        assertTrue(roleNames.contains("ADMIN"))
        assertTrue(roleNames.contains("STAFF"))

        val staffRole = roles.first { role -> role.name == "STAFF" }
        assertEquals(1, staffRole.permissions.size)
    }

    @Test
    fun newRoleShouldHaveNoPermissionsUntilAssigned() {
        // 1. Simpan role baru
        val managerRole = roleRepository.save(
            Role(name = "MANAGER", description = "Manajer"),
        )
        assertNotNull(managerRole.id)
        assertTrue(managerRole.permissions.isEmpty())

        // 2. Berikan permission user:read ke MANAGER
        val userReadPermission = permissionRepository.findByName("user:read")
        assertNotNull(userReadPermission)
        assertNotNull(userReadPermission.id)
        roleRepository.assignPermission(managerRole.id, userReadPermission.id)

        // 3. Berikan permission yang SAMA sekali lagi — tidak boleh error
        roleRepository.assignPermission(managerRole.id, userReadPermission.id)

        // 4. Ambil ulang, sekarang MANAGER punya tepat 1 permission
        val reloadedManagerRole = roleRepository.findById(managerRole.id)
        println(reloadedManagerRole)
        assertNotNull(reloadedManagerRole)
        assertEquals(1, reloadedManagerRole.permissions.size)
    }
}