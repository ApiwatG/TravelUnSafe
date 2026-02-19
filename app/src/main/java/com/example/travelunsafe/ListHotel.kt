package com.example.travelunsafe

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListHotelScreen(
    hotels: List<Hotel>, // <--- 1. รับรายการข้อมูลเข้ามา
    isLoading: Boolean = false, // <--- 2. รับสถานะการโหลด
    onHotelClick: (Hotel) -> Unit // <--- 3. ส่งข้อมูลโรงแรมที่ถูกกดออกไป
) {
    // State ควบคุม Bottom Sheet (เหมือนเดิม)
    var showFilterSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { /* Back Action */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                }
            )
        },
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "โรงแรมและที่พัก",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 24.dp)
            )

            // เรียกใช้ปุ่มจากไฟล์ HotelComponents.kt
            FilterButtonsRow(
                onFilterClick = { showFilterSheet = true }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- แก้ไขตรงนี้: เลือกว่าจะโชว์ Skeleton หรือ ข้อมูลจริง ---
            if (isLoading) {
                // กำลังโหลด: โชว์ Skeleton 5 อัน
                repeat(5) {
                    HotelCardSkeleton()
                    Spacer(modifier = Modifier.height(16.dp))
                }
            } else {
                // โหลดเสร็จแล้ว: วนลูปข้อมูลจริง
                hotels.forEach { hotel ->
                    HotelCard(
                        hotel = hotel,
                        onClick = { onHotelClick(hotel) } // ส่ง object hotel กลับไปเมื่อถูกกด
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // ถ้าไม่มีข้อมูลเลย
                if (hotels.isEmpty()) {
                    Text("ไม่พบข้อมูลโรงแรม", modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }

        // Logic แสดง Bottom Sheet (Filter)
        if (showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFilterSheet = false },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                // เรียกใช้เนื้อหาจากไฟล์ FilterBottomSheet.kt
                FilterSheetContent(
                    onCloseClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) showFilterSheet = false
                        }
                    }
                )
            }
        }
    }
}