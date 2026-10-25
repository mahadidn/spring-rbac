package com.test.mdn.infrastructure.persistence


import com.test.mdn.domain.model.User
import com.test.mdn.domain.repository.UserRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest
@Transactional
class JooqUserRepositoryTest {

    @Autowired
    lateinit var userRepository: UserRepository

    @Test
    fun findByUsernameShouldReturnUserWithRolesAndPermissions() {
        val user = userRepository.findByUsername("budi")

        println(user)
        assertNotNull(user)
        assertEquals(2, user.roles.size)

        val adminRole = user.roles.first { role -> role.name == "ADMIN" }
        assertEquals(2, adminRole.permissions.size)
    }

    @Test
    fun findByUsernameShouldReturnNullWhenUserNotFound() {
        val user = userRepository.findByUsername("orang-yang-tidak-ada")

        assertNull(user)
    }

    @Test
    fun existsByUsernameShouldWork() {
        assertTrue(userRepository.existsByUsername("budi"))
        assertFalse(userRepository.existsByUsername("orang-yang-tidak-ada"))
    }

    @Test
    fun saveShouldInsertNewUserAndGenerateId() {
        val newUser = User(
            username = "sari",
            email = "sari@mail.com",
            passwordHash = "dummy-hash",
        )

        val savedUser = userRepository.save(newUser)

        println(savedUser)
        assertNotNull(savedUser.id)
        assertNotNull(savedUser.createdAt)
        assertTrue(savedUser.roles.isEmpty())
    }
}