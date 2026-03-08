package com.example.travelunsafe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ===== CATEGORY DATA CLASS =====
data class PlaceCategory(
    val label: String,
    val icon: ImageVector
)

// ===== GUIDE RESULT MODEL =====
data class GuideResult(
    val guide_id: String,
    val title: String,
    val author: String,
    val authorImage: String? = null,
    val likes: String = "0",
    val views: String = "0",
    val imageUrl: String? = null
)

@Composable
fun SearchScreen(
    viewModel: TravelViewModel,
    onBack: () -> Unit,
    onGuideClick: ((String) -> Unit)? = null,
    onPlaceClick: ((String) -> Unit)? = null
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    // Load data on enter
    LaunchedEffect(Unit) {
        viewModel.loadProvinces()
        viewModel.loadPlaces()
        viewModel.loadGuides()
    }

    // ===== FILTER PLACES by name, location, OR province name =====
    val filteredPlaces = viewModel.places.filter { place ->
        val matchesSearch = if (searchQuery.isBlank()) true
        else {
            place.place_name.contains(searchQuery, ignoreCase = true) ||
                    place.location?.contains(searchQuery, ignoreCase = true) == true ||
                    place.province?.contains(searchQuery, ignoreCase = true) == true
        }
        val matchesCategory = if (selectedCategory == null) true
        else place.category?.contains(selectedCategory!!, ignoreCase = true) == true
        matchesSearch && matchesCategory
    }

    // ===== FILTER GUIDES by title or author =====
    val filteredGuides = viewModel.guides.map { g ->
        GuideResult(
            guide_id    = g.guide_id,
            title       = g.guide_name,
            author      = g.username ?: "ไม่ระบุชื่อ",
            authorImage = g.image_profile,
            imageUrl    = g.image_guide
        )
    }.let { mapped ->
        if (searchQuery.isBlank()) mapped
        else mapped.filter { guide ->
            guide.title.contains(searchQuery, ignoreCase = true) ||
                    guide.author.contains(searchQuery, ignoreCase = true)
        }
    }

    val categories = listOf(
        PlaceCategory("ร้านอาหาร", Icons.Filled.Restaurant),
        PlaceCategory("สถานที่ท่องเที่ยว", Icons.Filled.Place),
        PlaceCategory("คาเฟ่", Icons.Filled.Coffee),
        PlaceCategory("ของหวาน", Icons.Filled.Cake),
        PlaceCategory("บาร์", Icons.Filled.LocalBar)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // ===== TOP BAR =====
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF212121)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Search box
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFF0F0F0)),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color(0xFF9E9E9E),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 16.sp,
                            color = Color(0xFF212121)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        decorationBox = { inner ->
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "ค้นหาจังหวัด, สถานที่, ไกด์...",
                                    fontSize = 16.sp,
                                    color = Color(0xFF9E9E9E)
                                )
                            }
                            inner()
                        }
                    )
                }
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {

            // ===== CATEGORY LABEL =====
            item {
                Text(
                    text = "สำรวจพื้นที่",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            // ===== CATEGORY ICONS =====
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat.label
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                selectedCategory = if (isSelected) null else cat.label
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) Color(0xFF42A5F5)
                                        else Color(0xFFEEEEEE)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = cat.icon,
                                    contentDescription = cat.label,
                                    tint = if (isSelected) Color.White else Color(0xFF616161),
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = cat.label,
                                fontSize = 11.sp,
                                color = Color(0xFF424242),
                                maxLines = 2,
                                modifier = Modifier.widthIn(max = 60.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ===== HOTEL BOOKING BANNER =====
            item {
                HotelBookingBanner()
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ===== LOADING =====
            if (viewModel.isLoading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF42A5F5))
                    }
                }
            } else {

                // ══════════════════════════════════════════
                //  SECTION 1: สถานที่ (Places)
                // ══════════════════════════════════════════
                item {
                    SectionTitle(
                        title = "สถานที่",
                        emoji = "📍",
                        count = filteredPlaces.size
                    )
                }

                if (filteredPlaces.isEmpty()) {
                    item {
                        EmptySearchResult(message = "ไม่พบสถานที่ที่ค้นหา")
                    }
                } else {
                    items(filteredPlaces, key = { it.place_id }) { place ->
                        PlaceResultRow(
                            place = place,
                            onClick = { onPlaceClick?.invoke(place.place_id) }  // ← ADD
                        )
                        HorizontalDivider(
                            color = Color(0xFFEEEEEE),
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

                // ══════════════════════════════════════════
                //  SECTION 2: ไกด์การเดินทาง (Guides)
                // ══════════════════════════════════════════
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    SectionTitle(
                        title = "ไกด์การเดินทาง",
                        emoji = "📖",
                        count = filteredGuides.size
                    )
                }

                if (filteredGuides.isEmpty()) {
                    item {
                        EmptySearchResult(message = "ไม่พบไกด์ที่ค้นหา")
                    }
                } else {
                    items(filteredGuides, key = { it.guide_id }) { guide ->
                        GuideResultRow(
                            guide = guide,
                            onClick = { onGuideClick?.invoke(guide.guide_id) }
                        )
                        HorizontalDivider(
                            color = Color(0xFFEEEEEE),
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

// ===== SECTION TITLE =====
@Composable
fun SectionTitle(title: String, emoji: String, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = emoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )
        }
        Text(
            text = "$count รายการ",
            fontSize = 13.sp,
            color = Color(0xFF9E9E9E)
        )
    }
}

// ===== EMPTY RESULT =====
@Composable
fun EmptySearchResult(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            fontSize = 14.sp,
            color = Color(0xFF9E9E9E)
        )
    }
}

// ===== HOTEL BOOKING BANNER =====
@Composable
fun HotelBookingBanner() {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(Color(0xFF81D4FA), Color(0xFF4FC3F7))
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "พักที่ญี่ปุ่น?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "จองโรงแรมราคาพิเศษ",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
            Icon(
                Icons.Default.Hotel,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

// ===== PLACE RESULT ROW =====
@Composable
fun PlaceResultRow(place: Place, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail
        Box(
            modifier = Modifier
                .size(width = 80.dp, height = 64.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF80CBC4), Color(0xFF26A69A))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Place,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = place.place_name,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF212121),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Province name
            if (!place.province.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFFFF9800),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = place.province,
                        fontSize = 12.sp,
                        color = Color(0xFFFF9800),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (!place.location.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = place.location,
                    fontSize = 12.sp,
                    color = Color(0xFF757575),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = null,
                    tint = Color(0xFF9E9E9E),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${place.view} views",
                    fontSize = 12.sp,
                    color = Color(0xFF9E9E9E)
                )
            }
        }
    }
}

// ===== GUIDE RESULT ROW =====
@Composable
fun GuideResultRow(guide: GuideResult, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail
        Box(
            modifier = Modifier
                .size(width = 110.dp, height = 80.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFFEF9A9A), Color(0xFFE57373))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MenuBook,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = guide.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF212121),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))

            // Author row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFCFD8DC)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color(0xFF607D8B),
                        modifier = Modifier.size(14.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = guide.author,
                    fontSize = 12.sp,
                    color = Color(0xFF757575)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Likes / Views / Share
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FavoriteBorder, null, tint = Color(0xFF9E9E9E), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(guide.likes, fontSize = 12.sp, color = Color(0xFF9E9E9E))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Visibility, null, tint = Color(0xFF9E9E9E), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(guide.views, fontSize = 12.sp, color = Color(0xFF9E9E9E))
                }
                Icon(Icons.Default.Share, null, tint = Color(0xFF9E9E9E), modifier = Modifier.size(16.dp))
            }
        }
    }
}