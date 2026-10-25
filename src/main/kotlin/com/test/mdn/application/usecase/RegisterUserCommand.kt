package com.test.mdn.application.usecase

data class RegisterUserCommand(
    val username: String,
    val email: String,
    val rawPassword: String,
)