package com.example.hotel

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelEditScreen(
    viewModel: HotelViewModel,
    hotel: Hotel,
    onSaveSuccess: () -> Unit
) {
    val context      = LocalContext.current
    val message      by viewModel.message.collectAsState()

    var hotelName    by remember { mutableStateOf(hotel.hotelName) }
    var address      by remember { mutableStateOf(hotel.address) }
    var province     by remember { mutableStateOf(hotel.province) }
    var price        by remember { mutableStateOf(hotel.pricePerNight.toString()) }
    var maxGuest     by remember { mutableStateOf(hotel.maxGuest.toString()) }
    var contactPhone by remember { mutableStateOf(hotel.contactPhone) }
    var imageUri     by remember { mutableStateOf<Uri?>(null) }
    var provExpanded by remember { mutableStateOf(false) }
    val countries = listOf("Bangkok", "Chiang Mai", "Phuket", "Khon Kaen", "Pattaya")

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> imageUri = uri }

    LaunchedEffect(message) {
        if (message == "แก้ไขโรงแรมสำเร็จ") {
            kotlinx.coroutines.delay(500)
            onSaveSuccess()
            viewModel.clearMessage()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1565C0))
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onSaveSuccess) {
                Icon(Icons.Default.ArrowBack, contentDescription = "กลับ", tint = Color.White)
            }
            Text(
                text = "แก้ไขโรงแรม",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // รูปโรงแรม
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { imagePicker.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                when {
                    imageUri != null -> {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    !hotel.imageUrl.isNullOrEmpty() -> {
                        AsyncImage(
                            model = File(hotel.imageUrl),  // ← โหลดจาก path ที่เก็บไว้
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    else -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Edit, null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                            Text("แตะเพื่อเปลี่ยนรูป", color = Color.Gray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = hotelName, onValueChange = { hotelName = it },
                label = { Text("ชื่อโรงแรม") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // เลือกจังหวัด
            ExposedDropdownMenuBox(
                expanded = provExpanded,
                onExpandedChange = { provExpanded = !provExpanded }
            ) {
                OutlinedTextField(
                    value = province, onValueChange = {},
                    label = { Text("เลือกจังหวัด") }, readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(provExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(8.dp)
                )
                ExposedDropdownMenu(
                    expanded = provExpanded,
                    onDismissRequest = { provExpanded = false }
                ) {
                    countries.forEach { pv ->
                        DropdownMenuItem(text = { Text(pv) }, onClick = {
                            province = pv; provExpanded = false
                        })
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ที่อยู่
            OutlinedTextField(
                value = address, onValueChange = { address = it },
                label = { Text("ที่อยู่ / รายละเอียด") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 5, shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // จำนวนผู้เข้าพัก
            OutlinedTextField(
                value = maxGuest, onValueChange = { maxGuest = it },
                label = { Text("จำนวนผู้เข้าพักสูงสุด") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // เบอร์โทร
            OutlinedTextField(
                value = contactPhone, onValueChange = { contactPhone = it },
                label = { Text("เบอร์โทรศัพท์") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // ราคา/คืน
            OutlinedTextField(
                value = price, onValueChange = { price = it },
                label = { Text("ราคา/คืน") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // ปุ่มบันทึก
            Button(
                onClick = {
                    // ← copy รูปใหม่ถ้ามีการเลือก ไม่งั้นใช้ path เดิม
                    val savedImagePath = imageUri?.let {
                        saveImageToInternalStorage(context, it)
                    } ?: hotel.imageUrl

                    viewModel.updateHotel(
                        hotel.hotelId, hotelName, address, province,
                        price, maxGuest, contactPhone, savedImagePath
                    )
                },
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("บันทึก", color = Color.White)
            }
        }
    }
}