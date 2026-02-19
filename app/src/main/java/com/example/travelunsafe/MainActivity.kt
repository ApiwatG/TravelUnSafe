package com.example.travelunsafe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // เรียกใช้ AppNavigation() จากไฟล์แยกได้เลย
            // ไม่ต้องสร้างฟังก์ชันซ้ำในไฟล์นี้แล้ว
            AppNavigation()
        }
    }
}