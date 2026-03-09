package com.example.travelunsafe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

// ──────────────────────────────────────────────────────────
//  UI state passed to GuideDetailScreen
// ──────────────────────────────────────────────────────────
data class GuideAuthor(
    val name: String,
    val image: String? = null
)

data class PlaceItem(
    val name: String,
    val location: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val note: String? = null
)

data class DaySection(
    val dayNumber: Int,
    val date: String? = null,
    val places: List<PlaceItem>
)

data class GuideDetailUiState(
    val title: String = "",
    val description: String? = null,
    val imageUrl: String? = null,
    val author: GuideAuthor = GuideAuthor(""),
    val createdAt: String? = null,
    val days: List<DaySection> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// ──────────────────────────────────────────────────────────
//  ViewModel — maps GuideModel directly (no extra API call)
// ──────────────────────────────────────────────────────────
class GuideViewModel : ViewModel() {

    var uiState by mutableStateOf(GuideDetailUiState(isLoading = true))
        private set

    fun loadGuide(guide: GuideModel) {
        uiState = GuideDetailUiState(
            title       = guide.guide_name,
            description = guide.guide_detail,
            imageUrl    = guide.image_guide,
            author      = GuideAuthor(
                name  = guide.username ?: "ไม่ระบุชื่อ",
                image = guide.image_profile
            ),
            createdAt = guide.createdAt,
            days      = emptyList(),   // guide table has no itinerary rows yet
            isLoading = false
        )
    }
}