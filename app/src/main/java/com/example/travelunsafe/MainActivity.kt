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
import com.example.travelunsafe.ui.theme.TravelUnSafeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TravelUnSafeTheme {
                TravelApp()
            }
        }
    }
}

@Composable
fun TravelApp() {
    var currentDestination by remember { mutableStateOf<NavDestination>(NavDestination.Home) }
    var showFabMenu by remember { mutableStateOf(false) }

    // Wrap in Box so FabPopupMenu can float above everything
    Box(modifier = Modifier.fillMaxSize()) {

        Scaffold(
            bottomBar = {
                // ✅ Just call TravelBottomNavBar here — one line!
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
                    NavDestination.Home      -> HomeScreen()
                    NavDestination.Messages  -> PlaceholderScreen("แชท")
                    NavDestination.Favorites -> PlaceholderScreen("ถูกใจ")
                    NavDestination.Profile   -> ProfileScreen()
                }
            }
        }

        // ✅ FAB popup floats above the Scaffold
        FabPopupMenu(
            visible = showFabMenu,
            onDismiss = { showFabMenu = false },
            onGuideClick = {
                // TODO: navigate to guide creation
            },
            onTravelPlanClick = {
                // TODO: navigate to travel plan creation
            }
        )
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    androidx.compose.foundation.layout.Box(
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
