package com.test.mdn.domain.exception

class UsernameAlreadyUsedException(
    username: String,
) : RuntimeException("Username '$username' has been used")