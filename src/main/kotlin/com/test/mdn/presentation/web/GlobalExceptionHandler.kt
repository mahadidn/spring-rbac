package com.test.mdn.presentation.web

import com.test.mdn.domain.exception.EmailAlreadyUsedException
import com.test.mdn.domain.exception.RoleNotFoundException
import com.test.mdn.domain.exception.UsernameAlreadyUsedException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    // =====================================================
    // 409 CONFLICT
    // =====================================================
    @ExceptionHandler(UsernameAlreadyUsedException::class, EmailAlreadyUsedException::class)
    fun handleAlreadyUsed(exception: RuntimeException): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.message)
        problemDetail.title = "Data sudah dipakai"
        return problemDetail
    }

    // =====================================================
    // 500 — role default
    // =====================================================
    @ExceptionHandler(RoleNotFoundException::class)
    fun handleRoleNotFound(exception: RoleNotFoundException): ProblemDetail {
        log.error("Konfigurasi role bermasalah", exception)

        val problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Terjadi kesalahan konfigurasi pada server",
        )
        problemDetail.title = "Kesalahan server"
        return problemDetail
    }

    // =====================================================
    // 400 — validation failed
    // =====================================================
    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        val fieldErrors: Map<String, String> = ex.bindingResult.fieldErrors.associate { fieldError ->
            fieldError.field to (fieldError.defaultMessage ?: "Nilai tidak valid")
        }

        ex.body.title = "Input tidak valid"
        ex.body.detail = "Periksa kembali data yang dikirim"
        ex.body.setProperty("errors", fieldErrors)

        return super.handleMethodArgumentNotValid(ex, headers, status, request)
    }
}