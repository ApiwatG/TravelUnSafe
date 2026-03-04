package com.example.travelunsafe

data class User(
    val user_id: String,
    val username: String,
    val email: String,
    val role: String,
    val status: Int  // 1 = Active, 0 = Banned
)