package com.example.qualityautomacaochallenge.application.controller

import com.example.qualityautomacaochallenge.application.error_handling.DeleteConflictException
import com.example.qualityautomacaochallenge.application.error_handling.NotFoundException
import com.example.qualityautomacaochallenge.application.user.*
import com.example.qualityautomacaochallenge.domain.IUserRepository
import com.example.qualityautomacaochallenge.domain.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import kotlin.jvm.optionals.getOrNull

@RestController
class UserController(
    @Autowired private val repository: IUserRepository,
    @Autowired private val passwordEncoder: PasswordEncoder,
) {
    /**
     * HTTP Method    : GET
     * Response Status: [HttpStatus.OK]
     * Response Body  : [ListUsersResponseDto]
     *
     * Route responsible for fetching a list of all registered users in the database and returning [ListUsersResponseDto].
     */
    @GetMapping("/users")
    @Throws(Exception::class)
    fun listUsers(): ResponseEntity<ListUsersResponseDto> {
        val foundUsers = repository.findAll()
        val response = ListUsersResponseDto(foundUsers.map { UserDto(it) })
        return ResponseEntity.ok().body(response)
    }

    /**
     * HTTP Method    : DELETE
     * Response Status: [HttpStatus.NO_CONTENT]
     *
     * Route responsible for deleting a specific user in the database (other than the requester).
     */
    @DeleteMapping("/user")
    @Throws(Exception::class)
    fun deleteUser(@RequestParam username: String): ResponseEntity<Void> {
        val specifiedUser = repository.findByUsername(username) ?: throw NotFoundException("")

        val loggedInUser: Authentication = SecurityContextHolder.getContext().authentication
        if (loggedInUser.name == specifiedUser.username) {
            throw DeleteConflictException("")
        }

        repository.delete(specifiedUser)

        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    /**
     * HTTP Method    : PUT
     * Response Status: [HttpStatus.OK]
     *
     * Route responsible for updating a specific user in the database.
     */
    @PutMapping("/user")
    @Throws(Exception::class)
    fun editUser(@RequestParam id: Int, @RequestBody newData: UserUpdateDto): ResponseEntity<UserDto> {
        val foundUser = repository.findById(id).getOrNull() ?: throw NotFoundException("")

        val encryptedPassword = passwordEncoder.encode(newData.password)
        val modifiedUser =
            User(foundUser.getId(), newData.username, encryptedPassword, created = foundUser.getCreated())

        repository.save(modifiedUser)

        return ResponseEntity(UserDto(modifiedUser), HttpStatus.OK)
    }

    /**
     * HTTP Method    : GET
     * Response Status: [HttpStatus.OK]
     *
     * Route responsible for counting the amount of inserted users per day.
     */
    @GetMapping("/user-count")
    @Throws(Exception::class)
    fun countUsers(): ResponseEntity<UserCountByDateResponseDto> {
        val allUsers = repository.findAll()
            .groupingBy { it.getCreated().toLocalDate() }
            .eachCount()
            .map { UserCountByDateDto(it.key, it.value) }

        val response = UserCountByDateResponseDto(allUsers)

        return ResponseEntity(response, HttpStatus.OK)
    }
}
