package com.example.qualityautomacaochallenge.infrastructure.security

import com.example.qualityautomacaochallenge.domain.IUserRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class SecurityFilter(
    @Autowired private val tokenService: TokenService,
    @Autowired private val userRepository: IUserRepository
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = recoverToken(request)

        if (token != null) {
            val username = tokenService.validateToken(token)
            val userDetails = userRepository.findByUsername(username)

            val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails?.authorities)
            SecurityContextHolder.getContext().authentication = authentication
        }
        filterChain.doFilter(request, response)
    }

    private fun recoverToken(request: HttpServletRequest): String? {
        val authHeader = request.getHeader("Authorization") ?: return null
        return authHeader.replace("Bearer ", "")
    }
}