package com.test.mdn.domain.exception

class RoleNotFoundException(
    roleName: String,
) : RuntimeException("Role '$roleName' not found")