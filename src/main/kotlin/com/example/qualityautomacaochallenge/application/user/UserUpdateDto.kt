package com.example.qualityautomacaochallenge.application.user

import com.example.qualityautomacaochallenge.domain.User

class UserUpdateDto(
    val username: String,
    val password: String,
) {
    constructor(user: User) : this(
        user.username,
        user.password,
    )
}