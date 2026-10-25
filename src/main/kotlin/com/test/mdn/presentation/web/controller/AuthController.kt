package com.test.mdn.presentation.web.controller

import com.test.mdn.application.usecase.RegisterUserCommand
import com.test.mdn.application.usecase.RegisterUserUseCase
import com.test.mdn.presentation.web.dto.RegisterUserRequest
import com.test.mdn.presentation.web.dto.UserResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val registerUserUseCase: RegisterUserUseCase,
) {

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@Valid @RequestBody request: RegisterUserRequest): UserResponse {
        val command = RegisterUserCommand(
            username = request.username,
            email = request.email,
            rawPassword = request.password,
        )

        val registeredUser = registerUserUseCase.execute(command)

        return UserResponse.fromDomain(registeredUser)
    }
}