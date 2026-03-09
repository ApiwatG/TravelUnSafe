package com.example.travelunsafe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

val BackgroundWhite = Color.White
val TitleBlack = Color(0xFF000000)
val TextPrimary = Color(0xFF212121)
val TextSecondary = Color(0xFF757575)

@Composable
fun HomeScreen(
    viewModel: TravelViewModel,
    prefs: SharedPreferencesManager,
    onSearchClick: () -> Unit = {},
    onHotelsClick: () -> Unit = {}
) {
    val username = prefs.getUsername().ifBlank { "นักท่องเที่ยว" }

    LaunchedEffect(Unit) {
        viewModel.loadPlaces()
        viewModel.loadGuides()
    }

    val topPlaces = remember(viewModel.places) {
        viewModel.places.sortedByDescending { it.view }.take(10)
    }
    val guides = viewModel.guides

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .verticalScroll(rememberScrollState())
    ) {
        HomeTopAppBar(username = username, onSearchClick = onSearchClick)
        HeroBanner()
        Spacer(modifier = Modifier.height(8.dp))

        SectionHeader(title = "สถานที่ยอดนิยม", emoji = "📍")
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(topPlaces, key = { it.place_id }) { place ->
                FeaturedPlaceCard(place = place)
            }
            if (topPlaces.isEmpty()) {
                item { EmptyRowCard("ยังไม่มีสถานที่") }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        SectionHeader(title = "ไกด์เด่นจากผู้ใช้", emoji = "📖")
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(guides, key = { it.guide_id }) { guide ->
                FeaturedGuideCard(guide = guide)
            }
            if (guides.isEmpty()) {
                item { EmptyRowCard("ยังไม่มีคู่มือ") }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        SectionHeader(title = "โรงแรมและที่พัก", emoji = "🏨")
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onHotelsClick) {
                Text("ดูโรงแรมทั้งหมด →", color = Color(0xFF42A5F5), fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun HomeTopAppBar(username: String = "", onSearchClick: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth().statusBarsPadding().height(64.dp).padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = "TravelUnSafe", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TitleBlack)
            if (username.isNotBlank()) {
                Text(text = "สวัสดี, $username 👋", fontSize = 13.sp, color = Color(0xFF757575))
            }
        }
        IconButton(onClick = onSearchClick) {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = TitleBlack, modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
fun HeroBanner() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(180.dp).clip(RoundedCornerShape(20.dp))
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFF1A237E), Color(0xFF0277BD)))))
        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color(0x99000000)))))
        Column(modifier = Modifier.align(Alignment.BottomStart).padding(20.dp)) {
            Text(text = "🗺️  สำรวจจุดหมายใหม่", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
            Text(text = "ค้นพบประสบการณ์การเดินทางของคุณ", fontSize = 13.sp, color = Color.White.copy(alpha = 0.85f))
        }
    }
}

@Composable
fun SectionHeader(title: String, emoji: String = "") {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        if (emoji.isNotEmpty()) { Text(text = emoji, fontSize = 18.sp); Spacer(modifier = Modifier.width(8.dp)) }
        Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TitleBlack)
    }
}

// ===== FEATURED PLACE CARD — NOW WITH ASYNCIMAGE =====
private val placeGradients = listOf(
    listOf(Color(0xFF80CBC4), Color(0xFF00897B)),
    listOf(Color(0xFF81D4FA), Color(0xFF0288D1)),
    listOf(Color(0xFFA5D6A7), Color(0xFF388E3C)),
    listOf(Color(0xFFEF9A9A), Color(0xFFE53935)),
    listOf(Color(0xFFCE93D8), Color(0xFF8E24AA)),
    listOf(Color(0xFFFFCC80), Color(0xFFEF6C00))
)

@Composable
fun FeaturedPlaceCard(place: Place) {
    val gradient = placeGradients[(place.place_id.hashCode() and 0x7FFFFFFF) % placeGradients.size]

    Column(modifier = Modifier.width(160.dp).clickable { }) {
        Box(
            modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            // ✅ FIX: Load real image if available
            if (!place.image_url.isNullOrBlank()) {
                val fullUrl = if (place.image_url.startsWith("http")) place.image_url
                else "http://192.168.1.11:3000/${place.image_url}"
                AsyncImage(
                    model = fullUrl,
                    contentDescription = place.place_name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Fallback gradient
                Box(
                    modifier = Modifier.fillMaxSize().background(Brush.linearGradient(gradient)),
                    contentAlignment = Alignment.Center
                ) { Text(text = "📍", fontSize = 36.sp) }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(text = place.place_name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        if (!place.location.isNullOrBlank()) {
            Text(text = place.location, fontSize = 12.sp, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Visibility, null, tint = Color(0xFF9E9E9E), modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text(text = "${place.view} views", fontSize = 11.sp, color = Color(0xFF9E9E9E))
        }
    }
}

// ===== FEATURED GUIDE CARD — NOW WITH ASYNCIMAGE =====
private val guideGradients = listOf(
    listOf(Color(0xFFEF9A9A), Color(0xFFC62828)),
    listOf(Color(0xFF90CAF9), Color(0xFF1565C0)),
    listOf(Color(0xFFA5D6A7), Color(0xFF2E7D32)),
    listOf(Color(0xFFFFCC80), Color(0xFFEF6C00)),
    listOf(Color(0xFFCE93D8), Color(0xFF6A1B9A)),
    listOf(Color(0xFF80DEEA), Color(0xFF0097A7))
)

@Composable
fun FeaturedGuideCard(guide: GuideModel) {
    val gradient = guideGradients[(guide.guide_id.hashCode() and 0x7FFFFFFF) % guideGradients.size]

    Column(
        modifier = Modifier.width(220.dp).clip(RoundedCornerShape(16.dp)).background(Color(0xFFF8F8F8)).clickable { }
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().height(130.dp).clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
            contentAlignment = Alignment.Center
        ) {
            // ✅ FIX: Load real guide image if available
            if (!guide.image_guide.isNullOrBlank()) {
                val fullUrl = if (guide.image_guide.startsWith("http")) guide.image_guide
                else "http://192.168.1.11:3000/${guide.image_guide}"
                AsyncImage(
                    model = fullUrl,
                    contentDescription = guide.guide_name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize().background(Brush.linearGradient(gradient)),
                    contentAlignment = Alignment.Center
                ) { Text(text = "🗺️", fontSize = 40.sp) }
            }

            if (!guide.provinces_name.isNullOrBlank()) {
                Box(
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                        .clip(RoundedCornerShape(8.dp)).background(Color.Black.copy(alpha = 0.45f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) { Text(text = guide.provinces_name, fontSize = 10.sp, color = Color.White) }
            }
        }

        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = guide.guide_name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121), maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (!guide.guide_detail.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = guide.guide_detail, fontSize = 12.sp, color = Color(0xFF616161), maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 17.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(28.dp).clip(CircleShape).background(Color(0xFFCFD8DC)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, null, tint = Color(0xFF607D8B), modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = guide.username ?: "ผู้ใช้", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF424242), maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun EmptyRowCard(text: String) {
    Box(
        modifier = Modifier.width(160.dp).height(140.dp).clip(RoundedCornerShape(16.dp)).background(Color(0xFFF0F0F0)),
        contentAlignment = Alignment.Center
    ) { Text(text = text, fontSize = 13.sp, color = Color(0xFF9E9E9E)) }
}