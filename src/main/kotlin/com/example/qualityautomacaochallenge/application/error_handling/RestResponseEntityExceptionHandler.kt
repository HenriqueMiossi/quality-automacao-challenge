package com.example.qualityautomacaochallenge.application.error_handling

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

/**
 * Class responsible for managing exceptions globally. It contains customized error handling logic for each type of used
 * exception.
 */
@ControllerAdvice
class RestResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {

    /**
     * Catches: [BadCredentialsException]
     * Returns: [HttpStatus.BAD_REQUEST]
     *
     * Handles credential errors, including mistyping username and password.
     */
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

    /**
     * Catches: [RegisterConflictException]
     * Returns: [HttpStatus.CONFLICT]
     *
     * Handles user registration errors, specially checking if the username is unique.
     */
    @ExceptionHandler(RegisterConflictException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleRegisterConflictException(
        ex: RegisterConflictException,
        request: WebRequest
    ): ResponseEntity<ErrorResponseDto> {
        val response = ErrorResponseDto(
            HttpStatus.CONFLICT.value(),
            "Conflict",
            "Something went wrong when trying to register a new user. Is your username unique?"
        )

        return ResponseEntity(response, HttpStatus.CONFLICT)
    }

    /**
     * Catches: [NotFoundException]
     * Returns: [HttpStatus.NOT_FOUND]
     *
     * Handles generic "not found" errors.
     */
    @ExceptionHandler(NotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFoundException(
        ex: NotFoundException,
        request: WebRequest
    ): ResponseEntity<ErrorResponseDto> {
        val response = ErrorResponseDto(
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            "The specified resource does not exist or could not be found"
        )

        return ResponseEntity(response, HttpStatus.NOT_FOUND)
    }

    /**
     * Catches: [DeleteConflictException]
     * Returns: [HttpStatus.CONFLICT]
     *
     * Handles user deletion conflicts, specifically if the requester and the user to be deleted are the same.
     */
    @ExceptionHandler(DeleteConflictException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleDeleteConflictException(
        ex: DeleteConflictException,
        request: WebRequest
    ): ResponseEntity<ErrorResponseDto> {
        val response = ErrorResponseDto(
            HttpStatus.CONFLICT.value(),
            "Conflict",
            "You can't delete your own user."
        )

        return ResponseEntity(response, HttpStatus.CONFLICT)
    }
}