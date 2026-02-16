package com.example.travelunsafe

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelBookingScreen() {
    Scaffold(
        // 1. ส่วนหัว (Top Bar)
        topBar = {
            TopAppBar(
                title = { /* ไม่ใส่ข้อความตรงนี้แล้ว */ },
                navigationIcon = {
                    IconButton(onClick = { /* ใส่คำสั่งย้อนกลับตรงนี้ */ }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack, // ไอคอนลูกศร
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                }
            )
        },
    ) { paddingValues ->

        // 3. เนื้อหาหลัก (Content)
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()) // ทำให้เลื่อนหน้าจอได้
        ) {
            Text(
                text = "โรงแรมและที่พัก",
                fontSize = 32.sp, // ปรับให้ใหญ่ขึ้น
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 24.dp) // เว้นระยะห่างด้านล่าง
            )
            // 3.1 ปุ่มตัวเลือก (Filter Buttons)
            FilterButtonsRow()

            Spacer(modifier = Modifier.height(24.dp))

            // 3.2 รายการโรงแรม (จำลองขึ้นมา 3 อัน)
            repeat(3) {
                HotelCardSkeleton()
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color.LightGray.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// --- ส่วนประกอบย่อย (Components) ---

@Composable
fun FilterButtonsRow() {
    val buttonColor = Color(0xFF00B0FF)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp) // เว้นระยะห่างระหว่างปุ่ม
    ) {
        // ปุ่มซ้าย
        OutlinedButton(
            onClick = {},
            border = BorderStroke(width = 1.dp, color = buttonColor),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f).height(50.dp)
        ) {
            Text("ที่ไหน: โอซาก้า", color = Color.Gray)
        }

        // ปุ่มขวา
        OutlinedButton(
            onClick = {},
            border = BorderStroke(width = 1.dp, color = buttonColor),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f).height(50.dp)
        ) {
            Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("คัดกรอง", color = Color.Gray)
        }
    }
}

@Composable
fun HotelCardSkeleton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp) // กำหนดความสูงของการ์ด
    ) {
        // กล่องสี่เหลี่ยมแทนรูปภาพ (Placeholder)
        Box(
            modifier = Modifier
                .width(140.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray) // สีเทา
        )

        Spacer(modifier = Modifier.width(16.dp))

        // รายละเอียดด้านขวา
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween // ดันเนื้อหาไปบนสุดกับล่างสุด
        ) {
            Column {
                Text(
                    text = "ชื่อโรงแรมตัวอย่าง",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Blue, modifier = Modifier.size(14.dp))
                    Text(text = "สถานที่ตั้ง", fontSize = 12.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "⭐⭐⭐", fontSize = 12.sp)
            }

            Text(
                text = "฿ 1,200/คืน",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}