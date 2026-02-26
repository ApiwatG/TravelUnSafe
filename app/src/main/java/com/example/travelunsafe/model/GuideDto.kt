package com.example.travelunsafe.model

data class GuideDto(
    val guide_id: String,
    val guide_name: String,
    val username: String,
    val provinces_name: String,
    val guide_detail: String? = null
)
