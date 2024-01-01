package com.example.qualityautomacaochallenge.application.error_handling

import org.apache.coyote.BadRequestException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class RestResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(BadCredentialsException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBadRequestException(ex: BadCredentialsException, request: WebRequest): ResponseEntity<ErrorResponseDto> {
        val response = ErrorResponseDto(
            HttpStatus.BAD_REQUEST.value(),
            "Bad credentials",
            "Did you type the correct username and password?"
        )

        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(RegisterConflictException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleRegisterConflictException(ex: RegisterConflictException, request: WebRequest): ResponseEntity<ErrorResponseDto> {
        val response = ErrorResponseDto(
            HttpStatus.CONFLICT.value(),
            "Conflict",
            "Something went wrong when trying to register a new user. Is your username unique?"
        )

        return ResponseEntity(response, HttpStatus.CONFLICT)
    }
}