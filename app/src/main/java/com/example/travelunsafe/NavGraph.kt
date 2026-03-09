package com.example.travelunsafe

import android.content.Intent
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
    object PlaceDetailScreen : Screen("place_detail/{placeId}") {
        fun createRoute(placeId: String) = "place_detail/$placeId"
    }
    // ── Hotel ─────────────────────────────────────────────
    object HotelList   : Screen("hotel_list")
    object HotelDetail : Screen("hotel_detail")
    // ── Travel Plan ───────────────────────────────────────
    object CreatePlan  : Screen("create_plan")
    object PlanDetail  : Screen("plan_detail/{tripId}") {
        fun createRoute(tripId: String) = "plan_detail/$tripId"
    }
    object HotelListFromPlan : Screen("hotel_list_from_plan/{province}/{tripId}") {
        fun createRoute(province: String, tripId: String) = "hotel_list_from_plan/$province/$tripId"
    }



    // ── Guide ─────────────────────────────────────────────
    object CreateGuide : Screen("create_guide")
    object GuideDetail : Screen("guide_detail/{guideId}") {
        fun createRoute(guideId: String) = "guide_detail/$guideId"
    }


}

@Composable
fun NavGraph(navController: NavHostController) {
    // ── ViewModels ────────────────────────────────────────
    val travelViewModel: TravelViewModel         = viewModel()
    val hotelViewModel: HotelViewModel           = viewModel()
    val tripViewModel: TripViewModel             = viewModel()
    val planDetailViewModel: PlanDetailViewModel  = viewModel()
    val ratingMap by hotelViewModel.ratingMap.collectAsState()

    val context = LocalContext.current
    val prefs   = remember { SharedPreferencesManager(context) }

    val startDestination = if (prefs.isLoggedIn()) Screen.Main.route else Screen.Login.route

    // Shared hotel state
    var selectedHotel by remember { mutableStateOf<Hotel?>(null) }
    var selectedTripId by remember { mutableStateOf<String?>(null) }

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
                viewModel               = travelViewModel,
                prefs                   = prefs,
                onNavigateToSearch      = { navController.navigate(Screen.Search.route) },
                onNavigateToFriends     = { navController.navigate(Screen.Friends.route) },
                onNavigateToHotels      = { navController.navigate(Screen.HotelList.route) },
                onNavigateToCreatePlan  = { navController.navigate(Screen.CreatePlan.route) },
                onNavigateToCreateGuide = { navController.navigate(Screen.CreateGuide.route) },
                onNavigateToGuideDetail = { guideId ->          // ✅ เพิ่ม
                    navController.navigate(Screen.GuideDetail.createRoute(guideId))
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ── SEARCH ───────────────────────────────────────
        composable(Screen.Search.route) {
            SearchScreen(
                viewModel = travelViewModel,
                prefs = prefs,                    // ← ADD THIS
                onBack = { navController.popBackStack() },
                onGuideClick = { guideId ->
                    navController.navigate(Screen.GuideDetail.createRoute(guideId))
                },
                onPlaceClick = { placeId ->
                    navController.navigate(Screen.PlaceDetailScreen.createRoute(placeId))
                }
            )
        }

        // ── FRIENDS ──────────────────────────────────────
        composable(Screen.Friends.route) {
            FriendScreen(viewModel = travelViewModel, prefs = prefs, onBack = { navController.popBackStack() })
        }

        // ── TRIP DETAIL ───────────────────────────────────
        composable(Screen.TripDetail.route) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("trip_id") ?: return@composable
            TripDetailScreen(tripId = tripId, navController = navController, viewModel = travelViewModel)
        }

        // ── HOTEL LIST (จากหน้าหลัก) ──────────────────────────────
        composable(Screen.HotelList.route) {
            val hotels       by hotelViewModel.hotels.collectAsState()
            val isLoading    by hotelViewModel.isLoading.collectAsState()
            val isFallback   by hotelViewModel.isFallbackMode.collectAsState()
            val searchedProv by hotelViewModel.searchedProvince.collectAsState()

            LaunchedEffect(Unit) {
                hotelViewModel.searchHotels("")
            }

            ListHotelScreen(
                hotels           = hotels,
                isLoading        = isLoading,
                isFallbackMode   = isFallback,
                searchedProvince = searchedProv,
                province         = "",           // ✅ เพิ่ม — ไม่มีจังหวัด แสดง "ทั้งหมด"
                onBack           = { navController.popBackStack() },
                onHotelClick     = { hotel ->
                    selectedHotel = hotel
                    selectedTripId = null   // ✅ มาจากหน้าหลัก ไม่มี tripId
                    navController.navigate(Screen.HotelDetail.route)
                },
                onApplyFilter = { minPrice, maxPrice, maxGuest, minRating ->  // ✅ เพิ่ม minRating
                    hotelViewModel.filterHotels(minPrice, maxPrice, maxGuest, minRating)
                }
            )
        }

        // ── HOTEL DETAIL ──────────────────────────────────
        composable(Screen.HotelDetail.route) {
            val context = LocalContext.current
            val prefs = remember { SharedPreferencesManager(context) }

            // 💡 ตรวจสอบว่ามีโรงแรมที่ถูกเลือก (selectedHotel) หรือไม่
            selectedHotel?.let { hotel ->
                HotelDetailScreen(
                    hotel       = hotel,
                    // 💡 แก้ไขตรงนี้: ใช้ selectedTripId ที่เราเก็บไว้ตอนเลือกโรงแรม
                    tripId      = selectedTripId,
                    userId      = prefs.getUserId(),
                    viewModel   = travelViewModel,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // ── CREATE PLAN ───────────────────────────────────
        composable(Screen.CreatePlan.route) {
            CreatePlanScreen(
                viewModel       = tripViewModel,
                prefs           = prefs,
                onStartPlanning = { newTripId ->
                    navController.navigate(Screen.PlanDetail.createRoute(newTripId))
                }
            )
        }

        // ── PLAN DETAIL ───────────────────────────────────
        composable(
            route = Screen.PlaceDetailScreen.route,
            arguments = listOf(navArgument("placeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val placeId = backStackEntry.arguments?.getString("placeId") ?: return@composable
            PlaceDetailScreen(
                placeId = placeId,
                viewModel = travelViewModel,
                prefs = prefs,
                onBack = { navController.popBackStack() }
            )
        }


        composable(
            route     = "plan_detail/{tripId}",
            arguments = listOf(navArgument("tripId") { type = NavType.StringType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId") ?: ""

            PlanDetailScreen(
                viewModel          = planDetailViewModel,
                tripId             = tripId,
                onBack             = { navController.popBackStack() },
                onNavigateToHotels = { province, tripIdArg -> // 💡 ใส่ 2 ตัวแปร

                        val encoded = java.net.URLEncoder.encode(province, "UTF-8")
                    navController.navigate(Screen.HotelListFromPlan.createRoute(encoded, tripId))

                }
            )
        }

        // ── CREATE GUIDE ──────────────────────────────────
        composable(Screen.CreateGuide.route) {
            CreateGuideScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // ── GUIDE DETAIL ──────────────────────────────────
        composable(
            route     = Screen.GuideDetail.route,
            arguments = listOf(navArgument("guideId") { type = NavType.StringType })
        ) { backStackEntry ->
            val guideId = backStackEntry.arguments?.getString("guideId") ?: return@composable
            // Find the guide from loaded list
            val guide = travelViewModel.guides.find { it.guide_id == guideId }
            if (guide != null) {
                val guideViewModel: GuideViewModel = viewModel()
                LaunchedEffect(guideId) { guideViewModel.loadGuide(guide) }
                GuideDetailComposable(
                    uiState = guideViewModel.uiState,
                    onBack  = { navController.popBackStack() }
                )
            }
        }

        // ── HOTEL LIST (จากหน้า Plan) ──────────────────────────────
        composable(
            route = "hotel_list_from_plan/{province}/{tripId}",  // ✅ รับ tripId ด้วย
            arguments = listOf(
                navArgument("province") { type = NavType.StringType },
                navArgument("tripId")   { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val provinceRaw = backStackEntry.arguments?.getString("province") ?: "all"
            val province = java.net.URLDecoder.decode(provinceRaw, "UTF-8")
                .let { if (it == "all") "" else it }
            val tripId = backStackEntry.arguments?.getString("tripId") ?: ""

            val hotels       by hotelViewModel.hotels.collectAsState()
            val isLoading    by hotelViewModel.isLoading.collectAsState()
            val isFallback   by hotelViewModel.isFallbackMode.collectAsState()
            val searchedProv by hotelViewModel.searchedProvince.collectAsState()

            LaunchedEffect(province) {
                if (province.isBlank()) {
                hotelViewModel.searchHotels("")
            } else {
                hotelViewModel.setInitialProvince(province)
            }
            }

            ListHotelScreen(
                hotels           = hotels,
                isLoading        = isLoading,
                isFallbackMode   = isFallback,
                searchedProvince = searchedProv,
                ratingMap = ratingMap,
                province         = "",
                onBack           = { navController.popBackStack() },
                onHotelClick     = { hotel ->
                    selectedHotel = hotel
                    selectedTripId = tripId
                    navController.navigate(Screen.HotelDetail.route)
                },
                onApplyFilter = { minPrice, maxPrice, maxGuest, minRating ->
                    hotelViewModel.filterHotels(minPrice, maxPrice, maxGuest, minRating)
                }
            )
        }

    }
}
