package com.test.mdn.presentation.web.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterUserRequest(

    @NotBlank(message = "Username wajib diisi")
    @Size(min = 3, max = 50, message = "Username harus 3 sampai 50 karakter")
    val username: String,

    @NotBlank(message = "Email wajib diisi")
    @Email(message = "Format email tidak valid")
    @Size(max = 100, message = "Email maksimal 100 karakter")
    val email: String,

    @NotBlank(message = "Password wajib diisi")
    @Size(min = 8, max = 72, message = "Password harus 8 sampai 72 karakter")
    val password: String,
)
