package com.example.travelunsafe

data class RegisterClass(
    val username: String,
    val email: String,
    val password: String,
    val role: String = "user"
)