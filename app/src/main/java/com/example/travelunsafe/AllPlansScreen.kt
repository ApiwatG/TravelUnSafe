package com.example.travelunsafe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Gradient colors per card index so each trip card looks different
private val cardGradients = listOf(
    listOf(Color(0xFF80CBC4), Color(0xFF26A69A)),  // teal
    listOf(Color(0xFF90CAF9), Color(0xFF1E88E5)),  // blue
    listOf(Color(0xFFA5D6A7), Color(0xFF43A047)),  // green
    listOf(Color(0xFFFFCC80), Color(0xFFFB8C00)),  // orange
    listOf(Color(0xFFEF9A9A), Color(0xFFE53935)),  // red
    listOf(Color(0xFFCE93D8), Color(0xFF8E24AA)),  // purple
)

@Composable
fun AllPlansScreen(
    viewModel: TravelViewModel,
    prefs: SharedPreferencesManager,
    onBack: () -> Unit,
    onTripClick: (String) -> Unit = {}
) {
    val userId = prefs.getUserId()

    LaunchedEffect(userId) {
        if (userId.isNotBlank()) {
            viewModel.loadTrips(userId)  // load only this user's trips
        } else {
            viewModel.loadTrips()        // fallback: load all
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // ===== TOP BAR =====
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 4.dp, vertical = 8.dp)
        ) {
            // Back button left
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF212121)
                )
            }

            // Title center
            Text(
                text = "แผนทั้งหมด",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121),
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // ===== CONTENT =====
        when {
            viewModel.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF42A5F5))
                }
            }

            viewModel.trips.isEmpty() -> {
                // Empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🗺️", fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "ยังไม่มีแผนการเดินทาง",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "กดปุ่ม + เพื่อสร้างแผนใหม่",
                            fontSize = 14.sp,
                            color = Color(0xFF757575)
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        horizontal = 16.dp,
                        vertical = 12.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        items = viewModel.trips,
                        key = { it.trip_id }
                    ) { trip ->
                        val gradientIndex = viewModel.trips.indexOf(trip) % cardGradients.size
                        TripCard(
                            trip = trip,
                            gradientColors = cardGradients[gradientIndex],
                            onClick = { onTripClick(trip.trip_id) }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

// ===== TRIP CARD =====
@Composable
fun TripCard(
    trip: Trip,
    gradientColors: List<Color>,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ===== IMAGE / THUMBNAIL =====
        Box(
            modifier = Modifier
                .size(width = 130.dp, height = 100.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Brush.linearGradient(gradientColors)),
            contentAlignment = Alignment.Center
        ) {
            // Replace with AsyncImage when you have image URLs:
            // AsyncImage(
            //     model = trip.image_url,
            //     contentDescription = trip.trip_name,
            //     modifier = Modifier.fillMaxSize(),
            //     contentScale = ContentScale.Crop
            // )
            Icon(
                imageVector = Icons.Default.Map,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // ===== TRIP INFO =====
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = trip.trip_name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Date range
            val dateText = formatDateRange(trip.start_date, trip.end_date)
            if (dateText.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = Color(0xFF9E9E9E),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = dateText,
                        fontSize = 13.sp,
                        color = Color(0xFF757575)
                    )
                }
            }
        }
    }
}

// ===== DATE FORMATTER =====
// Converts "2025-06-01" → "26/1" style like the design
fun formatDateRange(startDate: String?, endDate: String?): String {
    if (startDate == null && endDate == null) return ""

    fun shortDate(d: String?): String {
        if (d.isNullOrBlank()) return ""
        return try {
            val parts = d.split("-")
            val day = parts[2].trimStart('0')
            val month = parts[1].trimStart('0')
            "$day/$month"
        } catch (e: Exception) { d }
    }

    val start = shortDate(startDate)
    val end = shortDate(endDate)

    return when {
        start.isNotBlank() && end.isNotBlank() -> "วัน $start - $end"
        start.isNotBlank() -> "วัน $start"
        else -> ""
    }
}
