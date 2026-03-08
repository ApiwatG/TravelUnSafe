package com.example.travelunsafe

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage // อย่าลืม Import Coil
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.CircularProgressIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelDetailScreen(
    hotel: Hotel,
    tripId: String? = null,        // ✅ เพิ่ม — รับ tripId จาก NavGraph
    userId: String = "U0001",// <--- 1. รับข้อมูล Hotel ที่ถูกคลิกส่งเข้ามา
    viewModel: TravelViewModel,
    onBackClick: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var checkInInput by remember { mutableStateOf("") }   // ✅ เพิ่ม
    var checkOutInput by remember { mutableStateOf("") }  // ✅ เพิ่ม
    var isBooking by remember { mutableStateOf(false) }   // ✅ เพิ่ม
    val context = LocalContext.current

    // ตั้งค่า URL ของรูปภาพให้ตรงกับ Server ของเรา
    val baseUrl = "http://192.168.1.11:3000/images/"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "รายละเอียดโรงแรมและที่พัก",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                textAlign = TextAlign.Center
            )

            // 2. เปลี่ยนกล่องสีเทา เป็นรูปภาพจริงจาก Database
            AsyncImage(
                model = if (!hotel.image_url.isNullOrEmpty()) "$baseUrl${hotel.image_url}" else "",
                contentDescription = hotel.hotel_name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop, // ตัดภาพให้เต็มสัดส่วน
                error = androidx.compose.ui.graphics.painter.ColorPainter(Color.LightGray) // ถ้าโหลดรูปไม่ขึ้นให้เป็นสีเทา
            )

            Column(modifier = Modifier.padding(16.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 3. ชื่อโรงแรมจาก Database
                    Text(
                        text = hotel.hotel_name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = { showDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFAB40)),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp)
                    ) {
                        Text("เพิ่ม", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 4. ราคาต่อคืน
                Text(
                    text = "฿${hotel.price_per_night} / คืน",
                    fontSize = 18.sp,
                    color = Color(0xFF00B0FF),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 5. สถานที่ตั้ง (เอา Address ต่อกับ Province)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${hotel.address ?: ""} ${hotel.province_name ?: ""}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 6. เบอร์โทรติดต่อ (ตรวจสอบก่อนว่ามีเบอร์ไหม)
                if (!hotel.contact_phone.isNullOrEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Phone, // ไอคอนโทรศัพท์
                            contentDescription = null,
                            tint = Color(0xFF4CAF50), // สีเขียว
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = hotel.contact_phone,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "รายละเอียด",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 👇 7. เปลี่ยนมาใช้ข้อมูลจริงจาก Database
                Text(
                    text = hotel.hoteldetail ?: "ไม่มีข้อมูลรายละเอียดสำหรับโรงแรมนี้", // ถ้าค่าเป็น null ให้โชว์ข้อความเผื่อไว้
                    fontSize = 14.sp,
                    color = Color.Black,
                    lineHeight = 22.sp
                )
            }
        }

        // ส่วนของ Dialog ยืนยันการเพิ่ม (ใช้แบบเดิมได้เลย)
        if (showDialog) {
            Dialog(onDismissRequest = { showDialog = false }) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.padding(16.dp).fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "ยืนยันการเพิ่มหรือไม่?",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 32.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = {
                                    showDialog = false
                                    Toast.makeText(context, "เพิ่มสำเร็จเรียบร้อย!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFAB40)),
                                modifier = Modifier.width(90.dp)
                            ) {
                                Text("ยืนยัน", color = Color.White)
                            }

                            Button(
                                onClick = { showDialog = false },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9E9E9E)),
                                modifier = Modifier.width(90.dp)
                            ) {
                                Text("ยกเลิก", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}