package com.example.hotel

sealed class Screen(val route: String) {
    object HotelList   : Screen("hotel_list")
    object HotelManage : Screen("hotel_manage")
    object HotelEdit   : Screen("hotel_edit/{hotelId}") {
        fun createRoute(hotelId: String) = "hotel_edit/$hotelId"
    }
}
