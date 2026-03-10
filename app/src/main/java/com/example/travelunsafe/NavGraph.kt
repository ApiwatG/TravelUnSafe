package com.example.travelunsafe

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

sealed class Screen(val route: String) {
    object Login       : Screen("login")
    object Register    : Screen("register")
    object Main        : Screen("main")
    object Admin       : Screen("admin")
    object Search      : Screen("search")
    object Friends     : Screen("friends")
    object TripDetail  : Screen("trip_detail/{trip_id}") {
        fun createRoute(tripId: String) = "trip_detail/$tripId"
    }
    object PlaceDetailScreen : Screen("place_detail/{placeId}") {
        fun createRoute(placeId: String) = "place_detail/$placeId"
    }
    object HotelList   : Screen("hotel_list")
    object HotelDetail : Screen("hotel_detail")
    object CreatePlan  : Screen("create_plan")
    object PlanDetail  : Screen("plan_detail/{tripId}") {
        fun createRoute(tripId: String) = "plan_detail/$tripId"
    }
    object HotelListFromPlan : Screen("hotel_list_from_plan/{province}/{tripId}") {
        fun createRoute(province: String, tripId: String) = "hotel_list_from_plan/$province/$tripId"
    }
    object CreateGuide : Screen("create_guide")
    object GuideDetail : Screen("guide_detail/{guideId}") {
        fun createRoute(guideId: String) = "guide_detail/$guideId"
    }

    object EditGuide : Screen("edit_guide/{guideId}") {
        fun createRoute(guideId: String) = "edit_guide/$guideId"
    }
}

