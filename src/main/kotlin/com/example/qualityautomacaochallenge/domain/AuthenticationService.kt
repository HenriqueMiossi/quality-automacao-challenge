package com.example.qualityautomacaochallenge.domain

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

/**
 * Service class responsible for authentication-related operations.
 * Implements the Spring Security UserDetailsService interface to provide user details for authentication.
 *
 * Receives [repository], an instance of [IUserRepository], which is responsible for interacting with the user data storage.
 */
@Service
class AuthenticationService(
    @Autowired private val repository: IUserRepository
) : UserDetailsService {

    @Throws(BadCredentialsException::class)
    override fun loadUserByUsername(username: String?): UserDetails {
        if (username == null)
            throw BadCredentialsException("")

        return repository.findByUsername(username) ?: throw BadCredentialsException("")
    }
}