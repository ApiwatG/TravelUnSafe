package com.example.travelunsafe

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController() // สร้างตัวควบคุมการนำทาง

    // 1. เรียกใช้ ViewModel (ตัวดึงข้อมูลจาก Server)
    val viewModel: HotelViewModel = viewModel()

    // 2. ดึงค่า State ออกมาใช้ (Data จริงจาก Database)
    val hotels by viewModel.hotels.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // 3. ตัวแปรเก็บโรงแรมที่ถูกเลือก เพื่อส่งไปหน้า Detail
    var selectedHotel by remember { mutableStateOf<Hotel?>(null) }

    // กำหนดว่าจุดเริ่มต้นคือหน้า "list"
    NavHost(navController = navController, startDestination = "list") {

        // หน้า 1: รายการโรงแรม (List)
        composable("list") {
            ListHotelScreen(
                hotels = hotels,        // ส่งข้อมูลจริงให้หน้า List
                isLoading = isLoading,  // ส่งสถานะโหลด
                onHotelClick = { hotel ->
                    selectedHotel = hotel // เก็บข้อมูลโรงแรมที่ถูกกด
                    navController.navigate("detail") // เปลี่ยนหน้า
                }
            )
        }

        // หน้า 2: รายละเอียด (Detail)
        composable("detail") {
            // เช็คว่ามีข้อมูลโรงแรมส่งมาไหม ป้องกัน error
            if (selectedHotel != null) {
                HotelDetailScreen(
                    hotel = selectedHotel!!, // ส่งข้อมูลโรงแรมให้หน้า Detail
                    onBackClick = { navController.popBackStack() } // ย้อนกลับ
                )
            }
        }
    }
}