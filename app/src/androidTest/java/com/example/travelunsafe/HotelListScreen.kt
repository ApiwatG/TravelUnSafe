package com.example.hotel

import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// สำหรับเก็บสถานะจาก ViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun HotelListScreen(
    viewModel: HotelViewModel,
    onAddClick: () -> Unit,
    onEditClick: (Hotel) -> Unit
) {
    val hotels by viewModel.hotels.collectAsState()
    val deletedHotels by viewModel.deletedHotels.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showDeleteDialog by remember { mutableStateOf<Hotel?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getAllHotels()
        viewModel.getDeletedHotels()
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {

        // --- Top Bar ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("รายการโรงแรม", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Button(
                onClick = onAddClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                shape = RoundedCornerShape(8.dp)
            ) { Text("+ เพิ่มโรงแรม", color = Color.White) }
        }

        // --- Tab Bar ---
        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("ทั้งหมด (${hotels.size})") }
            )
            Tab(selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("ถังขยะ (${deletedHotels.size})") }
            )
        }

        // --- รายการโรงแรม ---
        when (selectedTab) {
            0 -> HotelItemList(
                hotels = hotels,
                onEdit = onEditClick,
                onDelete = { showDeleteDialog = it }
            )
            1 -> DeletedHotelList(
                hotels = deletedHotels,
                onRestore = { viewModel.restoreHotel(it.hotelId) }
            )
        }
    }

    // --- Dialog ยืนยันลบ ---
    showDeleteDialog?.let { hotel ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("ลบโรงแรม") },
            text = { Text("ต้องการลบ \"${hotel.hotelName}\" ใช่หรือไม่?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.softDeleteHotel(hotel.hotelId)
                    showDeleteDialog = null
                }) { Text("ลบ", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) { Text("ยกเลิก") }
            }
        )
    }
}

// --- รายการปกติ ---
@Composable
fun HotelItemList(
    hotels: List<Hotel>,
    onEdit: (Hotel) -> Unit,
    onDelete: (Hotel) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        items(hotels) { hotel ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(hotel.hotelName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(hotel.province, color = Color.Gray, fontSize = 13.sp)
                        Text("฿${hotel.pricePerNight}/คืน", color = Color(0xFFFF9800), fontSize = 13.sp)
                    }
                    // ปุ่มแก้ไข
                    IconButton(onClick = { onEdit(hotel) }) {
                        Icon(Icons.Default.Edit, contentDescription = "แก้ไข", tint = Color(0xFF1565C0))
                    }
                    // ปุ่มลบ
                    IconButton(onClick = { onDelete(hotel) }) {
                        Icon(Icons.Default.Delete, contentDescription = "ลบ", tint = Color.Red)
                    }
                }
            }
        }
    }
}

// --- ถังขยะ ---
@Composable
fun DeletedHotelList(
    hotels: List<Hotel>,
    onRestore: (Hotel) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        items(hotels) { hotel ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(hotel.hotelName, fontWeight = FontWeight.Bold,
                            color = Color.Gray, fontSize = 16.sp)
                        Text("ลบเมื่อ: ${hotel.deletedAt}", color = Color.Gray, fontSize = 12.sp)
                    }
                    // ปุ่มกู้คืน
                    Button(
                        onClick = { onRestore(hotel) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("กู้คืน", color = Color.White) }
                }
            }
        }
    }
}