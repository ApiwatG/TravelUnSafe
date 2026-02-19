package com.example.travelunsafe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.travelunsafe.ui.theme.TravelUnSafeTheme

import com.example.travelunsafe.ui.theme.DaySection
import com.example.travelunsafe.ui.theme.GuideAuthor
import com.example.travelunsafe.ui.theme.GuideDetailScreen
import com.example.travelunsafe.ui.theme.GuideDetailUiState
import com.example.travelunsafe.ui.theme.PlaceItem

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TravelUnSafeTheme {
                GuideDetailScreen(
                    uiState = GuideDetailUiState(
                        title = "วิธีการเที่ยวญี่ปุ่นใน 2 สัปดาห์",
                        author = GuideAuthor(name = "White Snake"),
                        days = listOf(
                            DaySection(
                                dayNumber = 1,
                                places = listOf(
                                    PlaceItem(
                                        name = "Tokyo",
                                        tags = listOf("เมือง", "นิทรรศการ"),
                                        description = "โตเกียว เมืองหลวงที่พลุกพล่านของญี่ปุ่น ผสมผสานความทันสมัยสุดขีดและความดั้งเดิม..."
                                    ),
                                    PlaceItem(
                                        name = "Shibuya",
                                        tags = listOf(),
                                        description = "Shibuya is a special ward in Tokyo, Japan."
                                    )
                                )
                            )
                        )
                    ),
                    onBackClick = {},
                    onFollowClick = {}
                )
            }
        }
    }
}
