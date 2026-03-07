package com.example.travelunsafe.model

data class ItineraryDto(
    val itinerary_id: String? = null,
    val date: String,
    val start_time: String? = null,
    val end_time: String? = null,
    val place_name: String, // มาจากการ Join table Places
    val location: String    // มาจากการ Join table Places
)
