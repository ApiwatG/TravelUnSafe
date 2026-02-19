package com.example.travelunsafe

// Data Class นี้ต้องมีชื่อตัวแปรตรงกับ JSON ที่ส่งมาจาก Server เป๊ะๆ
data class Hotel(
    val hotel_id: String,
    val hotel_name: String,
    val price_per_night: Int,
    val province_name: String?, // รับค่าจากการ Join ตาราง Provinces
    val image_url: String?,      // ชื่อไฟล์รูป หรือ URL
    val address: String?,        // เพิ่มตาม SQL ที่ select มา
    val contact_phone: String?,
    val max_guest: Int = 2,    // ค่า default เผื่อ null
    val hoteldetail: String?
)