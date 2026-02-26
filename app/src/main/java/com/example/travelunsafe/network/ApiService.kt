package com.example.travelunsafe.network

import com.example.travelunsafe.model.ItineraryDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("itinerary")
    suspend fun getItinerary(
        @Query("trip_id") tripId: String
    ): List<ItineraryDto>
}
