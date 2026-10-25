package com.test.mdn.infrastructure.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain


@Configuration
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }

            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }

            authorizeHttpRequests {
                // Endpoint publik — boleh diakses tanpa login
                authorize(HttpMethod.POST, "/api/auth/register", permitAll)
                authorize("/actuator/health", permitAll)
                authorize("/scalar/**", permitAll)
                authorize("/api-docs/**", permitAll)
                authorize("/docs/**", permitAll)
                authorize("/api/products/**", permitAll)

                // Selain itu wajib login
                authorize(anyRequest, authenticated)
            }
        }
        return http.build()
    }
}