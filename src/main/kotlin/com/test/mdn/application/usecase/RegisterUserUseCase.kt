package com.test.mdn.application.usecase

import com.test.mdn.application.usecase.RegisterUserCommand
import com.test.mdn.domain.exception.EmailAlreadyUsedException
import com.test.mdn.domain.exception.RoleNotFoundException
import com.test.mdn.domain.exception.UsernameAlreadyUsedException
import com.test.mdn.domain.model.User
import com.test.mdn.domain.repository.RoleRepository
import com.test.mdn.domain.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RegisterUserUseCase(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder,
) {

    private val defaultRoleName = "STAFF"

    @Transactional
    fun execute(command: RegisterUserCommand): User {

        if (userRepository.existsByUsername(command.username)) {
            throw UsernameAlreadyUsedException(command.username)
        }

        if (userRepository.existsByEmail(command.email)) {
            throw EmailAlreadyUsedException(command.email)
        }

        val defaultRole = roleRepository.findByName(defaultRoleName)
            ?: throw RoleNotFoundException(defaultRoleName)
        val defaultRoleId = checkNotNull(defaultRole.id) { "Role from db must have id" }

        val passwordHash = checkNotNull(passwordEncoder.encode(command.rawPassword)) {
            "hash result password must not be null"
        }

        // save new user
        val newUser = User(
            username = command.username,
            email = command.email,
            passwordHash = passwordHash,
        )
        val savedUser = userRepository.save(newUser)
        val savedUserId = checkNotNull(savedUser.id) { "user don't have an ID" }

        // give default role
        userRepository.assignRole(savedUserId, defaultRoleId)

        return checkNotNull(userRepository.findById(savedUserId)) {
            "A newly saved user can certainly be found\n"
        }
    }
}