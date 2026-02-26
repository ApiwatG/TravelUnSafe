package com.example.travelunsafe.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Data Models ──────────────────────────────────────────────────────────────

data class GuideAuthor(
    val name: String,
    val avatarUrl: String? = null
)

data class PlaceItem(
    val name: String,
    val tags: List<String>,
    val description: String,
    val imageUrl: String? = null
)

data class DaySection(
    val dayNumber: Int,
    val places: List<PlaceItem>
)

data class GuideDetailUiState(
    val title: String,
    val author: GuideAuthor,
    val days: List<DaySection>
)

// ── Screen ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideDetailScreen(
    uiState: GuideDetailUiState,
    onBackClick: () -> Unit,
    onFollowClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            GuideTopBar(title = uiState.title, onBackClick = onBackClick)
        },
        // นำ Bottom Bar ออกแล้ว
        containerColor = Color.White
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            item {
                HeroImagePlaceholder()
            }

            item {
                AuthorRow(
                    author = uiState.author,
                    onFollowClick = onFollowClick
                )
            }

            uiState.days.forEach { day ->
                item {
                    DaySectionHeader(dayNumber = day.dayNumber)
                }
                items(day.places) { place ->
                    PlaceCard(place = place)
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color(0xFFEEEEEE)
                    )
                }
            }
        }
    }
}

// ── Top Bar ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GuideTopBar(
    title: String,
    onBackClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
    )
}

// ── UI Components ─────────────────────────────────────────────────────────────

@Composable
private fun HeroImagePlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .background(Color.LightGray)
    ) {
        // ในอนาคตใช้ AsyncImage จาก Coil ดึงรูปจริง
        Text("Hero Image", modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
private fun AuthorRow(author: GuideAuthor, onFollowClick: () -> Unit) {
    Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.Gray))
        Spacer(modifier = Modifier.width(12.dp))
        Text(author.name, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
        Button(
            onClick = onFollowClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBC02D)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("ติดตาม", color = Color.White)
        }
    }
}

@Composable
private fun DaySectionHeader(dayNumber: Int) {
    Text(
        "วันที่ $dayNumber",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
private fun PlaceCard(place: PlaceItem) {
    Row(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Place, contentDescription = null, tint = Color.Blue, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(place.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = {}, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.BookmarkBorder, contentDescription = null)
                }
            }
            Row(modifier = Modifier.padding(vertical = 4.dp)) {
                place.tags.forEach { tag ->
                    Surface(
                        color = Color(0xFFF5F5F5),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Text(tag, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                    }
                }
            }
            Text(place.description, fontSize = 13.sp, color = Color.Gray, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text("ดูรายละเอียด", fontSize = 11.sp, color = Color.LightGray, modifier = Modifier.padding(top = 4.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Box(modifier = Modifier.size(width = 100.dp, height = 70.dp).clip(RoundedCornerShape(8.dp)).background(Color.LightGray))
    }
}
