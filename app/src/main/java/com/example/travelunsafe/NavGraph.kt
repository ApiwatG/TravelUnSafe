package com.example.travelunsafe

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

// ===== SCREEN ROUTES =====
sealed class Screen(val route: String) {
    object Login      : Screen("login")
    object Register   : Screen("register")
    object Main       : Screen("main")
    object Search     : Screen("search")
    object AllPlans   : Screen("all_plans")
    object TripDetail : Screen("trip_detail/{trip_id}") {
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

    // Auto-login: if already logged in, skip to main
    val startDestination = if (prefs.isLoggedIn()) Screen.Main.route else Screen.Login.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // ===== LOGIN =====
        composable(Screen.Login.route) {
            LoginScreen(
                navController = navController,
                viewModel = travelViewModel,
                prefs = prefs
            )
        }

        // ===== REGISTER =====
        composable(Screen.Register.route) {
            RegisterScreen(
                navController = navController,
                viewModel = travelViewModel,
                prefs = prefs
            )
        }

        // ===== MAIN (Bottom Nav Host) =====
        // ✅ TravelApp gets lambdas — navController stays in NavGraph where it belongs
        composable(Screen.Main.route) {
            TravelApp(
                viewModel = travelViewModel,
                prefs = prefs,
                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                onNavigateToAllPlans = { navController.navigate(Screen.AllPlans.route) }
            )
        }

        // ===== SEARCH =====
        composable(Screen.Search.route) {
            SearchScreen(
                viewModel = travelViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // ===== ALL PLANS =====
        composable(Screen.AllPlans.route) {
            AllPlansScreen(
                viewModel = travelViewModel,
                prefs = prefs,
                onBack = { navController.popBackStack() },
                onTripClick = { tripId ->
                    navController.navigate(Screen.TripDetail.createRoute(tripId))
                }
            )
        }

        // ===== TRIP DETAIL =====
        composable(Screen.TripDetail.route) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("trip_id") ?: return@composable
            TripDetailScreen(
                tripId = tripId,
                navController = navController,
                viewModel = travelViewModel
            )
        }

        // ===== HOTEL DETAIL =====
        composable(Screen.HotelDetail.route) { backStackEntry ->
            val hotelId = backStackEntry.arguments?.getString("hotel_id") ?: return@composable
            HotelDetailScreen(
                hotelId = hotelId,
                navController = navController,
                viewModel = travelViewModel
            )
        }
    }
}