package com.example.hotel

import com.google.gson.annotations.SerializedName

data class Hotel(
    @SerializedName("hotel_id")        val hotelId: String = "",
    @SerializedName("hotel_name")      val hotelName: String = "",
    @SerializedName("address")         val address: String = "",
    @SerializedName("province")        val province: String = "",
    @SerializedName("price_per_night") val pricePerNight: Int = 0,
    @SerializedName("max_guest")       val maxGuest: Int = 0,
    @SerializedName("contact_phone")   val contactPhone: String = "",
    @SerializedName("image_url")       val imageUrl: String? = null,
    @SerializedName("provinces_id")    val provincesId: String? = null,
    @SerializedName("createdAt")       val createdAt: String? = null,
    @SerializedName("updatedAt")       val updatedAt: String? = null,
    @SerializedName("deletedAt")       val deletedAt: String? = null
)

