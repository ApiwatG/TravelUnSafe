package com.example.travelunsafe

sealed class Screen(val route: String) {
    object CreatePlan : Screen("create_plan")
    object PlanDetail : Screen("plan_detail")
}