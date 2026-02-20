package com.example.travelunsafe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ===== CATEGORY DATA CLASS =====
data class PlaceCategory(
    val label: String,
    val icon: ImageVector
)

// ===== SEARCH RESULT SEALED CLASS =====
// Used to interleave places and guides in one list
sealed class SearchResultItem {
    data class PlaceItem(val place: Place) : SearchResultItem()
    data class GuideItem(val guide: GuideResult) : SearchResultItem()
}

// Guide result model (from FavoritePlace or a guides endpoint)
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
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    // Load provinces + places on enter
    LaunchedEffect(Unit) {
        viewModel.loadProvinces()
        viewModel.loadPlaces()
    }

    // Filter places by province name matching search query
    val filteredPlaces = remember(searchQuery, viewModel.places) {
        if (searchQuery.isBlank()) viewModel.places
        else viewModel.places.filter { place ->
            place.place_name.contains(searchQuery, ignoreCase = true) ||
            place.location?.contains(searchQuery, ignoreCase = true) == true
        }
    }

    // Dummy guides — replace with real API when guide endpoint is ready
    val dummyGuides = listOf(
        GuideResult("g1", "Japan: วัดเซนโซจิ", "master of gym", likes = "2k", views = "100k"),
        GuideResult("g2", "Japan: ย่านชิบูย่า", "White Snake", likes = "7.8k", views = "600k"),
        GuideResult("g3", "Japan: ฟูจิซัง", "TravelKing", likes = "5k", views = "300k"),
        GuideResult("g4", "Japan: โอซาก้า", "JapanLover", likes = "3.2k", views = "200k"),
        GuideResult("g5", "Japan: โตเกียว", "WandererTH", likes = "9k", views = "1M"),
        GuideResult("g6", "Japan: นาโกย่า", "ExploreAsia", likes = "1.5k", views = "80k"),
    )

    // Interleave: 3 places → 3 guides → 3 places → ...
    val interleavedResults = remember(filteredPlaces, dummyGuides) {
        buildInterleavedList(filteredPlaces, dummyGuides)
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
            // Back button
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
                                    text = "ค้นหาจังหวัด, สถานที่...",
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

            // ===== INTERLEAVED RESULTS =====
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
                itemsIndexed(interleavedResults) { _, item ->
                    when (item) {
                        is SearchResultItem.PlaceItem -> PlaceResultRow(place = item.place)
                        is SearchResultItem.GuideItem -> GuideResultRow(guide = item.guide)
                    }
                    HorizontalDivider(
                        color = Color(0xFFEEEEEE),
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

// ===== INTERLEAVE BUILDER =====
// Pattern: 3 places → 3 guides → 3 places → 3 guides...
fun buildInterleavedList(
    places: List<Place>,
    guides: List<GuideResult>
): List<SearchResultItem> {
    val result = mutableListOf<SearchResultItem>()
    var pIdx = 0
    var gIdx = 0
    val chunkSize = 3

    while (pIdx < places.size || gIdx < guides.size) {
        // Add up to 3 places
        repeat(chunkSize) {
            if (pIdx < places.size) {
                result.add(SearchResultItem.PlaceItem(places[pIdx++]))
            }
        }
        // Add up to 3 guides
        repeat(chunkSize) {
            if (gIdx < guides.size) {
                result.add(SearchResultItem.GuideItem(guides[gIdx++]))
            }
        }
    }
    return result
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
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { /* TODO: navigate to hotel booking */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF9800)
                    ),
                    shape = RoundedCornerShape(24.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "จองโรงแรม",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            // Sleeping person emoji placeholder
            Text(text = "😴", fontSize = 52.sp)
        }
    }
}

// ===== PLACE RESULT ROW =====
@Composable
fun PlaceResultRow(place: Place) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: navigate to place detail */ }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail placeholder
        Box(
            modifier = Modifier
                .size(width = 110.dp, height = 80.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF80CBC4), Color(0xFF4DB6AC))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Replace with AsyncImage when you have real image URLs
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
            if (!place.location.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = place.location,
                    fontSize = 12.sp,
                    color = Color(0xFF757575),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
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
fun GuideResultRow(guide: GuideResult) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: navigate to guide detail */ }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail placeholder
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
