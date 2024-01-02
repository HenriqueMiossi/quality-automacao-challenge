package com.example.qualityautomacaochallenge.application.user

import com.example.qualityautomacaochallenge.domain.User
import org.springframework.security.core.GrantedAuthority
import java.time.LocalDateTime

/**
 * This is essentially the User class defined in the domain package, but with the id and password omitted.
 * Using a specific class for it should make the response more flexible to changes and more customizable.
 */
class UserDto(
    val id: Int,
    val username: String,
    val created: LocalDateTime,
    val isEnabled: Boolean,
    val authorities: MutableCollection<out GrantedAuthority>,
    val isAccountNonLocked: Boolean,
    val isCredentialsNonExpired: Boolean,
    val isAccountNonExpired: Boolean
) {
    constructor(user: User) : this(
        user.getId(),
        user.username,
        user.getCreated(),
        user.isEnabled,
        user.authorities,
        user.isAccountNonLocked,
        user.isCredentialsNonExpired,
        user.isAccountNonExpired
    )
}