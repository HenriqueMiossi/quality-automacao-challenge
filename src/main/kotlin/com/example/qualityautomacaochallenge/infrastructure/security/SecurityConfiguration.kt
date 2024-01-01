package com.example.qualityautomacaochallenge.infrastructure.security

import com.example.qualityautomacaochallenge.domain.AuthenticationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
class SecurityConfiguration(
    @Autowired private val authenticationService: AuthenticationService,
    @Autowired private val securityFilter: SecurityFilter
) {

    @Bean
    fun SecurityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        return httpSecurity
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it
                    .requestMatchers(HttpMethod.POST, "/login").permitAll()
                    .requestMatchers(HttpMethod.POST, "/register").permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
    }

    @Bean
    fun authenticationManager(
        authenticationManagerBuilder: AuthenticationManagerBuilder,
        passwordEncoder: PasswordEncoder
    ): AuthenticationManager {
        val provider = DaoAuthenticationProvider()
        provider.setPasswordEncoder(passwordEncoder)
        provider.setUserDetailsService(authenticationService)

        return ProviderManager(provider)
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        // For simplicity, this project does not use salt. Though, it should definitely be used in real applications
        return Argon2PasswordEncoder(0, 32, 1, 66536, 2)
    }
}
