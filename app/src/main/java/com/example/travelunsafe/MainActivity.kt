package com.example.travelunsafe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.travelunsafe.ui.theme.TravelUnSafeTheme
import com.example.travelunsafe.ui.theme.GuideDetailScreen

class MainActivity : ComponentActivity() {
    
    // เรียกใช้ GuideViewModel เพื่อดึงข้อมูลจาก MySQL
    private val viewModel: GuideViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TravelUnSafeTheme {
                // แสดงหน้าจอโดยใช้ข้อมูล (uiState) ที่ดึงมาจาก Database
                GuideDetailScreen(
                    uiState = viewModel.uiState,
                    onBackClick = { finish() }, // เมื่อกดปุ่มย้อนกลับให้ปิดแอปหรือย้อนหน้า
                    onFollowClick = { /* จัดการปุ่มติดตาม */ }
                )
            }
        }
    }
}
