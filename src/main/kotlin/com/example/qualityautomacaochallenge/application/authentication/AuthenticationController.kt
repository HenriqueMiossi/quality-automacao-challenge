package com.example.qualityautomacaochallenge.application.authentication

import com.example.qualityautomacaochallenge.application.error_handling.RegisterConflictException
import com.example.qualityautomacaochallenge.domain.*
import com.example.qualityautomacaochallenge.domain.IUserRepository
import com.example.qualityautomacaochallenge.infrastructure.security.TokenService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpClientErrorException
import kotlin.jvm.Throws

@RestController
class AuthenticationController(
    @Autowired private val repository: IUserRepository,
    @Autowired private val authenticationManager: AuthenticationManager,
    @Autowired private val passwordEncoder: PasswordEncoder,
    @Autowired private val tokenService: TokenService
) {
    @PostMapping("/login")
    @Throws(BadCredentialsException::class)
    fun login(@RequestBody credentials: CredentialsDto): ResponseEntity<String> {
        val springCredentials = UsernamePasswordAuthenticationToken(
            credentials.username, credentials.password
        )
        val authentication = authenticationManager.authenticate(springCredentials)

        val token = tokenService.generateToken(authentication.principal as User)

        return ResponseEntity.ok().body(token)
    }

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

        return ResponseEntity.ok().build()
    }
}
