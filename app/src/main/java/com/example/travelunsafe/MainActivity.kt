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
import androidx.navigation.compose.rememberNavController
import com.example.travelunsafe.ui.theme.TravelUnSafeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TravelUnSafeTheme {
                // NavGraph owns the ONE navController — everything routes through here
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}

// ===================================================
//  TRAVEL APP — Bottom Nav Shell
//
//  ✅ CORRECT PATTERN: receives lambdas, NOT navController
//  NavGraph calls this and passes navigate() calls as lambdas.
//  This way TravelApp never touches a navController directly,
//  so there's no "graph not set" crash.
// ===================================================
@Composable
fun TravelApp(
    viewModel: TravelViewModel,
    prefs: SharedPreferencesManager,
    onNavigateToSearch: () -> Unit,
    onNavigateToFriends: () -> Unit,
    onNavigateToHotels: () -> Unit,
    onNavigateToCreatePlan: () -> Unit,
    onNavigateToCreateGuide: () -> Unit,   // ← NEW
    onNavigateToGuideDetail: (String) -> Unit,
    onLogout: () -> Unit
) {
    var currentDestination by remember { mutableStateOf<NavDestination>(NavDestination.Home) }
    var showFabMenu by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {

        Scaffold(
            bottomBar = {
                TravelBottomNavBar(
                    currentDestination = currentDestination,
                    onItemSelected = {
                        currentDestination = it
                        showFabMenu = false
                    },
                    onFabClick = { showFabMenu = !showFabMenu },
                    isFabOpen = showFabMenu
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (currentDestination) {
                    NavDestination.Home -> HomeScreen(
                        viewModel      = viewModel,
                        prefs          = prefs,
                        onSearchClick  = onNavigateToSearch,
                        onHotelsClick  = onNavigateToHotels,
                        onGuideClick  = onNavigateToGuideDetail

                    )
                    NavDestination.Messages  -> PlaceholderScreen("แชท")
                    NavDestination.Favorites -> FavoritePlaceScreen(
                        viewModel = viewModel,
                        prefs     = prefs
                    )
                    NavDestination.Profile -> ProfileScreen(
                        viewModel      = viewModel,
                        prefs          = prefs,
                        onLogout       = {
                            viewModel.logout()
                            prefs.logout()
                            onLogout()
                        },
                        onFriendsClick = onNavigateToFriends
                    )
                }
            }
        }

        FabPopupMenu(
            visible   = showFabMenu,
            onDismiss = { showFabMenu = false },
            onGuideClick = {
                showFabMenu = false
                onNavigateToCreateGuide()                   // ✅ NOW WIRED!
            },
            onTravelPlanClick = {
                showFabMenu = false
                onNavigateToCreatePlan()
            }
        )
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.Text(
            text = title,
            fontSize = androidx.compose.ui.unit.TextUnit(24f, androidx.compose.ui.unit.TextUnitType.Sp),
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
    }
}