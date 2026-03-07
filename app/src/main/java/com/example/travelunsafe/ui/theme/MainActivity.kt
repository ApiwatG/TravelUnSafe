package com.example.travelunsafe.ui.theme

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.travelunsafe.CreateGuideActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // เปิดไปหน้า CreateGuideActivity เลย
        val intent = Intent(this, CreateGuideActivity::class.java)
        startActivity(intent)
        
        // ปิดหน้าตัวเองทิ้ง
        finish()
    }
}
