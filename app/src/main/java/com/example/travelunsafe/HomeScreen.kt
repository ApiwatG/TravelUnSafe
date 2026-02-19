package com.example.travelunsafe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ===== COLORS =====
val BackgroundWhite = Color.White
val TitleBlack = Color(0xFF000000)
val PlaceholderGray = Color(0xFFCFD8DC)
val PlaceholderDark = Color(0xFFB0BEC5)
val TextPrimary = Color(0xFF212121)
val TextSecondary = Color(0xFF757575)

// ===== DATA CLASS =====
data class Guide(
    val title: String,
    val gradientColors: List<Color> = listOf(Color(0xFF81D4FA), Color(0xFF0288D1))
)

@Composable
fun HomeScreen() {
    val guides = listOf(
        Guide("Paris guide", listOf(Color(0xFFB0BEC5), Color(0xFF78909C))),
        Guide("Hawaii guide", listOf(Color(0xFF80DEEA), Color(0xFF0097A7))),
        Guide("Tokyo guide", listOf(Color(0xFFEF9A9A), Color(0xFFC62828))),
        Guide("Bali guide", listOf(Color(0xFFA5D6A7), Color(0xFF2E7D32))),
        Guide("London guide", listOf(Color(0xFF90CAF9), Color(0xFF1565C0))),
        Guide("Seoul guide", listOf(Color(0xFFF48FB1), Color(0xFFAD1457)))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .verticalScroll(rememberScrollState())
    ) {
        HomeTopAppBar()
        HeroBanner()
        GuidesSection(guides = guides)
        Spacer(modifier = Modifier.height(16.dp))
    }
}

// ===== TOP APP BAR =====
@Composable
fun HomeTopAppBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(64.dp)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "TravelUnSave",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = TitleBlack
        )
        IconButton(onClick = { /* Handle search */ }) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = TitleBlack,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

// ===== HERO BANNER =====
@Composable
fun HeroBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
    ) {
        // Background gradient simulating a destination photo
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF1A237E),
                            Color(0xFF283593),
                            Color(0xFF1565C0),
                            Color(0xFF0277BD)
                        )
                    )
                )
        )
        // Subtle overlay pattern circles to mimic cityscape
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x88000000)
                        )
                    )
                )
        )
        // If using Coil, replace above boxes with:
        // AsyncImage(model = "your_image_url", contentDescription = null,
        //   modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)

        // Caption at bottom left
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = "🗺️  สำรวจจุดหมายใหม่",
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "ค้นพบประสบการณ์การเดินทางของคุณ",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

// ===== GUIDES SECTION =====
@Composable
fun GuidesSection(guides: List<Guide>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
    ) {
        Text(
            text = "ไกด์เด่นจากผู้ใช้",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TitleBlack,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(guides) { guide ->
                GuideCard(guide = guide)
            }
        }
    }
}

// ===== GUIDE CARD =====
@Composable
fun GuideCard(guide: Guide) {
    Column(
        modifier = Modifier
            .width(180.dp)
            .clickable { /* Handle card click */ }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.linearGradient(guide.gradientColors)
                ),
            contentAlignment = Alignment.Center
        ) {
            // Replace with AsyncImage for real photos:
            // AsyncImage(model = guide.imageUrl, contentDescription = guide.title,
            //   modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
            //   contentScale = ContentScale.Crop)
            Text(
                text = "🗺️",
                fontSize = 36.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = guide.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary,
            maxLines = 1
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
