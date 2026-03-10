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
    hotels: List<Hotel>,
    isLoading: Boolean = false,
    ratingMap: Map<String, Double> = emptyMap(),

    // ✅ เพิ่ม 2 ตัวแปรนี้เข้ามา เพื่อรับสถานะว่าหาโรงแรมเจอไหม
    isFallbackMode: Boolean = false,
    searchedProvince: String = "",
    province: String = "",

    onBack: () -> Unit = {},
    onHotelClick: (Hotel) -> Unit,
    onApplyFilter: (minPrice: Double?, maxPrice: Double?, maxGuest: Int?, minRating: Double?) -> Unit
) {
    var showFilterSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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

            FilterButtonsRow(
                province = province,
                onFilterClick = { showFilterSheet = true })
            Spacer(modifier = Modifier.height(24.dp))

            // ✅ เพิ่มการแจ้งเตือนผู้ใช้ กรณีหาจังหวัดไม่เจอ แล้วระบบดึงข้อมูลทั้งหมดมาแสดงแทน
            if (isFallbackMode && searchedProvince.isNotBlank() && !isLoading) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Text(
                        text = "ไม่พบโรงแรมใน '$searchedProvince' นี่คือโรงแรมทั้งหมดที่เราแนะนำ",
                        color = Color(0xFFD32F2F),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            if (isLoading) {
                repeat(5) {
                    HotelCardSkeleton()
                    Spacer(modifier = Modifier.height(16.dp))
                }
            } else {
                hotels.forEach { hotel ->
                    HotelCard(hotel = hotel,
                        rating = ratingMap[hotel.hotel_id] ?: 0.0,
                        onClick = { onHotelClick(hotel) })
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // กรณีนี้อาจจะไม่เกิดขึ้นแล้ว เพราะถ้าว่างเราดึงข้อมูลทั้งหมดมาโชว์ แต่เผื่อระบบล่มหรือไม่มีข้อมูลใน DB เลย
                if (hotels.isEmpty() && !isFallbackMode) {
                    Text("ไม่พบข้อมูลโรงแรม", modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }

        if (showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFilterSheet = false },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                FilterSheetContent(
                    onCloseClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) showFilterSheet = false
                        }
                    },
                    onApplyFilter = onApplyFilter // ✅ ส่งต่อ
                )
            }
        }
    }
}