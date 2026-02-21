package com.example.travelunsafe

sealed class Screen(val route: String) {
    object Login    : Screen("login")
    object Register : Screen("register")
    object Profile  : Screen("profile/{user_id}") {
        fun createRoute(userId: String) = "profile/$userId"
    }
}