package com.example.travelunsafe.ui.theme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

// ── Model สำหรับข้อมูลจากฐานข้อมูล ──────────────────────────────────
data class GuideInfo(
    val title: String = "กำลังโหลด...",
    val username: String = "...",
    val imageUrl: String = "",
    val description: String = ""
)

class GuideDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val guideId = intent.getStringExtra("guide_id") ?: "GI001"

        setContent {
            MaterialTheme {
                GuideDetailScreen(
                    guideId = guideId,
                    onBackClick = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideDetailScreen(guideId: String, onBackClick: () -> Unit) {
    var guideData by remember { mutableStateOf(GuideInfo()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(guideId) {
        val fetchedData = withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("http://10.0.2.2:3001/api/guides/$guideId")
                    .build()
                val response = client.newCall(request).execute()
                val json = JSONObject(response.body?.string() ?: "{}")
                
                GuideInfo(
                    title = json.optString("title", "ไม่พบหัวข้อ"),
                    username = json.optString("username", "ไม่ทราบชื่อ"),
                    imageUrl = json.optString("image_url", ""),
                    description = json.optString("description", "")
                )
            } catch (e: Exception) {
                null
            }
        }
        if (fetchedData != null) guideData = fetchedData
        isLoading = false
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = guideData.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back", modifier = Modifier.size(20.dp))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFFFA726))
            }
        } else {
            LazyColumn(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                // 1. Hero Image พร้อมระบบเช็ค Error
                item {
                    val fullImageUrl = if (guideData.imageUrl.startsWith("http")) guideData.imageUrl 
                                     else "http://10.0.2.2:3001/images/guides/${guideData.imageUrl}"
                    
                    SubcomposeAsyncImage(
                        model = fullImageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop,
                        loading = {
                            Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5)), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(30.dp), strokeWidth = 2.dp)
                            }
                        },
                        error = {
                            Box(modifier = Modifier.fillMaxSize().background(Color(0xFFEEEEEE)), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.BrokenImage, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
                                    Text("โหลดรูปไม่สำเร็จ", color = Color.LightGray, fontSize = 12.sp)
                                }
                            }
                        }
                    )
                }

                item { AuthorSection(name = guideData.username) }

                item {
                    Text(
                        "แผนการเดินทาง",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                    )
                }

                item {
                    PlaceItemRow(
                        PlaceDetail(
                            name = guideData.title,
                            tags = listOf("แนะนำ"),
                            description = guideData.description,
                            imageUrl = if (guideData.imageUrl.startsWith("http")) guideData.imageUrl 
                                      else "http://10.0.2.2:3001/images/guides/${guideData.imageUrl}"
                        )
                    )
                }
                
                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
fun AuthorSection(name: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.Gray))
        Spacer(modifier = Modifier.width(12.dp))
        Text(name, fontSize = 16.sp, color = Color.DarkGray, modifier = Modifier.weight(1f))
        Button(
            onClick = { },
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA726)),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Text("ติดตาม", fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun PlaceItemRow(place: PlaceDetail) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Place, null, tint = Color(0xFF4285F4), modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(place.name, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                IconButton(onClick = { }) {
                    Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0xFF1A1A1A)).padding(6.dp)) {
                        Icon(Icons.Default.BookmarkBorder, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                place.tags.forEach { TagChip(it) }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(place.description, fontSize = 14.sp, color = Color.Gray, lineHeight = 20.sp, maxLines = 10, overflow = TextOverflow.Ellipsis)
        }
        Spacer(modifier = Modifier.width(16.dp))
        
        // รูปสถานที่พร้อมระบบเช็ค Error
        SubcomposeAsyncImage(
            model = place.imageUrl,
            contentDescription = null,
            modifier = Modifier.size(width = 100.dp, height = 80.dp).clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop,
            error = {
                Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Image, contentDescription = null, tint = Color.LightGray)
                }
            }
        )
    }
}

@Composable
fun TagChip(text: String) {
    Surface(color = Color(0xFFF0F0F0), shape = RoundedCornerShape(8.dp)) {
        Text(text, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), fontSize = 12.sp, color = Color.DarkGray)
    }
}

data class PlaceDetail(val name: String, val tags: List<String>, val description: String, val imageUrl: String)
