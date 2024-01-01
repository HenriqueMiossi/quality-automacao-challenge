package com.example.qualityautomacaochallenge.domain

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import kotlin.jvm.Throws

@Service
class AuthenticationService(
    @Autowired private val repository: IUserRepository
) : UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String?): UserDetails {
        if (username == null)
            throw UsernameNotFoundException("username does not exist")

        return repository.findByUsername(username) ?: throw UsernameNotFoundException("username does not exist")
    }
}