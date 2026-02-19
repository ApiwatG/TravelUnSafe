package com.example.travelunsafe

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSheetContent(onCloseClick: () -> Unit) {
    // State สำหรับเก็บค่าต่างๆ
    var minPrice by remember { mutableStateOf("") }
    var maxPrice by remember { mutableStateOf("") }
    val selectedStars = remember { mutableStateListOf<Int>() }
    var selectedBeds by remember { mutableStateOf("เท่าไหร่ก็ได้") }
    val bedOptions = listOf("เท่าไหร่ก็ได้", "2+", "3+", "4+")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 32.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // หัวข้อ + ปุ่มปิด
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("คัดกรอง", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            IconButton(onClick = onCloseClick) {
                Icon(Icons.Default.Close, contentDescription = "ปิด")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 1. ช่วงราคา
        Text("ช่วงราคา (บาท)", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = minPrice,
                onValueChange = { if(it.all { char -> char.isDigit() }) minPrice = it },
                label = { Text("ต่ำสุด") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = maxPrice,
                onValueChange = { if(it.all { char -> char.isDigit() }) maxPrice = it },
                label = { Text("สูงสุด") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2. ระดับดาว
        Text("ระดับดาว", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            repeat(5) { index ->
                val star = index + 1
                val isSelected = selectedStars.contains(star)
                FilterChip(
                    selected = isSelected,
                    onClick = { if(isSelected) selectedStars.remove(star) else selectedStars.add(star) },
                    label = { Text("★ $star") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFE3F2FD),
                        selectedLabelColor = Color(0xFF00B0FF),
                        selectedLeadingIconColor = Color(0xFF00B0FF)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3. จำนวนเตียงต่อห้อง (Beds per room)
        Text(
            text = "จำนวนเตียงต่อห้อง", // หรือภาษาไทย "จำนวนเตียงต่อห้อง"
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp) // เว้นระยะห่างระหว่างปุ่ม
        ) {
            bedOptions.forEach { option ->
                val isSelected = selectedBeds == option

                // กำหนดสีตามรูปภาพตัวอย่าง
                val backgroundColor = if (isSelected) Color(0xFFE8EAF6) else Color(0xFFF5F5F5) // ม่วงอ่อน / เทา
                val borderColor = if (isSelected) Color(0xFF3F51B5) else Color.Transparent // เส้นขอบน้ำเงินเข้ม
                val textColor = if (isSelected) Color(0xFF3F51B5) else Color.Gray

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .background(backgroundColor)
                        .border(
                            width = if (isSelected) 1.5.dp else 0.dp,
                            color = borderColor,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { selectedBeds = option } // กดแล้วเปลี่ยนค่า
                ) {
                    Text(
                        text = option,
                        color = textColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ปุ่มยืนยัน (กดแล้วปิดหน้าต่าง)
        Button(
            onClick = onCloseClick,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B0FF))
        ) {
            Text("ดูผลลัพธ์", fontSize = 18.sp, color = Color.White)
        }
    }
}