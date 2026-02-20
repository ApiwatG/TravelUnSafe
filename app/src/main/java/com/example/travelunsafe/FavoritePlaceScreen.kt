package com.example.travelunsafe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Gradient per category type
private fun categoryGradient(category: String?): List<Color> = when {
    category?.contains("Beach", ignoreCase = true) == true ->
        listOf(Color(0xFF80DEEA), Color(0xFF0097A7))
    category?.contains("Mountain", ignoreCase = true) == true ->
        listOf(Color(0xFFA5D6A7), Color(0xFF388E3C))
    category?.contains("Temple", ignoreCase = true) == true ->
        listOf(Color(0xFFFFCC80), Color(0xFFEF6C00))
    category?.contains("Park", ignoreCase = true) == true ->
        listOf(Color(0xFFC5E1A5), Color(0xFF558B2F))
    category?.contains("Museum", ignoreCase = true) == true ->
        listOf(Color(0xFFCE93D8), Color(0xFF6A1B9A))
    else -> listOf(Color(0xFF90CAF9), Color(0xFF1565C0))
}

@Composable
fun FavoritePlaceScreen(
    viewModel: TravelViewModel,
    prefs: SharedPreferencesManager
) {
    val context = LocalContext.current
    val userId = prefs.getUserId()

    LaunchedEffect(userId) {
        if (userId.isNotBlank()) {
            viewModel.loadFavorites(userId)
        }
    }

    val favorites = viewModel.favorites
    val isLoading = viewModel.isLoading

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
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = "สถานที่ที่ถูกใจ",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121),
                modifier = Modifier.align(Alignment.CenterStart)
            )
            Text(
                text = "${favorites.size} แห่ง",
                fontSize = 14.sp,
                color = Color(0xFF9E9E9E),
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }

        HorizontalDivider(color = Color(0xFFEEEEEE))

        // ===== CONTENT =====
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF42A5F5))
                }
            }

            favorites.isEmpty() -> {
                // Empty state
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🤍", fontSize = 72.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "ยังไม่มีสถานที่ที่ถูกใจ",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "กดหัวใจที่สถานที่เพื่อเพิ่มลงรายการ",
                            fontSize = 14.sp,
                            color = Color(0xFF9E9E9E)
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = favorites,
                        key = { it.favorite_id }
                    ) { fav ->
                        FavoritePlaceCard(
                            favorite = fav,
                            onRemove = {
                                viewModel.removeFavorite(context, fav.favorite_id, userId)
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

// ===== FAVORITE PLACE CARD =====
@Composable
fun FavoritePlaceCard(
    favorite: FavoritePlace,
    onRemove: () -> Unit
) {
    var showConfirm by remember { mutableStateOf(false) }
    val gradient = categoryGradient(favorite.category_place_name)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF9F9F9))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail
        Box(
            modifier = Modifier
                .size(width = 90.dp, height = 70.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Brush.linearGradient(gradient)),
            contentAlignment = Alignment.Center
        ) {
            // Replace with AsyncImage when image_url is available
            val emoji = when {
                favorite.category_place_name?.contains("Beach", ignoreCase = true) == true -> "🏖️"
                favorite.category_place_name?.contains("Mountain", ignoreCase = true) == true -> "⛰️"
                favorite.category_place_name?.contains("Temple", ignoreCase = true) == true -> "🛕"
                favorite.category_place_name?.contains("Park", ignoreCase = true) == true -> "🌳"
                favorite.category_place_name?.contains("Museum", ignoreCase = true) == true -> "🏛️"
                else -> "📍"
            }
            Text(text = emoji, fontSize = 28.sp)
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = favorite.place_name,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF212121),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (!favorite.location.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF9E9E9E),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = favorite.location,
                        fontSize = 12.sp,
                        color = Color(0xFF9E9E9E),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            if (!favorite.provinces_name.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = favorite.provinces_name,
                    fontSize = 11.sp,
                    color = Color(0xFF42A5F5),
                    fontWeight = FontWeight.Medium
                )
            }
            if (!favorite.category_place_name.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE3F2FD))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = favorite.category_place_name,
                        fontSize = 10.sp,
                        color = Color(0xFF1565C0),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Remove (heart) button
        IconButton(onClick = { showConfirm = true }) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Remove favorite",
                tint = Color(0xFFE53935)
            )
        }
    }

    // Confirm dialog
    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("ลบออกจากรายการโปรด?", fontWeight = FontWeight.Bold) },
            text = { Text("คุณต้องการลบ \"${favorite.place_name}\" ออกจากรายการโปรดใช่ไหม?") },
            confirmButton = {
                TextButton(onClick = {
                    showConfirm = false
                    onRemove()
                }) {
                    Text("ลบ", color = Color(0xFFE53935), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) {
                    Text("ยกเลิก", color = Color(0xFF757575))
                }
            }
        )
    }
}
