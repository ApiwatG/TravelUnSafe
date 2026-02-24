package com.example.travelunsafe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.travelunsafe.ui.theme.TravelUnSafeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TravelUnSafeTheme {
                MainAppContainer()
            }
        }
    }
}

@Composable
fun MainAppContainer() {
    val navController = rememberNavController()
    var currentDestination by remember { mutableStateOf<NavDestination>(NavDestination.Home) }
    var showFabMenu by remember { mutableStateOf(false) }

    val tripViewModel: TripViewModel = viewModel()
    val planDetailViewModel: PlanDetailViewModel = viewModel()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                TravelBottomNavBar(
                    currentDestination = currentDestination,
                    onItemSelected = { currentDestination = it },
                    onFabClick = { showFabMenu = !showFabMenu },
                    isFabOpen = showFabMenu
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "create_plan",
                modifier = Modifier.padding(innerPadding)
            ) {
                // 📍 หน้าที่ 1: สร้างแผน
                composable("create_plan") {
                    CreatePlanScreen(
                        viewModel = tripViewModel,
                        onStartPlanning = { newTripId ->
                            // 💡 ส่ง ID ที่ได้จากการสร้างทริป พ่วงไปกับชื่อเส้นทาง
                            navController.navigate("plan_detail/$newTripId")
                        }
                    )
                }

                // 📍 หน้าที่ 2: รายละเอียดแผน (💡 เพิ่ม arguments เพื่อรับค่า tripId)
                composable(
                    route = "plan_detail/{tripId}",
                    arguments = listOf(navArgument("tripId") { type = NavType.StringType })
                ) { backStackEntry ->
                    // ดึงค่า ID ออกมา
                    val tripId = backStackEntry.arguments?.getString("tripId") ?: ""

                    PlanDetailScreen(
                        viewModel = planDetailViewModel,
                        tripId = tripId, // ส่ง ID จริงเข้า ViewModel ของหน้านี้
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }

        FabPopupMenu(
            visible = showFabMenu,
            onDismiss = { showFabMenu = false },
            onGuideClick = { showFabMenu = false },
            onTravelPlanClick = {
                navController.navigate("create_plan")
                showFabMenu = false
            }
        )
    }
}