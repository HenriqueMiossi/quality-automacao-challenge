package com.example.qualityautomacaochallenge.application.controller

import com.example.qualityautomacaochallenge.domain.*
import com.example.qualityautomacaochallenge.domain.IUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController (
    @Autowired private val repository: IUserRepository
) {
    @GetMapping("/users")
    fun listUsers(): ResponseEntity<List<User>> {
        try {
            val users = repository.findAll()
            return ResponseEntity.ok().body(users)
        } catch (e: Exception) {
            throw RuntimeException("Error when listing users");
        }
    }

    @PostMapping("/user")
    fun createUser(@RequestBody newUser: User): ResponseEntity<User> {
        try {
            val savedUser = repository.save(newUser)
            return ResponseEntity.ok().body(savedUser)
        } catch (e: Exception) {
            throw RuntimeException("Error when creating new user");
        }
    }
}
