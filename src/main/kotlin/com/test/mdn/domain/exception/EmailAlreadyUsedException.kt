package com.test.mdn.domain.exception

class EmailAlreadyUsedException(
    email: String,
) : RuntimeException("Email '$email' has been used")