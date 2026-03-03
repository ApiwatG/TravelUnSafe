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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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

// ฟังก์ชัน copy รูปไปเก็บใน Internal Storage แล้ว return path
fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val fileName = "hotel_${System.currentTimeMillis()}.jpg"
        val file = File(context.filesDir, fileName)
        FileOutputStream(file).use { output ->
            inputStream.copyTo(output)
        }
        inputStream.close()
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelManageScreen(viewModel: HotelViewModel, onSaveSuccess: () -> Unit) {
    val context = LocalContext.current  // ← เพิ่ม

    var hotelName        by remember { mutableStateOf("") }
    var address          by remember { mutableStateOf("") }
    var pricePerNight    by remember { mutableStateOf("") }
    var maxGuest         by remember { mutableStateOf("") }
    var contactPhone     by remember { mutableStateOf("") }
    var selectedProvince by remember { mutableStateOf("") }
    var provinceExpanded by remember { mutableStateOf(false) }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> imageUri = uri }

    val countries = listOf("Bangkok", "Chiang Mai", "Phuket", "Khon Kaen", "Pattaya")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1565C0))
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onSaveSuccess() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "กลับ",
                    tint = Color.White
                )
            }
            Text(
                text = "จัดการโรงแรม",
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
            // ส่วนเลือกรูปภาพ
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri == null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                        Text("แตะเพื่อเลือกรูปโรงแรม", color = Color.Gray)
                    }
                } else {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
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

            ExposedDropdownMenuBox(
                expanded = provinceExpanded,
                onExpandedChange = { provinceExpanded = !provinceExpanded }
            ) {
                OutlinedTextField(
                    value = selectedProvince, onValueChange = {},
                    label = { Text("เลือกจังหวัด") }, readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(provinceExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(8.dp)
                )
                ExposedDropdownMenu(
                    expanded = provinceExpanded,
                    onDismissRequest = { provinceExpanded = false }
                ) {
                    countries.forEach { item ->
                        DropdownMenuItem(text = { Text(item) }, onClick = {
                            selectedProvince = item
                            provinceExpanded = false
                        })
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = address, onValueChange = { address = it },
                label = { Text("ที่อยู่ / รายละเอียด") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 5, shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = maxGuest, onValueChange = { maxGuest = it },
                label = { Text("จำนวนผู้เข้าพักสูงสุด") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = contactPhone, onValueChange = { contactPhone = it },
                label = { Text("เบอร์โทรศัพท์") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = pricePerNight, onValueChange = { pricePerNight = it },
                label = { Text("ราคา/คืน") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // ← copy รูปไป Internal Storage ก่อน แล้วเอา path ไปเก็บ DB
                    val savedImagePath = imageUri?.let { saveImageToInternalStorage(context, it) }

                    val newHotelId = "H" + System.currentTimeMillis().toString().takeLast(6)
                    val hotel = Hotel(
                        hotelId       = newHotelId,
                        hotelName     = hotelName,
                        address       = address,
                        province      = selectedProvince,
                        pricePerNight = pricePerNight.toIntOrNull() ?: 0,
                        maxGuest      = maxGuest.toIntOrNull() ?: 0,
                        contactPhone  = contactPhone,
                        provincesId   = "PV001",
                        imageUrl      = savedImagePath  // ← String path แทน Uri
                    )
                    android.util.Log.d("HOTEL_DEBUG", "Sending: $hotel")
                    viewModel.insertHotel(hotel)
                    onSaveSuccess()
                },
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("เพิ่มข้อมูล", color = Color.White)
            }
        }
    }
}