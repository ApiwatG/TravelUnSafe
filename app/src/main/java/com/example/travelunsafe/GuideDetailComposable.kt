package com.example.travelunsafe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// ─────────────────────────────────────────────────────────────
//  Constants
// ─────────────────────────────────────────────────────────────
private val Orange     = Color(0xFFFFA726)
private val OrangeDeep = Color(0xFFF57C00)
private val TextPrim   = Color(0xFF212121)
private val TextSec    = Color(0xFF757575)
private val BgCard     = Color(0xFFFAFAFA)

// ─────────────────────────────────────────────────────────────
//  Root composable
// ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideDetailComposable(
    uiState: GuideDetailUiState,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = uiState.title.ifBlank { "คู่มือท่องเที่ยว" },
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { innerPadding ->

        when {
            uiState.isLoading -> LoadingView(innerPadding)
            uiState.error != null -> ErrorView(uiState.error, innerPadding)
            else -> GuideContent(uiState, innerPadding)
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Loading
// ─────────────────────────────────────────────────────────────
@Composable
private fun LoadingView(padding: PaddingValues) {
    Box(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Orange)
    }
}

// ─────────────────────────────────────────────────────────────
//  Error
// ─────────────────────────────────────────────────────────────
@Composable
private fun ErrorView(message: String, padding: PaddingValues) {
    Box(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = Color(0xFFE57373),
                modifier = Modifier.size(52.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text(message, color = TextSec, fontSize = 14.sp)
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Main content
// ─────────────────────────────────────────────────────────────
@Composable
private fun GuideContent(
    uiState: GuideDetailUiState,
    padding: PaddingValues
) {
    LazyColumn(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // ── Hero image / banner ───────────────────────────
        item { GuideBannerSection(uiState) }

        // ── Author card ───────────────────────────────────
        item { GuideAuthorCard(uiState.author) }

        // ── Description card ──────────────────────────────
        if (!uiState.description.isNullOrBlank()) {
            item { GuideDescriptionCard(uiState.description) }
        }

        // ── Itinerary days (if any) ───────────────────────
        if (uiState.days.isNotEmpty()) {
            item { GuideSectionTitle("แผนการเดินทาง", Icons.Default.Map) }
            uiState.days.forEach { day ->
                item { GuideDayCard(day) }
            }
        }

        item { Spacer(Modifier.height(32.dp)) }
    }
}

// ─────────────────────────────────────────────────────────────
//  Hero banner
// ─────────────────────────────────────────────────────────────
@Composable
private fun GuideBannerSection(uiState: GuideDetailUiState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
    ) {
        if (!uiState.imageUrl.isNullOrBlank()) {
            // Try to build full URL if relative path
            val imageUrl = if (uiState.imageUrl.startsWith("http")) {
                uiState.imageUrl
            } else {
                "http://10.0.2.2:3000/${uiState.imageUrl}"
            }
            AsyncImage(
                model = imageUrl,
                contentDescription = uiState.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Placeholder gradient when no image
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Orange, OrangeDeep)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.MenuBook,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.4f),
                    modifier = Modifier.size(80.dp)
                )
            }
        }

        // Gradient overlay at bottom for title legibility
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f)),
                        startY = 120f
                    )
                )
        )

        // Title + date overlay
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = uiState.title,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (!uiState.createdAt.isNullOrBlank()) {
                val shortDate = uiState.createdAt.take(10)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = shortDate,
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Author card
// ─────────────────────────────────────────────────────────────
@Composable
private fun GuideAuthorCard(author: GuideAuthor) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            if (!author.image.isNullOrBlank()) {
                val imgUrl = if (author.image.startsWith("http")) author.image
                else "http://10.0.2.2:3000/${author.image}"
                AsyncImage(
                    model = imgUrl,
                    contentDescription = author.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFCFD8DC)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = Color(0xFF607D8B),
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("ผู้เขียน", fontSize = 11.sp, color = TextSec)
                Text(
                    text = author.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrim
                )
            }

            // Follow button
            Button(
                onClick = { /* TODO: follow */ },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Orange),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Icon(
                    Icons.Default.PersonAdd,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp),
                    tint = Color.White
                )
                Spacer(Modifier.width(4.dp))
                Text("ติดตาม", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Description card
// ─────────────────────────────────────────────────────────────
@Composable
private fun GuideDescriptionCard(description: String) {
    var expanded by remember { mutableStateOf(false) }
    val needsToggle = true

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Orange.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = Orange,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "รายละเอียด",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrim
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color(0xFF424242),
                lineHeight = 22.sp,
                maxLines = if (expanded) Int.MAX_VALUE else 3,
                overflow = if (expanded) TextOverflow.Visible else TextOverflow.Ellipsis
            )
            if (needsToggle) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = if (expanded) "ย่อข้อความ ▲" else "ดูเพิ่มเติม ▼",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Orange,
                    modifier = Modifier.clickable { expanded = !expanded }
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Section title row (for Itinerary heading)
// ─────────────────────────────────────────────────────────────
@Composable
private fun GuideSectionTitle(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Orange.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Orange, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(10.dp))
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrim)
    }
}

// ─────────────────────────────────────────────────────────────
//  Day card (itinerary)
// ─────────────────────────────────────────────────────────────
@Composable
private fun GuideDayCard(day: DaySection) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Day header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Orange),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${day.dayNumber}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        text = "วันที่ ${day.dayNumber}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrim
                    )
                    if (!day.date.isNullOrBlank()) {
                        Text(day.date, fontSize = 12.sp, color = TextSec)
                    }
                }
            }

            if (day.places.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFF0F0F0))
                Spacer(Modifier.height(8.dp))
                day.places.forEachIndexed { index, place ->
                    GuidePlaceRow(place = place, isLast = index == day.places.lastIndex)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Place row inside day card
// ─────────────────────────────────────────────────────────────
@Composable
fun GuidePlaceRow(place: PlaceItem, isLast: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Timeline
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4285F4))
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(36.dp)
                        .background(Color(0xFFE0E0E0))
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = place.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrim
            )
            if (!place.location.isNullOrBlank()) {
                Text(place.location, fontSize = 12.sp, color = TextSec)
            }
            val timeText = buildString {
                if (!place.startTime.isNullOrBlank()) append(place.startTime)
                if (!place.endTime.isNullOrBlank()) append(" – ${place.endTime}")
            }
            if (timeText.isNotBlank()) {
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        tint = TextSec,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(Modifier.width(3.dp))
                    Text(timeText, fontSize = 12.sp, color = TextSec)
                }
            }
            if (!place.note.isNullOrBlank()) {
                Spacer(Modifier.height(3.dp))
                Text(
                    text = place.note,
                    fontSize = 13.sp,
                    color = Color(0xFF616161),
                    lineHeight = 18.sp
                )
            }
        }
    }
}