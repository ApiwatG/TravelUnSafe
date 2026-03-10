package com.example.travelunsafe

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- 1. Search Bar ดีไซน์ใหม่แบบ Minimal ---
@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit, placeholder: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text(placeholder, fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.primary) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Default.Close, null, modifier = Modifier.size(18.dp))
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// --- 2. Admin Item Card ดีไซน์ใหม่ เน้นความสะอาด ---
@Composable
fun AdminItemCard(title: String, subtitle: String, showDelete: Boolean, onDelete: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(onClick = { onDelete(); showDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                    Text("ยืนยันการลบ")
                }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("ยกเลิก") } },
            title = { Text("ลบข้อมูล") },
            text = { Text("คุณแน่ใจหรือไม่ว่าต้องการลบ '$title'?") },
            shape = RoundedCornerShape(24.dp)
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    title.take(1).uppercase(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, maxLines = 1)
                Text(subtitle, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (showDelete) {
                IconButton(
                    onClick = { showDialog = true },
                    colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
                ) {
                    Icon(Icons.Default.DeleteSweep, null)
                }
            }
        }
    }
}

// --- 3. Stat Card แบบมี Gradient และ Glassmorphism ---
@Composable
fun AdminStatCard(modifier: Modifier, icon: ImageVector, label: String, value: String, color: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.05f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(Modifier.padding(20.dp)) {
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = 0.1f),
                modifier = Modifier.size(40.dp)
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.padding(8.dp))
            }
            Spacer(Modifier.height(16.dp))
            Text(value, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// --- 4. Dashboard ดีไซน์ใหม่ ---
@Composable
fun AdminDashboardTab(userCount: Int, tripCount: Int, hotelCount: Int, isLoading: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text("ยินดีต้อนรับ, Admin", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("นี่คือสรุปภาพรวมของระบบในวันนี้", fontSize = 14.sp, color = Color.Gray)

        Spacer(Modifier.height(24.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AdminStatCard(Modifier.weight(1f), Icons.Default.People, "ผู้ใช้งาน", userCount.toString(), Color(0xFF6366F1))
            AdminStatCard(Modifier.weight(1f), Icons.Default.Map, "ทริป", tripCount.toString(), Color(0xFF10B981))
        }
        Spacer(Modifier.height(12.dp))
        AdminStatCard(Modifier.fillMaxWidth(), Icons.Default.Hotel, "โรงแรมทั้งหมดในระบบ", hotelCount.toString(), Color(0xFFF59E0B))
    }
}

// --- 5, 6, 7. Tab List ต่างๆ (ปรับให้ใช้ SearchBar และ Card ใหม่) ---
@Composable
fun AdminUsersTab(users: List<User>, isLoading: Boolean, onDelete: (String) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredList = users.filter { it.username.contains(searchQuery, true) || it.email.contains(searchQuery, true) }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        SearchBar(searchQuery, { searchQuery = it }, "ค้นหาผู้ใช้งาน...")
        if (isLoading && users.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(strokeWidth = 3.dp) }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 20.dp)) {
                items(filteredList) { user ->
                    AdminItemCard(user.username, user.email, user.role != "admin") { onDelete(user.user_id) }
                }
            }
        }
    }
}

// AdminTripsTab และ AdminHotelsTab ให้ก๊อปปี้ Logic คล้าย AdminUsersTab ด้านบนได้เลยครับ
@Composable
fun AdminTripsTab(trips: List<Trip>, isLoading: Boolean, onDelete: (String) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredList = trips.filter { it.trip_name.contains(searchQuery, true) }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        SearchBar(searchQuery, { searchQuery = it }, "ค้นหาทริป...")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 20.dp)) {
            items(filteredList) { trip ->
                AdminItemCard(trip.trip_name, trip.province ?: "ไม่ระบุจังหวัด", true) { onDelete(trip.trip_id) }
            }
        }
    }
}

@Composable
fun AdminHotelsTab(hotels: List<Hotel>, isLoading: Boolean, onDelete: (String) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredList = hotels.filter { it.hotel_name.contains(searchQuery, true) }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        SearchBar(searchQuery, { searchQuery = it }, "ค้นหาโรงแรม...")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 20.dp)) {
            items(filteredList) { hotel ->
                AdminItemCard(hotel.hotel_name, "฿${hotel.price_per_night} / คืน", true) { onDelete(hotel.hotel_id) }
            }
        }
    }
}