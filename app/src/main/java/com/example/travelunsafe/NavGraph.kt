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
}

@Composable
fun NavGraph(navController: NavHostController) {
    // ── ViewModels ────────────────────────────────────────
    val travelViewModel: TravelViewModel       = viewModel()
    val hotelViewModel: HotelViewModel         = viewModel()
    val tripViewModel: TripViewModel           = viewModel()
    val planDetailViewModel: PlanDetailViewModel = viewModel()

    val context = LocalContext.current
    val prefs   = remember { SharedPreferencesManager(context) }

    val startDestination = if (prefs.isLoggedIn()) Screen.Main.route else Screen.Login.route

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
            val hotels       by hotelViewModel.hotels.collectAsState()
            val isLoading    by hotelViewModel.isLoading.collectAsState()
            val isFallback   by hotelViewModel.isFallbackMode.collectAsState()
            val searchedProv by hotelViewModel.searchedProvince.collectAsState()

            // ✅ เพิ่มตรงนี้ — โหลดโรงแรมทั้งหมดทันทีที่เข้าหน้า
            LaunchedEffect(Unit) {
                hotelViewModel.searchHotels("")
            }

            ListHotelScreen(
                hotels           = hotels,
                isLoading        = isLoading,
                isFallbackMode   = isFallback,
                searchedProvince = searchedProv,
                onBack           = { navController.popBackStack() },
                onHotelClick     = { hotel ->
                    selectedHotel = hotel
                    navController.navigate(Screen.HotelDetail.route)
                },
                onApplyFilter = { minPrice, maxPrice, maxGuest -> // ✅ เพิ่ม
                    hotelViewModel.filterHotels(minPrice, maxPrice, maxGuest)
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
                navController   = navController,
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
