package com.example.travelunsafe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

data class GuideAuthor(val name: String, val image: String? = null)
data class PlaceItem(val name: String, val location: String? = null, val startTime: String? = null, val endTime: String? = null, val note: String? = null)
data class DaySection(val dayNumber: Int, val date: String? = null, val places: List<PlaceItem>)

data class GuideDetailUiState(
    val title: String = "",
    val description: String? = null,
    val imageUrl: String? = null,
    val author: GuideAuthor = GuideAuthor(""),
    val createdAt: String? = null,
    val days: List<DaySection> = emptyList(),
    val posts: List<GuidePost> = emptyList(),
    val isLoading: Boolean = false,
    val isPostLoading: Boolean = false,
    val error: String? = null
)

class GuideViewModel : ViewModel() {

    var uiState by mutableStateOf(GuideDetailUiState(isLoading = true))
        private set

    private var currentGuideId: String = ""

    fun loadGuide(guide: GuideModel) {
        currentGuideId = guide.guide_id
        uiState = GuideDetailUiState(
            title       = guide.guide_name,
            description = guide.guide_detail,
            imageUrl    = guide.image_guide,
            author      = GuideAuthor(name = guide.username ?: "ไม่ระบุชื่อ", image = guide.image_profile),
            createdAt   = guide.createdAt,
            days        = emptyList(),
            isLoading   = false
        )
        loadPosts()
    }

    private fun loadPosts() {
        viewModelScope.launch {
            uiState = uiState.copy(isPostLoading = true)
            try {
                val posts = TravelClient.travelAPI.getGuidePosts(currentGuideId)
                uiState = uiState.copy(posts = posts, isPostLoading = false)
            } catch (e: Exception) {
                uiState = uiState.copy(isPostLoading = false)
            }
        }
    }

    fun createPost(userId: String, title: String, detail: String?, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = TravelClient.travelAPI.createGuidePost(
                    guideId = currentGuideId,
                    body    = CreateGuidePostRequest(user_id = userId, title = title, detail = detail)
                )
                if (response.isSuccessful && response.body()?.error == false) {
                    loadPosts()
                    onSuccess()
                } else {
                    onError("โพสต์ไม่สำเร็จ")
                }
            } catch (e: Exception) {
                onError("Network error: ${e.message}")
            }
        }
    }
}