package com.example.hotel

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HotelViewModel : ViewModel() {

    private val _hotels = MutableStateFlow<List<Hotel>>(emptyList())
    val hotels: StateFlow<List<Hotel>> = _hotels

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    // ดึงข้อมูลโรงแรมทั้งหมด
    fun getAllHotels() {
        viewModelScope.launch {
            try {
                val res = HotelClient.instance.getAllHotels()
                if (res.isSuccessful) {
                    _hotels.value = res.body() ?: emptyList()
                }
            } catch (e: Exception) {
                _message.value = e.message
            }
        }
    }

    // เพิ่มข้อมูลโรงแรม
    fun insertHotel(hotel: Hotel) {
        viewModelScope.launch {
            try {
                val res = HotelClient.instance.insertHotel(hotel)
                if (res.isSuccessful) {
                    _message.value = "เพิ่มข้อมูลโรงแรมเรียบร้อยแล้ว"
                    getAllHotels() // refresh ข้อมูล
                }
            } catch (e: Exception) {
                _message.value = e.message
            }
        }
    }

    // Soft Delete
    fun softDeleteHotel(hotelId: String) {
        viewModelScope.launch {
            try {
                val res = HotelClient.instance.softDeleteHotel(hotelId)
                if (res.isSuccessful) {
                    _message.value = "ย้ายโรงแรมไปที่ถังขยะแล้ว"
                    getAllHotels()
                    getDeletedHotels()
                }
            } catch (e: Exception) {
                _message.value = e.message
            }
        }
    }

    private val _deletedHotels = MutableStateFlow<List<Hotel>>(emptyList())
    val deletedHotels: StateFlow<List<Hotel>> = _deletedHotels

    fun getDeletedHotels() {
        viewModelScope.launch {
            try {
                val res = HotelClient.instance.getDeletedHotels()
                if (res.isSuccessful) _deletedHotels.value = res.body() ?: emptyList()
            } catch (e: Exception) { _message.value = e.message }
        }
    }

    fun restoreHotel(hotelId: String) {
        viewModelScope.launch {
            try {
                val res = HotelClient.instance.restoreHotel(hotelId)
                if (res.isSuccessful) {
                    _message.value = "กู้คืนโรงแรมสำเร็จ"
                    getAllHotels()
                    getDeletedHotels()
                }
            } catch (e: Exception) { _message.value = e.message }
        }
    }

    fun updateHotel(
        hotelId: String,
        hotelName: String, address: String, province: String,
        price: String, maxGuest: String, contactPhone: String,
        imagePath: String?
    ) {
        viewModelScope.launch {
            try {
                // 1. สร้าง Object Hotel จากข้อมูลในฟอร์ม
                val hotelUpdate = Hotel(
                    hotelId = hotelId,
                    hotelName = hotelName,
                    address = address,
                    province = province,
                    pricePerNight = price.toIntOrNull() ?: 0,
                    maxGuest = maxGuest.toIntOrNull() ?: 0,
                    contactPhone = contactPhone,
                    imagePath
                )

                // 2. เรียกใช้ API ตาม Interface ใหม่ (ส่งแค่ 2 ตัวแปร)
                val res = HotelClient.instance.updateHotel(hotelId, hotelUpdate)
                android.util.Log.d("HOTEL_DEBUG", "code = ${res.code()}")
                android.util.Log.d("HOTEL_DEBUG", "error = ${res.errorBody()?.string()}")

                if (res.isSuccessful) {
                    _message.value = "แก้ไขโรงแรมสำเร็จ"
                    getAllHotels()
                }
            } catch (e: Exception) {
                _message.value = e.message
            }
        }
    }
    fun clearMessage() { _message.value = null }


}
