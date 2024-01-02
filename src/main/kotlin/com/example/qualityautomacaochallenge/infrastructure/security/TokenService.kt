package com.example.qualityautomacaochallenge.infrastructure.security

import com.example.qualityautomacaochallenge.domain.User
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Service
class TokenService(
    @Autowired private val signingKey: RSAKey
) {
    fun generateToken(user: User): String {
        try {
            return "Bearer " +
                    Jwts.builder()
                        .issuer("auth-api")
                        .subject(user.getId().toString())
                        .expiration(createExpirationDate())
                        .signWith(signingKey.getPrivateKey())
                        .compact()
        } catch (e: JwtException) {
            throw RuntimeException("Error while generating token", e)
        }
    }

    fun validateToken(token: String): String {
        try {
            return Jwts.parser()
                .verifyWith(signingKey.getPublicKey())
                .build()
                .parseSignedClaims(token)
                .payload.subject
        } catch (e: JwtException) {
            throw RuntimeException("Error while validating token", e)
        }
    }

    fun createExpirationDate(): Date {
        val instant = Instant.now().plus(120, ChronoUnit.MINUTES)
        return Date.from(instant)
    }
}