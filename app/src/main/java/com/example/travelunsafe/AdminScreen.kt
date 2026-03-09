package com.example.travelunsafe

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class AdminTab(val label: String, val icon: ImageVector) {
    DASHBOARD("ภาพรวม", Icons.Default.Dashboard),
    USERS("ผู้ใช้", Icons.Default.People),
    TRIPS("ทริป", Icons.Default.Map),
    HOTELS("โรงแรม", Icons.Default.Hotel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: AdminViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val users by viewModel.users.collectAsState()
    val trips by viewModel.trips.collectAsState()
    val hotels by viewModel.hotels.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMsg by viewModel.errorMsg.collectAsState()
    val successMsg by viewModel.successMsg.collectAsState()

    var selectedTab by remember { mutableStateOf(AdminTab.DASHBOARD) }

    LaunchedEffect(selectedTab) {
        when (selectedTab) {
            AdminTab.USERS -> viewModel.loadUsers()
            AdminTab.TRIPS -> viewModel.loadTrips()
            AdminTab.HOTELS -> viewModel.loadHotels()
            AdminTab.DASHBOARD -> {
                viewModel.loadUsers()
                viewModel.loadTrips()
                viewModel.loadHotels()
            }
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(errorMsg, successMsg) {
        errorMsg?.let { snackbarHostState.showSnackbar(it); viewModel.clearMessages() }
        successMsg?.let { snackbarHostState.showSnackbar(it); viewModel.clearMessages() }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AdminPanelSettings, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(8.dp))
                        Text("Admin Panel", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                actions = { IconButton(onClick = onLogout) { Icon(Icons.Default.ExitToApp, "Logout", tint = MaterialTheme.colorScheme.error) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            )
        },
        bottomBar = {
            NavigationBar {
                AdminTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label, fontSize = 11.sp) }
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                AdminTab.DASHBOARD -> AdminDashboardTab(users.size, trips.size, hotels.size, isLoading)
                AdminTab.USERS -> AdminUsersTab(users, isLoading, onDelete = { viewModel.deleteUser(it) })
                AdminTab.TRIPS -> AdminTripsTab(trips, isLoading, onDelete = { viewModel.deleteTrip(it) }) // แก้ให้เป็น onDelete
                AdminTab.HOTELS -> AdminHotelsTab(hotels, isLoading, onDelete = { viewModel.deleteHotel(it) }) // แก้ให้เป็น onDelete
            }
        }
    }
}