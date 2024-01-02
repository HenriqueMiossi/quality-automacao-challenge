package com.example.qualityautomacaochallenge.domain

import jakarta.persistence.*
import lombok.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime

@Table(name = "user", schema = "dbo")
@Entity(name = "User")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Int,

    @Setter
    private val username: String,

    @Setter
    private val password: String,

    @Column(columnDefinition = "TIMESTAMP")
    private val created: LocalDateTime
) : UserDetails {

    constructor(username: String, password: String) : this(0, username, password, LocalDateTime.now())
    constructor(id: Int, username: String, password: String) : this(id, username, password, LocalDateTime.now())

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        // In a larger application, a more robust implementation of roles is better suited, maybe with a Role enum
        // and a switch statement, but here a simple ROLE_USER should be sufficient for this demo
        return mutableListOf(SimpleGrantedAuthority("ROLE_USER"))
    }

    override fun getPassword(): String {
        return this.password
    }

    override fun getUsername(): String {
        return this.username
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

    fun getId(): Int {
        return this.id
    }

    fun getCreated(): LocalDateTime {
        return this.created
    }
}
