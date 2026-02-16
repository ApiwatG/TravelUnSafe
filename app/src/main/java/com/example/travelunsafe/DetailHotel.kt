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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelDetailScreen() {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        // 1. TopBar: มีปุ่มย้อนกลับเหมือนหน้าแรก
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { /* สั่งให้ย้อนกลับหน้าเดิม */ }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->

        // 3. เนื้อหาหลัก (Scrollable Column)
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // สำคัญ: ทำให้เลื่อนอ่านเนื้อหาได้
        ) {

            // 3.1 หัวข้อหน้า (อยู่กลาง)
            Text(
                text = "รายละเอียดโรงแรมและที่พัก",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                textAlign = TextAlign.Center
            )

            // 3.2 รูปภาพขนาดใหญ่ (Banner)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp) // ความสูงของรูป
                    .background(Color.LightGray) // สีเทาแทนรูปจริง
            )

            // 3.3 ส่วนข้อมูล (Info Section)
            Column(modifier = Modifier.padding(16.dp)) {

                // แถวชื่อโรงแรม + ปุ่มจอง
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween, // ดันให้ห่างกันสุดขอบ
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // ชื่อโรงแรม
                    Text(
                        text = "Hotel Yururito Osaka",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f) // ให้กินพื้นที่ส่วนใหญ่
                    )

                    // ปุ่มจอง (สีส้ม)
                    Button(
                        onClick = { showDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFAB40)), // สีส้ม
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp)
                    ) {
                        Text("เพิ่ม", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // สถานที่ตั้ง
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF2196F3), // สีฟ้า
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "โอซาก้า, Nishihommachi, Nishi-ku",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // หัวข้อรายละเอียด
                Text(
                    text = "รายละเอียด",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                // เนื้อหาคำบรรยาย (Text Body)
                Text(
                    text = "โรงแรมระดับ ⭐⭐⭐⭐⭐ ดาว ในเมืองโอซาก้า (Nishi-ku) ใกล้ย่าน Hommachi ซึ่งเป็นจุดที่เดินทางไปยัง Namba, Umeda, Shinsaibashi ได้สะดวกด้วยรถไฟใต้ดินหลายเส้นทาง (Midosuji, Yotsubashi, Chuo Line) ในระยะเดินไม่ไกลจากสถานีหลัก\n\n" +
                            "ห้องพัก & สิ่งอำนวยความสะดวก: มีห้องพักหลายแบบ รวมถึงห้องกว้างรองรับ ครอบครัวหรือกลุ่ม (เช่น 4 คน หรือสูงสุด ~7 คน) และห้องแบบ Twin ห้องพักตกแต่งเรียบง่ายแต่สะอาด มีเตียง Simmons คุณภาพดี",
                    fontSize = 14.sp,
                    color = Color.Black,
                    lineHeight = 22.sp // เว้นระยะบรรทัดให้อ่านง่าย
                )
            }
        }
        if (showDialog) {
            Dialog(onDismissRequest = { showDialog = false }) {
                // เริ่มวาดกล่องสีขาว
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // หัวข้อ
                        Text(
                            text = "ยืนยันการจองหรือไม่?",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 32.dp)
                        )

                        // แถวปุ่ม (ยืนยัน - ยกเลิก)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // ปุ่มยืนยัน (สีส้ม)
                            Button(
                                onClick = {
                                    showDialog = false // ปิด Dialog
                                    // โชว์ข้อความแจ้งเตือน
                                    Toast.makeText(context, "จองสำเร็จเรียบร้อย!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFAB40)),
                                modifier = Modifier.width(90.dp)
                            ) {
                                Text("ยืนยัน", color = Color.White)
                            }

                            // ปุ่มยกเลิก (สีเทา)
                            Button(
                                onClick = { showDialog = false }, // แค่ปิด Dialog
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