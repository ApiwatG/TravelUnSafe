package com.example.travelunsafe

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchHotelScreen(
    onBackClick: () -> Unit,
    onSearchClick: (String) -> Unit // ส่งคำค้นหากลับไป
) {
    // เก็บค่าที่ผู้ใช้พิมพ์
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("จองโรงแรม", color = Color(0xFFFDB067)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // ช่องพิมพ์ค้นหา
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("ที่ไหน:") },
                placeholder = { Text("เมืองที่คุณต้องการไปเที่ยว") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                // ---> แก้ไขตรงนี้ครับ <---
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4DB6FF),
                    unfocusedBorderColor = Color(0xFF4DB6FF),
                    focusedLabelColor = Color(0xFF4DB6FF), // (Option) เปลี่ยนสี Label ตอนกดพิมพ์
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ช่องวันที่ (จำลองหน้าตาไว้ก่อน)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF4DB6FF), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(text = "เมื่อไหร่", fontSize = 12.sp, color = Color.Gray)
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                        Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.Gray)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "26/1 - 27/1", color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ปุ่มค้นหา
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .background(Color(0xFFEEEEEE), RoundedCornerShape(20.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "3", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { onSearchClick(searchQuery) }, // กดปุ่มแล้วส่งคำค้นหา
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFDB067)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ค้นหา", fontSize = 16.sp)
                }
            }
        }
    }
}