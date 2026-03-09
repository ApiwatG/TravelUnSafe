package com.example.travelunsafe

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlanScreen(
    viewModel: TripViewModel,
    prefs: SharedPreferencesManager,
    onStartPlanning: (String) -> Unit
) {
    val context = LocalContext.current
    val userId = prefs.getUserId()
    // สถานะการกรอกข้อมูล
    var destination by remember { mutableStateOf("") }
    var selectedProvince by remember { mutableStateOf("") } // ตัวแปรเก็บจังหวัดที่เลือกหรือพิมพ์
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // สถานะการเปิด/ปิด ปฏิทินและ Dropdown
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var expandedProvince by remember { mutableStateOf(false) }

    // 1. ดึงข้อมูลจาก ViewModel มาอัปเดตแบบ Real-time
    val provinceDataList by viewModel.provinceList.collectAsState()

    // 2. ดึงเฉพาะชื่อจังหวัด (provinces_name) ออกมาเป็น List ของ String
    val provinces = provinceDataList.map { it.provinces_name }

    // 💡 3. กรองรายชื่อจังหวัดตามสิ่งที่ผู้ใช้พิมพ์ (ค้นหา)
    val filteredProvinces = provinces.filter {
        it.contains(selectedProvince, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        Text(text = "วางแผนการเดินทางใหม่", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF7B05B))
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "สร้างแผนการเดินทางและวางแผนเส้น\nทางสำหรับทริปที่กำลังจะมาถึงของคุณ", fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(32.dp))

        // 1. ช่องกรอก: ทริป
        OutlinedTextField(
            value = destination, onValueChange = { destination = it },
            placeholder = { Text("ตั้งชื่อทริป", color = Color.Gray) },
            leadingIcon = { Text("ทริป:", modifier = Modifier.padding(start = 16.dp), fontWeight = FontWeight.Bold) },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFF5BB2F9), focusedBorderColor = Color(0xFF5BB2F9))
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 2. ช่องเลือกและค้นหา: จังหวัด (Searchable Dropdown)
        ExposedDropdownMenuBox(
            expanded = expandedProvince,
            onExpandedChange = { expandedProvince = !expandedProvince },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedProvince,
                onValueChange = {
                    selectedProvince = it
                    expandedProvince = true // เปิด Dropdown อัตโนมัติเวลาเริ่มพิมพ์
                },
                readOnly = false, // 💡 เปลี่ยนเป็น false เพื่อให้คีย์บอร์ดเด้งและพิมพ์ค้นหาได้
                placeholder = { Text("ค้นหาหรือเลือกจังหวัด", color = Color.Gray) },
                leadingIcon = { Text("จังหวัด:", modifier = Modifier.padding(start = 16.dp), fontWeight = FontWeight.Bold) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedProvince) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFF5BB2F9), focusedBorderColor = Color(0xFF5BB2F9))
            )

            // 💡 แสดงรายการเมนูที่ผ่านการกรองแล้ว (filteredProvinces)
            if (filteredProvinces.isNotEmpty()) {
                ExposedDropdownMenu(
                    expanded = expandedProvince,
                    onDismissRequest = { expandedProvince = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    filteredProvinces.forEach { province ->
                        DropdownMenuItem(
                            text = { Text(province) },
                            onClick = {
                                selectedProvince = province // นำค่าที่เลือกไปใส่ในช่อง
                                expandedProvince = false // ปิด Dropdown
                            }
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // 3. ช่องเลือก: วันไป
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = startDate,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("เช่น 26/01", color = Color.Gray) },
                leadingIcon = {
                    Column(modifier = Modifier.padding(start = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "วันไป", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                    }
                },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFF5BB2F9), focusedBorderColor = Color(0xFF5BB2F9))
            )
            Box(modifier = Modifier.matchParentSize().clickable { showStartDatePicker = true })
        }
        Spacer(modifier = Modifier.height(16.dp))

        // 4. ช่องเลือก: วันกลับ
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = endDate,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("เช่น 28/01 (เว้นว่างได้)", color = Color.Gray) },
                leadingIcon = {
                    Column(modifier = Modifier.padding(start = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "วันกลับ", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                    }
                },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFF5BB2F9), focusedBorderColor = Color(0xFF5BB2F9))
            )
            Box(modifier = Modifier.matchParentSize().clickable { showEndDatePicker = true })
        }

        Spacer(modifier = Modifier.weight(1f))

        // 5. ปุ่มเริ่มต้นการวางแผน
        Button(
            onClick = {
                isLoading = true
                viewModel.createNewTrip(
                    tripName = destination,
                    province = selectedProvince,
                    startDateStr = startDate,
                    endDateStr = endDate,
                    userId = userId,
                    onSuccess = { newTripId ->
                        isLoading = false
                        Toast.makeText(context, "บันทึกทริปสำเร็จ!", Toast.LENGTH_SHORT).show()
                        onStartPlanning(newTripId)
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
            // ตรวจสอบเพิ่มเติม: ต้องมีชื่อทริป, เลือกจังหวัดที่มีในระบบ, และใส่วันไป
            enabled = destination.isNotBlank() && provinces.contains(selectedProvince) && startDate.isNotBlank() && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(text = "เริ่มต้นการวางแผน", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
        Spacer(modifier = Modifier.height(60.dp))
    }

    // ====== Dialog ปฏิทินวันไป ======
    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    startDate = formatMillisToDate(datePickerState.selectedDateMillis)
                    showStartDatePicker = false
                }) { Text("ตกลง", color = Color(0xFF5BB2F9)) }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) { Text("ยกเลิก", color = Color.Gray) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // ====== Dialog ปฏิทินวันกลับ ======
    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    endDate = formatMillisToDate(datePickerState.selectedDateMillis)
                    showEndDatePicker = false
                }) { Text("ตกลง", color = Color(0xFF5BB2F9)) }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) { Text("ยกเลิก", color = Color.Gray) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

fun formatMillisToDate(millis: Long?): String {
    if (millis == null) return ""
    val formatter = SimpleDateFormat("dd/MM", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    return formatter.format(Date(millis))
}