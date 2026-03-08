package com.example.travelunsafe

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun CreatePlanScreen(
    navController: NavController,
    viewModel: TripViewModel,
    onStartPlanning: (String) -> Unit
) {
    val context = LocalContext.current
    var destination by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        Text(text = "วางแผนการเดินทางใหม่", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF7B05B))
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "สร้างแผนการเดินทางและวางแผนเส้น\nทางสำหรับทริปที่กำลังจะมาถึงของคุณ", fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = destination, onValueChange = { destination = it },
            placeholder = { Text("ตั้งชื่อทริป", color = Color.Gray) },
            leadingIcon = { Text("ทริป:", modifier = Modifier.padding(start = 16.dp), fontWeight = FontWeight.Bold) },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFF5BB2F9), focusedBorderColor = Color(0xFF5BB2F9))
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = startDate, onValueChange = { startDate = it },
            placeholder = { Text("เช่น 26/1", color = Color.Gray) },
            leadingIcon = {
                Column(modifier = Modifier.padding(start = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "วันไป", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                }
            },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFF5BB2F9), focusedBorderColor = Color(0xFF5BB2F9))
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = endDate, onValueChange = { endDate = it },
            placeholder = { Text("เช่น 28/1 (เว้นว่างได้)", color = Color.Gray) },
            leadingIcon = {
                Column(modifier = Modifier.padding(start = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "วันกลับ", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                }
            },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFF5BB2F9), focusedBorderColor = Color(0xFF5BB2F9))
        )

        // 💡 ลบ TextButton เชิญเพื่อนออกไปแล้ว และดันปุ่มบันทึกลงไปด้านล่าง
        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                isLoading = true
                viewModel.createNewTrip(
                    tripName = destination,
                    startDateStr = startDate,
                    endDateStr = endDate,
                    userId = "U0001", // ตรวจสอบ ID ผู้ใช้ของคุณให้ตรงกับในฐานข้อมูล
                    onSuccess = { newTripId ->
                        isLoading = false
                        Toast.makeText(context, "บันทึกทริปสำเร็จ!", Toast.LENGTH_SHORT).show()

                        // ---------------------------------------------------------
                        // แก้ไข: สั่งให้ไปหน้า SearchHotelScreen หลังจากบันทึกสำเร็จ
                        // ---------------------------------------------------------
                        navController.navigate("search")

                        // หมายเหตุ: ถ้าในอนาคตอยากส่ง ID ทริปไปหน้าค้นหาด้วย เผื่อเอาไปผูกกับโรงแรม
                        // สามารถแก้เป็น navController.navigate("search/$newTripId") ได้ครับ
                    },
                    onError = { errorMessage ->
                        isLoading = false
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                )
            },
            modifier = Modifier.width(220.dp).height(54.dp),
            shape = RoundedCornerShape(27.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF7B05B)),
            // ปุ่มจะกดได้ก็ต่อเมื่อ กรอกข้อมูลครบ และ ไม่ได้โหลดอยู่
            enabled = destination.isNotBlank() && startDate.isNotBlank() && !isLoading
        ) {
            if (isLoading) {
                // ตอนกำลังโหลด โชว์วงกลมหมุนๆ
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(
                    text = "เริ่มต้นการวางแผน",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
        Spacer(modifier = Modifier.height(60.dp))
    }
}