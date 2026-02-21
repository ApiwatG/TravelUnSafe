package com.example.travelunsafe

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

@Composable
fun NavGraph(navController: NavHostController, viewModel: HotelsViewModel) {
    NavHost(navController = navController, startDestination = Screen.Login.route) {

        composable(Screen.Login.route) {
            LoginScreen(navController = navController, viewModel = viewModel)
        }

        composable(Screen.Register.route) {
            RegisterScreen(navController = navController, viewModel = viewModel)
        }

        composable(
            route = Screen.Profile.route,
            arguments = listOf(navArgument("user_id") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("user_id") ?: ""
            ProfileScreen(navController = navController, viewModel = viewModel, userId = userId)
        }
    }
}