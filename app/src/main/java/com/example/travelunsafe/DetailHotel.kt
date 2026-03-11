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
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelDetailScreen(
    hotel: Hotel,
    tripId: String? = null,
    userId: String = "",
    viewModel: TravelViewModel,
    showAddButton: Boolean = true,
    onBackClick: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val baseUrl = "http://10.0.2.2:3000/images/" // แก้ IP ให้ตรงกับเครื่องคุณถ้าใช้เครื่องจริงเทสต์

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

            AsyncImage(
                model = if (!hotel.image_url.isNullOrEmpty()) "$baseUrl${hotel.image_url}" else "",
                contentDescription = hotel.hotel_name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop,
                error = androidx.compose.ui.graphics.painter.ColorPainter(Color.LightGray)
            )

            Column(modifier = Modifier.padding(16.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = hotel.hotel_name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    if (showAddButton) {
                        Button(
                            onClick = { showDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFAB40)),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp)
                        ) {
                            Text("เพิ่ม", color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "฿${hotel.price_per_night.toInt()} / คืน",
                    fontSize = 18.sp,
                    color = Color(0xFF00B0FF),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = Color(0xFF2196F3), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${hotel.address ?: ""} ${hotel.province_name ?: ""}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (!hotel.contact_phone.isNullOrEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Phone, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = hotel.contact_phone, fontSize = 14.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(text = "รายละเอียด", fontSize = 16.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = hotel.hoteldetail ?: "ไม่มีข้อมูลรายละเอียดสำหรับโรงแรมนี้",
                    fontSize = 14.sp,
                    color = Color.Black,
                    lineHeight = 22.sp
                )
            }
        }

        // ===== DIALOG ยืนยันการจอง =====
        if (showDialog) {
            var checkInInput by remember { mutableStateOf("") }
            var checkOutInput by remember { mutableStateOf("") }

            // คำนวณจำนวนคืนและราคารวม
            val nights = remember(checkInInput, checkOutInput) {
                try {
                    val fmt = java.time.LocalDate.parse(checkInInput)
                    val fmtOut = java.time.LocalDate.parse(checkOutInput)
                    val n = java.time.temporal.ChronoUnit.DAYS.between(fmt, fmtOut).toInt()
                    if (n > 0) n else 0
                } catch (e: Exception) { 0 }
            }
            val totalPrice = nights * hotel.price_per_night.toInt()

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
                        Text("ยืนยันการเพิ่มหรือไม่?", fontSize = 20.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(hotel.hotel_name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("฿${hotel.price_per_night.toInt()} / คืน", fontSize = 14.sp, color = Color(0xFF00B0FF))

                        Spacer(modifier = Modifier.height(16.dp))

                        // ช่องกรอกวันเช็คอิน
                        OutlinedTextField(
                            value = checkInInput,
                            onValueChange = { checkInInput = it },
                            label = { Text("เช็คอิน (YYYY-MM-DD)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // ช่องกรอกวันเช็คเอาท์
                        OutlinedTextField(
                            value = checkOutInput,
                            onValueChange = { checkOutInput = it },
                            label = { Text("เช็คเอาท์ (YYYY-MM-DD)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // แสดงราคารวม
                        if (nights > 0) {
                            Text(
                                text = "$nights คืน × ฿${hotel.price_per_night.toInt()} = ฿$totalPrice",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF7B05B)
                            )
                        } else if (checkInInput.isNotBlank() && checkOutInput.isNotBlank()) {
                            Text("วันที่ไม่ถูกต้อง", color = Color.Red, fontSize = 13.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = {
                                    if (nights <= 0) {
                                        Toast.makeText(context, "กรุณากรอกวันที่ให้ถูกต้อง", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    if (tripId != null && tripId.isNotBlank()) {
                                        showDialog = false
                                        viewModel.createBooking(
                                            context = context,
                                            hotelId = hotel.hotel_id,
                                            userId = userId,
                                            tripId = tripId,
                                            checkIn = checkInInput,
                                            checkOut = checkOutInput,
                                            totalPrice = totalPrice,  // ✅ ราคารวมจากจำนวนคืนจริง
                                            onSuccess = {
                                                Toast.makeText(context, "เพิ่มโรงแรมลงทริปสำเร็จ!", Toast.LENGTH_SHORT).show()
                                                onBackClick()
                                            }
                                        )
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFAB40)),
                                modifier = Modifier.width(90.dp)
                            ) { Text("ยืนยัน", color = Color.White) }

                            Button(
                                onClick = { showDialog = false },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9E9E9E)),
                                modifier = Modifier.width(90.dp)
                            ) { Text("ยกเลิก", color = Color.White) }
                        }
                    }
                }
            }
        }
    }
}