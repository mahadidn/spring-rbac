package com.test.mdn.application.usecase

import com.test.mdn.domain.exception.EmailAlreadyUsedException
import com.test.mdn.domain.exception.UsernameAlreadyUsedException
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest
@Transactional
class RegisterUserUseCaseTest {

    @Autowired
    lateinit var registerUserUseCase: RegisterUserUseCase

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    @Test
    fun registerShouldCreateUserWithHashedPasswordAndDefaultRole() {
        val command = RegisterUserCommand(
            username = "sari",
            email = "sari@mail.com",
            rawPassword = "rahasia123",
        )

        val registeredUser = registerUserUseCase.execute(command)

        println(registeredUser)

        assertNotNull(registeredUser.id)

        assertNotEquals("rahasia123", registeredUser.passwordHash)
        assertTrue(registeredUser.passwordHash.startsWith("{bcrypt}"))

        assertTrue(passwordEncoder.matches("rahasia123", registeredUser.passwordHash))

        assertEquals(1, registeredUser.roles.size)
        assertEquals("STAFF", registeredUser.roles.first().name)
    }

    @Test
    fun registerShouldFailWhenUsernameAlreadyUsed() {
        val command = RegisterUserCommand(
            username = "budi",              
            email = "budi-lain@mail.com",
            rawPassword = "rahasia123",
        )

        assertFailsWith<UsernameAlreadyUsedException> {
            registerUserUseCase.execute(command)
        }
    }

    @Test
    fun registerShouldFailWhenEmailAlreadyUsed() {
        val command = RegisterUserCommand(
            username = "budi-lain",
            email = "budi@mail.com",
            rawPassword = "rahasia123",
        )

        assertFailsWith<EmailAlreadyUsedException> {
            registerUserUseCase.execute(command)
        }
    }
}