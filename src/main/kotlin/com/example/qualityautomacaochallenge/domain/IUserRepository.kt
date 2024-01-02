package com.example.qualityautomacaochallenge.domain

import org.springframework.data.jpa.repository.JpaRepository

interface IUserRepository : JpaRepository<User, Int> {
    fun findByUsername(username: String): User?
}