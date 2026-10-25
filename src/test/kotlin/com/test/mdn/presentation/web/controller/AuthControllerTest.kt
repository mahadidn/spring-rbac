package com.test.mdn.presentation.web.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun registerShouldReturn201AndNeverExposePasswordHash() {
        mockMvc.post("/api/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                  "username": "sari",
                  "email": "sari@mail.com",
                  "password": "rahasia123"
                }
            """.trimIndent()
        }.andDo {
            print()
        }.andExpect {
            status { isCreated() }
            jsonPath("$.id") { exists() }
            jsonPath("$.username") { value("sari") }
            jsonPath("$.roles[0]") { value("STAFF") }
            jsonPath("$.passwordHash") { doesNotExist() }
        }
    }

    @Test
    fun registerShouldReturn409WhenUsernameAlreadyUsed() {
        mockMvc.post("/api/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                  "username": "budi",
                  "email": "budi-lain@mail.com",
                  "password": "rahasia123"
                }
            """.trimIndent()
        }.andExpect {
            status { isConflict() }
            jsonPath("$.title") { value("Data sudah dipakai") }
            jsonPath("$.detail") { value("Username 'budi' has been used") }
        }
    }

    @Test
    fun registerShouldReturn400WithFieldErrorsWhenInputInvalid() {
        mockMvc.post("/api/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                  "username": "sari",
                  "email": "bukan-email",
                  "password": "123"
                }
            """.trimIndent()
        }.andDo {
            print()
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.title") { value("Input tidak valid") }
            jsonPath("$.errors.email") { exists() }
            jsonPath("$.errors.password") { exists() }
            jsonPath("$.errors.username") { doesNotExist() }
        }
    }

    @Test
    fun otherEndpointsShouldBeLockedWithoutLogin() {
        mockMvc.get("/api/users").andExpect {
            status { isForbidden() }
        }
    }
}