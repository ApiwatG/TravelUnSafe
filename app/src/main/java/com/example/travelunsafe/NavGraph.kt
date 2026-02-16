package com.example.travelunsafe

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController() // สร้างตัวควบคุมการนำทาง

    // กำหนดว่าจุดเริ่มต้นคือหน้า "list"
    NavHost(navController = navController, startDestination = "list") {

        // หน้า 1: รายการโรงแรม (List)
        composable("list") {
            ListHotelScreen(
                // ส่งฟังก์ชัน: เมื่อกดเลือกโรงแรม ให้สั่ง navigate ไปหน้า "detail"
                onHotelClick = { navController.navigate("detail") }
            )
        }

        // หน้า 2: รายละเอียด (Detail)
        composable("detail") {
            HotelDetailScreen(
                // ส่งฟังก์ชัน: เมื่อกดปุ่มกลับ ให้สั่ง popBackStack (ย้อนกลับ)
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}