@Composable
fun NavGraph(navController: NavHostController) {
    val travelViewModel: TravelViewModel         = viewModel()
    val adminViewModel: AdminViewModel           = viewModel()
    val hotelViewModel: HotelViewModel           = viewModel()
    val tripViewModel: TripViewModel             = viewModel()
    val planDetailViewModel: PlanDetailViewModel  = viewModel()
    val ratingMap by hotelViewModel.ratingMap.collectAsState()

    val context = LocalContext.current
    val prefs   = remember { SharedPreferencesManager(context) }

    val startDestination = if (prefs.isLoggedIn()) Screen.Main.route else Screen.Login.route

    var selectedHotel  by remember { mutableStateOf<Hotel?>(null) }
    var selectedTripId by remember { mutableStateOf<String?>(null) }

    NavHost(navController = navController, startDestination = startDestination) {

        // ── AUTH ─────────────────────────────────────────
        composable(Screen.Login.route) {
            LoginScreen(navController = navController, viewModel = travelViewModel, prefs = prefs)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController = navController, viewModel = travelViewModel, prefs = prefs)
        }

        // ── ADMIN ─────────────────────────────────────────
        composable(Screen.Admin.route) {
            AdminScreen(
                // แก้ไขจาก TravelViewModel (ชื่อ Class) เป็น travelViewModel (ตัวแปร)
                viewModel = adminViewModel,
                onBack    = { navController.popBackStack() },
                onLogout  = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ── MAIN ───────────────────────────────────────────
        composable(Screen.Main.route) {
            TravelApp(
                viewModel               = travelViewModel,
                prefs                   = prefs,
                onNavigateToSearch      = { navController.navigate(Screen.Search.route) },
                onNavigateToFriends     = { navController.navigate(Screen.Friends.route) },
                onNavigateToHotels      = { navController.navigate(Screen.HotelList.route) },
                onNavigateToHotelDetail = { hotel ->
                    selectedHotel  = hotel
                    selectedTripId = null
                    navController.navigate(Screen.HotelDetail.route)
                },
                onNavigateToPlaceDetail = { placeId ->
                    navController.navigate(Screen.PlaceDetailScreen.createRoute(placeId))
                },
                onNavigateToCreatePlan  = { navController.navigate(Screen.CreatePlan.route) },
                onNavigateToCreateGuide = { navController.navigate(Screen.CreateGuide.route) },
                onNavigateToGuideDetail = { guideId ->
                    navController.navigate(Screen.GuideDetail.createRoute(guideId))
                },
                onNavigateToPlanDetail  = { tripId ->
                    navController.navigate(Screen.PlanDetail.createRoute(tripId))
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
                viewModel    = travelViewModel,
                prefs        = prefs,
                onBack       = { navController.popBackStack() },
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

        // ── HOTEL LIST (from home) ──────────────────────────────
        composable(Screen.HotelList.route) {
            val hotels       by hotelViewModel.hotels.collectAsState()
            val isLoading    by hotelViewModel.isLoading.collectAsState()
            val isFallback   by hotelViewModel.isFallbackMode.collectAsState()
            val searchedProv by hotelViewModel.searchedProvince.collectAsState()

            LaunchedEffect(Unit) { hotelViewModel.searchHotels("") }

            ListHotelScreen(
                hotels           = hotels,
                isLoading        = isLoading,
                isFallbackMode   = isFallback,
                searchedProvince = searchedProv,
                province         = "",
                onBack           = { navController.popBackStack() },
                onHotelClick     = { hotel ->
                    selectedHotel  = hotel
                    selectedTripId = null
                    navController.navigate(Screen.HotelDetail.route)
                },
                onApplyFilter = { minPrice, maxPrice, maxGuest, minRating ->
                    hotelViewModel.filterHotels(minPrice, maxPrice, maxGuest, minRating)
                }
            )
        }

        // ── HOTEL DETAIL ──────────────────────────────────
        composable(Screen.HotelDetail.route) {
            val ctx   = LocalContext.current
            val lPrefs = remember { SharedPreferencesManager(ctx) }
            selectedHotel?.let { hotel ->
                HotelDetailScreen(
                    hotel         = hotel,
                    tripId        = selectedTripId,
                    userId        = lPrefs.getUserId(),
                    viewModel     = travelViewModel,
                    showAddButton = selectedTripId != null,
                    onBackClick   = { navController.popBackStack() }
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

        // ── PLACE DETAIL ──────────────────────────────────
        composable(
            route     = Screen.PlaceDetailScreen.route,
            arguments = listOf(navArgument("placeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val placeId = backStackEntry.arguments?.getString("placeId") ?: return@composable
            PlaceDetailScreen(
                placeId   = placeId,
                viewModel = travelViewModel,
                prefs     = prefs,
                onBack    = { navController.popBackStack() }
            )
        }

        // ── PLAN DETAIL ───────────────────────────────────
        composable(
            route = Screen.PlanDetail.route,
            arguments = listOf(
                navArgument("tripId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId") ?: ""

            PlanDetailScreen(
                viewModel = planDetailViewModel,
                tripId = tripId,

                onBack = { navController.popBackStack() },

                onNavigateToHotels = { province, _ ->

                    val safeProvince =
                        if (province.isBlank()) "all" else province

                    val encoded =
                        java.net.URLEncoder.encode(safeProvince, "UTF-8")

                    navController.navigate(
                        Screen.HotelListFromPlan.createRoute(encoded, tripId)
                    )
                }
            )
        }

        // ── CREATE GUIDE ──────────────────────────────────
        composable(Screen.CreateGuide.route) {
            CreateGuideScreen(
                onBackClick = { navController.popBackStack() },
                onSuccessFinish = {
                    // แนะนำให้โหลดข้อมูลใหม่หลังสร้างเสร็จ
                    travelViewModel.loadGuides()
                    navController.popBackStack()
                }
            )
        }

        // ── GUIDE DETAIL ──────────────────────────────────
        composable(
            route     = Screen.GuideDetail.route,
            arguments = listOf(navArgument("guideId") { type = NavType.StringType })
        ) { backStackEntry ->
            val guideId = backStackEntry.arguments?.getString("guideId") ?: return@composable
            GuideDetailScreen(
                guideId            = guideId,
                viewModel          = travelViewModel,
                sharedPrefsManager = prefs,
                onBack             = { navController.popBackStack() }
            )
        }

        // ── HOTEL LIST (from Plan) ──────────────────────────────
        composable(
            route     = "hotel_list_from_plan/{province}/{tripId}",
            arguments = listOf(
                navArgument("province") { type = NavType.StringType },
                navArgument("tripId")   { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val provinceRaw = backStackEntry.arguments?.getString("province") ?: "all"
            val province    = java.net.URLDecoder.decode(provinceRaw, "UTF-8")
                .let { if (it == "all") "" else it }
            val tripId      = backStackEntry.arguments?.getString("tripId") ?: ""

            val hotels       by hotelViewModel.hotels.collectAsState()
            val isLoading    by hotelViewModel.isLoading.collectAsState()
            val isFallback   by hotelViewModel.isFallbackMode.collectAsState()
            val searchedProv by hotelViewModel.searchedProvince.collectAsState()

            LaunchedEffect(province) {
                if (province.isBlank()) hotelViewModel.searchHotels("")
                else hotelViewModel.setInitialProvince(province)
            }

            ListHotelScreen(
                hotels           = hotels,
                isLoading        = isLoading,
                isFallbackMode   = isFallback,
                searchedProvince = searchedProv,
                ratingMap        = ratingMap,
                province         = province,
                onBack           = { navController.popBackStack() },
                onHotelClick     = { hotel ->
                    selectedHotel  = hotel
                    selectedTripId = tripId
                    navController.navigate(Screen.HotelDetail.route)
                },
                onApplyFilter = { minPrice, maxPrice, maxGuest, minRating ->
                    hotelViewModel.filterHotels(minPrice, maxPrice, maxGuest, minRating)
                }
            )
        }
        composable(
            route = Screen.EditGuide.route,
            arguments = listOf(
                navArgument("guideId") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val guideId =
                backStackEntry.arguments?.getString("guideId") ?: return@composable

            // ค้นหา guide จาก ViewModel (หาจากหน้าหลัก หรือหน้าโปรไฟล์)
            val guide = travelViewModel.guides.find { it.guide_id == guideId }
                ?: travelViewModel.profileSummary?.guides?.find { it.guide_id == guideId }

            if (guide != null) {

                // ✅ เรียกใช้ CreateGuideScreen โดยส่งค่า editGuide = guide
                // สิ่งนี้จะทำให้หน้าจอเปลี่ยนไปอยู่ในโหมด "แก้ไข" โดยอัตโนมัติ
                CreateGuideScreen(
                    editGuide = guide,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onSuccessFinish = {
                        // เมื่อแก้ไขสำเร็จ ให้รีเฟรชข้อมูลในแอป
                        travelViewModel.loadGuides()
                        travelViewModel.loadProfile(prefs.getUserId())
                        navController.popBackStack()
                    }
                )

            } else {
                // ถ้าหา guide ไม่เจอ (เช่น เน็ตหลุดโหลดไม่ทัน) ให้เด้งกลับอัตโนมัติ
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            }
        }
    }
}