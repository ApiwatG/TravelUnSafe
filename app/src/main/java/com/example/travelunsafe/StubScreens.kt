package com.example.travelunsafe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

// ============================================================
//  STUB SCREENS — Replace each with real UI when ready
//  LoginScreen  → now in LoginScreen.kt  (real UI)
//  RegisterScreen → now in RegisterScreen.kt (real UI)
// ============================================================

// ===== TRIP DETAIL SCREEN STUB =====
// TODO: Replace with real trip detail (itinerary + expenses)
@Composable
fun TripDetailScreen(
    tripId: String,
    navController: NavHostController,
    viewModel: TravelViewModel
) {
    LaunchedEffect(tripId) {
        viewModel.loadItinerary(tripId)
        viewModel.loadExpenses(tripId)
    }
    StubScreen(
        title = "รายละเอียดทริป",
        emoji = "🗺️",
        subtitle = "trip_id: $tripId",
        buttonText = "กลับ",
        onButtonClick = { navController.popBackStack() }
    )
}

// ===== HOTEL DETAIL SCREEN STUB =====
// TODO: Replace with real hotel detail (info + reviews + booking)
@Composable
fun HotelDetailScreen(
    hotelId: String,
    navController: NavHostController,
    viewModel: TravelViewModel
) {
    LaunchedEffect(hotelId) {
        viewModel.loadReviews(hotelId)
    }
    StubScreen(
        title = "รายละเอียดโรงแรม",
        emoji = "🏨",
        subtitle = "hotel_id: $hotelId",
        buttonText = "กลับ",
        onButtonClick = { navController.popBackStack() }
    )
}

// ============================================================
//  REUSABLE STUB COMPOSABLE
// ============================================================
@Composable
private fun StubScreen(
    title: String,
    emoji: String,
    subtitle: String = "",
    buttonText: String,
    onButtonClick: () -> Unit,
    secondaryButtonText: String? = null,
    onSecondaryClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(text = emoji, fontSize = 64.sp)
            Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121))
            if (subtitle.isNotBlank()) {
                Text(text = subtitle, fontSize = 13.sp, color = Color(0xFF9E9E9E))
            }
            Text(text = "🚧 หน้านี้กำลังสร้าง", fontSize = 14.sp, color = Color(0xFF9E9E9E))
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onButtonClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF42A5F5)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = buttonText, color = Color.White, fontWeight = FontWeight.Bold)
            }
            if (secondaryButtonText != null && onSecondaryClick != null) {
                OutlinedButton(
                    onClick = onSecondaryClick,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = secondaryButtonText, color = Color(0xFF42A5F5))
                }
            }
        }
    }
}