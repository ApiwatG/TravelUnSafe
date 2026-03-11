package com.example.travelunsafe

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// ===== DETAIL PLACE MODEL (from /places/:id) =====
data class PlaceDetail(
    val place_id: String,
    val place_name: String,
    val location: String? = null,
    val view: Int = 0,
    val placedetail: String? = null,
    val image_url: String? = null,
    val category_id: String? = null,
    val provinces_id: String? = null,
    val category: String? = null,
    val province: String? = null,
    val createdAt: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceDetailScreen(
    placeId: String,
    viewModel: TravelViewModel,
    prefs: SharedPreferencesManager,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var placeDetail by remember { mutableStateOf<PlaceDetail?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isFavorited by remember { mutableStateOf(false) }

    // Load place detail
    LaunchedEffect(placeId) {
        isLoading = true
        try {
            val response = TravelClient.travelAPI.getPlaceById(placeId)
            if (response.isSuccessful) {
                placeDetail = response.body()
            }
        } catch (e: Exception) {
            android.util.Log.e("PlaceDetail", "Error: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        containerColor = Color.White
    ) { innerPadding ->

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFFFA726))
            }
        } else if (placeDetail == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ErrorOutline, null, tint = Color(0xFFE57373), modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("ไม่พบข้อมูลสถานที่", color = Color(0xFF757575))
                }
            }
        } else {
            val place = placeDetail!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                // ═══════════════════════════════════════════
                //  HERO IMAGE with overlay
                // ═══════════════════════════════════════════
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    // Background image or gradient
                    if (!place.image_url.isNullOrBlank()) {
                        val fullUrl = if (place.image_url.startsWith("http")) place.image_url
                        else "http://10.0.2.2:3000/images/${place.image_url}"
                        AsyncImage(
                            model = fullUrl,
                            contentDescription = place.place_name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // Gradient placeholder based on category
                        val gradientColors = when (place.category) {
                            "ร้านอาหาร" -> listOf(Color(0xFFFFCC80), Color(0xFFEF6C00))
                            "สถานที่ท่องเที่ยว" -> listOf(Color(0xFF81C784), Color(0xFF2E7D32))
                            "คาเฟ่" -> listOf(Color(0xFFCE93D8), Color(0xFF7B1FA2))
                            "ของหวาน" -> listOf(Color(0xFFEF9A9A), Color(0xFFE53935))
                            "บาร์" -> listOf(Color(0xFF90CAF9), Color(0xFF1565C0))
                            else -> listOf(Color(0xFF90CAF9), Color(0xFF1565C0))
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.verticalGradient(gradientColors))
                        )
                        // Category icon in center
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            val icon = when (place.category) {
                                "ร้านอาหาร" -> Icons.Default.Restaurant
                                "สถานที่ท่องเที่ยว" -> Icons.Default.Place
                                "คาเฟ่" -> Icons.Default.Coffee
                                "ของหวาน" -> Icons.Default.Cake
                                "บาร์" -> Icons.Default.LocalBar
                                else -> Icons.Default.Place
                            }
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.3f),
                                modifier = Modifier.size(120.dp)
                            )
                        }
                    }

                    // Dark gradient overlay at bottom
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                                )
                            )
                    )

                    // Back button
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(8.dp)
                            .align(Alignment.TopStart)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.3f))
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Title overlay at bottom of image
                    Text(
                        text = place.place_name,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 20.dp, bottom = 20.dp, end = 20.dp)
                    )
                }

                // ═══════════════════════════════════════════
                //  INFO CHIPS (Category + Province)
                // ═══════════════════════════════════════════
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category chip
                    if (!place.category.isNullOrBlank()) {
                        Surface(
                            color = Color(0xFFFFF3E0),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val icon = when (place.category) {
                                    "Beach" -> Icons.Default.BeachAccess
                                    "Mountain" -> Icons.Default.Terrain
                                    "Temple" -> Icons.Default.AccountBalance
                                    "Park" -> Icons.Default.Park
                                    "Museum" -> Icons.Default.Museum
                                    else -> Icons.Default.Place
                                }
                                Icon(icon, null, tint = Color(0xFFFFA726), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = place.category,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFFE65100)
                                )
                            }
                        }
                    }

                    // Province chip
                    if (!place.province.isNullOrBlank()) {
                        Surface(
                            color = Color(0xFFE3F2FD),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.LocationOn, null, tint = Color(0xFF1976D2), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = place.province,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF1565C0)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // View count
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Visibility, null, tint = Color(0xFF9E9E9E), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${place.view}",
                            fontSize = 13.sp,
                            color = Color(0xFF9E9E9E)
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)

                // ═══════════════════════════════════════════
                //  LOCATION
                // ═══════════════════════════════════════════
                if (!place.location.isNullOrBlank()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Place,
                            contentDescription = null,
                            tint = Color(0xFF4285F4),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = place.location,
                            fontSize = 15.sp,
                            color = Color(0xFF424242)
                        )
                    }
                    HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                }

                // ═══════════════════════════════════════════
                //  DESCRIPTION / DETAIL
                // ═══════════════════════════════════════════
                if (!place.placedetail.isNullOrBlank()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                    ) {
                        Text(
                            text = "รายละเอียด",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = place.placedetail,
                            fontSize = 15.sp,
                            color = Color(0xFF424242),
                            lineHeight = 24.sp
                        )
                    }
                } else {
                    // No detail placeholder
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                    ) {
                        Text(
                            text = "รายละเอียด",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "ยังไม่มีรายละเอียดของสถานที่นี้",
                            fontSize = 15.sp,
                            color = Color(0xFF9E9E9E),
                            lineHeight = 24.sp
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)

                // ═══════════════════════════════════════════
                //  ACTION BUTTONS
                // ═══════════════════════════════════════════
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Save / Favorite button
                    Button(
                        onClick = {
                            val userId = prefs.getUserId()
                            if (userId.isNotBlank()) {
                                viewModel.addFavorite(
                                    context = context,
                                    userId = userId,
                                    placeId = placeId,
                                    onSuccess = { isFavorited = true }
                                )
                            } else {
                                Toast.makeText(context, "กรุณาเข้าสู่ระบบก่อน", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFavorited) Color(0xFFE8F5E9) else Color(0xFF1A1A1A)
                        )
                    ) {
                        Icon(
                            imageVector = if (isFavorited) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            tint = if (isFavorited) Color(0xFF4CAF50) else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isFavorited) "บันทึกแล้ว" else "บันทึก",
                            fontWeight = FontWeight.Bold,
                            color = if (isFavorited) Color(0xFF4CAF50) else Color.White,
                            fontSize = 15.sp
                        )
                    }

                    // Share button

                        Spacer(modifier = Modifier.width(8.dp))

                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }

