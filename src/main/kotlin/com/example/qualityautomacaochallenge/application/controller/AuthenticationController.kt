package com.example.qualityautomacaochallenge.application.controller

import com.example.qualityautomacaochallenge.application.authentication.CredentialsDto
import com.example.qualityautomacaochallenge.application.authentication.LoginResponseDto
import com.example.qualityautomacaochallenge.application.error_handling.RegisterConflictException
import com.example.qualityautomacaochallenge.domain.IUserRepository
import com.example.qualityautomacaochallenge.domain.User
import com.example.qualityautomacaochallenge.infrastructure.security.TokenService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthenticationController(
    @Autowired private val repository: IUserRepository,
    @Autowired private val authenticationManager: AuthenticationManager,
    @Autowired private val passwordEncoder: PasswordEncoder,
    @Autowired private val tokenService: TokenService
) {
    /**
     * HTTP Method    : POST
     * Request Body   : [CredentialsDto]
     * Response Status: [HttpStatus.OK]
     * Response Body  : [LoginResponseDto]
     *
     * Route responsible for managing login logic. Receives [credentials] and returns [ResponseEntity.ok] with
     * [LoginResponseDto] if the username and password matches to anything in the database. Throws [BadCredentialsException]
     * if anything doesn't match. Exception handled by the global exception handler.
     */
    @PostMapping("/login")
    @Throws(BadCredentialsException::class)
    fun login(@RequestBody credentials: CredentialsDto): ResponseEntity<LoginResponseDto> {
        val springCredentials = UsernamePasswordAuthenticationToken(
            credentials.username, credentials.password
        )
        val authentication = authenticationManager.authenticate(springCredentials)

        val response = LoginResponseDto(tokenService.generateToken(authentication.principal as User))
        return ResponseEntity.ok().body(response)
    }

    /**
     * HTTP Method    : POST
     * Request Body   : [CredentialsDto]
     * Response Status: [HttpStatus.CREATED]
     *
     * Route responsible for managing new user registration logic. Receives [credentials] and returns [ResponseEntity.ok]
     * with [LoginResponseDto] if the username and password matches to anything in the database. Throws [BadCredentialsException]
     * if anything doesn't match. Exception handled by the global exception handler.
     */
    @PostMapping("/register")
    @Throws(RegisterConflictException::class)
    fun register(@RequestBody credentials: CredentialsDto): ResponseEntity<Void> {
        if (repository.findByUsername(credentials.username) != null) {
            throw RegisterConflictException("")
        }

        val encryptedPassword = passwordEncoder.encode(credentials.password)
        repository.save(
            User(credentials.username, encryptedPassword)
        )

        return ResponseEntity<Void>(HttpStatus.CREATED)
    }
}
