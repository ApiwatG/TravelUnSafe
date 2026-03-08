package com.example.travelunsafe

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun FilterButtonsRow(
    onFilterClick: () -> Unit // รับฟังก์ชันคลิกปุ่มคัดกรอง
) {
    val buttonColor = Color(0xFF00B0FF)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
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

        // ปุ่มขวา (เชื่อมต่อ onFilterClick)
        OutlinedButton(
            onClick = onFilterClick,
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
            .height(120.dp)
    ) {
        // รูปภาพจำลอง
        Box(
            modifier = Modifier
                .width(140.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // ข้อมูลโรงแรม
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "ชื่อโรงแรมตัวอย่าง", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Blue, modifier = Modifier.size(14.dp))
                    Text(text = "สถานที่ตั้ง", fontSize = 12.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "⭐⭐⭐", fontSize = 12.sp)
            }
            Text(text = "฿1,200/คืน", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
fun HotelCard(
    hotel: Hotel, // รับข้อมูล Hotel เข้ามา
    onClick: () -> Unit
) {
    // URL พื้นฐานของรูปภาพ (เปลี่ยน IP เป็นเครื่อง Server ของคุณ)
    // ถ้าใช้ Emulator Android ให้ใช้ 10.0.2.2 แทน localhost
    val baseUrl = "http://192.168.1.11:3001/images/"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() }, // ย้าย Clickable มาไว้ที่ Card
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row {
            // ส่วนรูปภาพ
            AsyncImage(
                model = if (hotel.image_url != null) "$baseUrl${hotel.image_url}" else "", // ต่อ URL กับชื่อไฟล์
                contentDescription = hotel.hotel_name,
                modifier = Modifier
                    .width(140.dp)
                    .fillMaxHeight(),
                contentScale = ContentScale.Crop, // ตัดภาพให้เต็มกรอบ
                // ถ้าโหลดรูปไม่ติด ให้โชว์สีเทาแทน
                error = androidx.compose.ui.graphics.painter.ColorPainter(Color.LightGray)
            )

            // ส่วนข้อมูล
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = hotel.hotel_name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1 // ถ้าชื่อยาวเกินให้ตัดบรรทัด
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Blue, modifier = Modifier.size(14.dp))
                        Text(
                            text = hotel.province_name ?: "ไม่ระบุ", // ดึงชื่อจังหวัดมาโชว์
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "⭐⭐⭐", fontSize = 12.sp)
                }
                Text(
                    text = "฿${hotel.price_per_night}/คืน", // ใส่ราคาจริง
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF00B0FF),
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}