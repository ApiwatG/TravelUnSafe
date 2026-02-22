package com.example.travelunsafe

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

// ===== SCREEN ROUTES =====
sealed class Screen(val route: String) {
    object Landing     : Screen("landing")
    object Login       : Screen("login")
    object Register    : Screen("register")
    object Main        : Screen("main")
    object Search      : Screen("search")
    object TripDetail  : Screen("trip_detail/{trip_id}") {
        fun createRoute(tripId: String) = "trip_detail/$tripId"
    }
    object HotelDetail : Screen("hotel_detail/{hotel_id}") {
        fun createRoute(hotelId: String) = "hotel_detail/$hotelId"
    }
}

@Composable
fun NavGraph(navController: NavHostController) {
    val travelViewModel: TravelViewModel = viewModel()
    val context = LocalContext.current
    val prefs = remember { SharedPreferencesManager(context) }

    val startDestination = if (prefs.isLoggedIn()) Screen.Main.route else Screen.Landing.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable(Screen.Landing.route) {
            LandingScreen(onStart = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Landing.route) { inclusive = true }
                }
            })
        }

        composable(Screen.Login.route) {
            LoginScreen(navController = navController, viewModel = travelViewModel, prefs = prefs)
        }

        composable(Screen.Register.route) {
            RegisterScreen(navController = navController, viewModel = travelViewModel, prefs = prefs)
        }

        composable(Screen.Main.route) {
            TravelApp(
                viewModel = travelViewModel,
                prefs = prefs,
                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(viewModel = travelViewModel, onBack = { navController.popBackStack() })
        }

        composable(Screen.TripDetail.route) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("trip_id") ?: return@composable
            TripDetailScreen(tripId = tripId, navController = navController, viewModel = travelViewModel)
        }

        composable(Screen.HotelDetail.route) { backStackEntry ->
            val hotelId = backStackEntry.arguments?.getString("hotel_id") ?: return@composable
            HotelDetailScreen(hotelId = hotelId, navController = navController, viewModel = travelViewModel)
        }
    }
}