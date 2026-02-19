package com.example.travelunsafe.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
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
        containerColor = Color.White
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Hero Image (placeholder gray)
            item {
                HeroImagePlaceholder()
            }

            // Author Row
            item {
                AuthorRow(
                    author = uiState.author,
                    onFollowClick = onFollowClick
                )
            }

            // Day sections
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

            item { Spacer(modifier = Modifier.height(24.dp)) }
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
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "ย้อนกลับ",
                    tint = Color(0xFF1A1A1A)
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White
        )
    )
}

// ── Hero Image Placeholder ────────────────────────────────────────────────────

@Composable
private fun HeroImagePlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color(0xFFBDBDBD))
    )
}

// ── Author Row ────────────────────────────────────────────────────────────────

@Composable
private fun AuthorRow(
    author: GuideAuthor,
    onFollowClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar placeholder
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFBDBDBD))
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = author.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1A1A1A),
            modifier = Modifier.weight(1f)
        )

        Button(
            onClick = onFollowClick,
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFA726)
            ),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(
                text = "ติดตาม",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

// ── Day Section Header ────────────────────────────────────────────────────────

@Composable
private fun DaySectionHeader(dayNumber: Int) {
    Text(
        text = "วันที่ $dayNumber",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1A1A1A),
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp)
    )
}

// ── Place Card ────────────────────────────────────────────────────────────────

@Composable
private fun PlaceCard(place: PlaceItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Left content
        Column(modifier = Modifier.weight(1f)) {
            // Place name with icon
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = Color(0xFF1565C0),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = place.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Tags
            if (place.tags.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    place.tags.forEach { tag ->
                        TagChip(label = tag)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Description
            Text(
                text = place.description,
                fontSize = 13.sp,
                color = Color(0xFF616161),
                lineHeight = 18.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "ดูรายละเอียด",
                fontSize = 12.sp,
                color = Color(0xFF9E9E9E)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Right: Image + Bookmark
        Box(contentAlignment = Alignment.TopEnd) {
            // Image placeholder
            Box(
                modifier = Modifier
                    .size(width = 90.dp, height = 70.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFBDBDBD))
            )
            // Bookmark icon overlay
            Icon(
                imageVector = Icons.Default.BookmarkBorder,
                contentDescription = "บันทึก",
                tint = Color.White,
                modifier = Modifier
                    .padding(4.dp)
                    .size(18.dp)
            )
        }
    }
}

// ── Tag Chip ──────────────────────────────────────────────────────────────────

@Composable
private fun TagChip(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF5F5F5))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color(0xFF424242)
        )
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GuideDetailScreenPreview() {
    val sampleState = GuideDetailUiState(
        title = "วิธีการเที่ยวญี่ปุ่นใน 2 สัปดาห์",
        author = GuideAuthor(name = "White Snake"),
        days = listOf(
            DaySection(
                dayNumber = 1,
                places = listOf(
                    PlaceItem(
                        name = "Tokyo",
                        tags = listOf("เมือง", "นิทรรศการ"),
                        description = "โตเกียว เมืองหลวงที่พลุกพล่านของญี่ปุ่น ผสมผสานความทันสมัยสุดขีดและความดั้งเดิมแต่ก่อง ท้องฟ้าสว่างไสวด้วยแสงนีออน..."
                    ),
                    PlaceItem(
                        name = "Shibuya",
                        tags = listOf(),
                        description = "Shibuya is a special ward in Tokyo, Japan."
                    )
                )
            )
        )
    )

    GuideDetailScreen(
        uiState = sampleState,
        onBackClick = {},
        onFollowClick = {}
    )
}