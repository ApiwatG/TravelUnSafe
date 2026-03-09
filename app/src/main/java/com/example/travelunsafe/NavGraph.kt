package com.example.travelunsafe

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

// ===== SCREEN ROUTES =====
sealed class Screen(val route: String) {
    object Login       : Screen("login")
    object Register    : Screen("register")
    object Main        : Screen("main")
    object Search      : Screen("search")
    object Friends     : Screen("friends")
    object TripDetail  : Screen("trip_detail/{trip_id}") {
        fun createRoute(tripId: String) = "trip_detail/$tripId"
    }
    // ── Hotel (friend's feature) ──────────────────────────
    object HotelList   : Screen("hotel_list")
    object HotelDetail : Screen("hotel_detail")  // uses shared state, not path param
    // ── Travel Plan (friend's feature) ───────────────────
    object CreatePlan  : Screen("create_plan")
    object PlanDetail  : Screen("plan_detail/{tripId}") {
        fun createRoute(tripId: String) = "plan_detail/$tripId"
    }
    object Profile : Screen("profile/{user_id}") {
        fun createRoute(userId: String) = "profile/$userId"
    }
    object Admin : Screen("admin")

}

@Composable
fun NavGraph(navController: NavHostController) {
    // ── ViewModels ────────────────────────────────────────
    val travelViewModel: TravelViewModel       = viewModel()
    val hotelViewModel: HotelsViewModel         = viewModel()
    val tripViewModel: TripViewModel           = viewModel()
    val planDetailViewModel: PlanDetailViewModel = viewModel()

    val context = LocalContext.current
    val prefs   = remember { SharedPreferencesManager(context) }

    val startDestination = when {
        !prefs.isLoggedIn()          -> Screen.Login.route
        prefs.getRole() == "admin"   -> Screen.Admin.route
        else                         -> Screen.Main.route
    }

    // Shared hotel state — HotelDetailScreen takes Hotel object not an id
    var selectedHotel by remember { mutableStateOf<Hotel?>(null) }

    NavHost(navController = navController, startDestination = startDestination) {

        // ── AUTH ─────────────────────────────────────────
        composable(Screen.Login.route) {
            LoginScreen(navController = navController, viewModel = travelViewModel, prefs = prefs)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController = navController, viewModel = travelViewModel, prefs = prefs)
        }

        composable(Screen.Admin.route) {
            val role = prefs.getRole()

            if (role == "admin") {
                val adminViewModel: AdminViewModel = viewModel()

                AdminScreen(
                    viewModel = adminViewModel,
                    onBack = { navController.popBackStack() },
                    onLogout = {
                        prefs.clear() // ลบข้อมูลการล็อกอิน
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
        // ── MAIN SHELL ────────────────────────────────────
        composable(Screen.Main.route) {
            TravelApp(
                viewModel              = travelViewModel,
                prefs                  = prefs,
                onNavigateToSearch     = { navController.navigate(Screen.Search.route) },
                onNavigateToFriends    = { navController.navigate(Screen.Friends.route) },
                onNavigateToHotels     = { navController.navigate(Screen.HotelList.route) },
                onNavigateToCreatePlan = { navController.navigate(Screen.CreatePlan.route) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ── SEARCH ───────────────────────────────────────
        composable(Screen.Search.route) {
            SearchScreen(viewModel = travelViewModel, onBack = { navController.popBackStack() })
        }

        // ── FRIENDS ──────────────────────────────────────
        composable(Screen.Friends.route) {
            FriendScreen(viewModel = travelViewModel, prefs = prefs, onBack = { navController.popBackStack() })
        }

        // ── TRIP DETAIL (stub) ────────────────────────────
        composable(Screen.TripDetail.route) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("trip_id") ?: return@composable
            TripDetailScreen(tripId = tripId, navController = navController, viewModel = travelViewModel)
        }

        // ── HOTEL LIST ────────────────────────────────────
        composable(Screen.HotelList.route) {
            val hotels    by hotelViewModel.hotels.collectAsState()
            val isLoading by hotelViewModel.isLoading.collectAsState()
            ListHotelScreen(
                hotels    = hotels,
                isLoading = isLoading,
                onBack    = { navController.popBackStack() },
                onHotelClick = { hotel ->
                    selectedHotel = hotel
                    navController.navigate(Screen.HotelDetail.route)
                }
            )
        }

        // ── HOTEL DETAIL ──────────────────────────────────
        composable(Screen.HotelDetail.route) {
            selectedHotel?.let { hotel ->
                HotelDetailScreen(hotel = hotel, onBackClick = { navController.popBackStack() })
            }
        }


        // ── CREATE PLAN ───────────────────────────────────
        composable(Screen.CreatePlan.route) {
            CreatePlanScreen(
                viewModel       = tripViewModel,
                onStartPlanning = { newTripId ->
                    navController.navigate(Screen.PlanDetail.createRoute(newTripId))
                }
            )
        }

        // ── PLAN DETAIL ───────────────────────────────────
        composable(
            route     = "plan_detail/{tripId}",
            arguments = listOf(navArgument("tripId") { type = NavType.StringType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
            PlanDetailScreen(viewModel = planDetailViewModel, tripId = tripId, onBack = { navController.popBackStack() })
        }
    }
}
