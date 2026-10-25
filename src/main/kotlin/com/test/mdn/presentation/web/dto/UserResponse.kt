package com.test.mdn.presentation.web.dto

import com.test.mdn.domain.model.User

data class UserResponse(
    val id: Long,
    val username: String,
    val email: String,
    val enabled: Boolean,
    val roles: List<String>,
) {
    companion object {
        fun fromDomain(user: User): UserResponse {
            return UserResponse(
                id = checkNotNull(user.id) { "User yang dikirim ke client pasti sudah punya id" },
                username = user.username,
                email = user.email,
                enabled = user.enabled,
                roles = user.roles.map { role -> role.name }.sorted(),
            )
        }
    }
}