// CreateGuideActivity.kt
package com.example.travelunsafe

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

class CreateGuideActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                CreateGuideScreen(
                    onBackClick = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGuideScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current

    // --- State ---
    var titleText by remember { mutableStateOf("") }
    var descriptionText by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // SharedPreferences อ่าน user_id ที่ login ไว้
    val localPrefs = remember { SharedPreferencesManager(context) }
    val userId = localPrefs.getUserId()

    // Launcher เปิด Gallery
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "ย้อนกลับ",
                            tint = Color(0xFF1A1A1A)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // ── หัวข้อ ──────────────────────────────────────
            Text(
                text = "สร้างไกด์การเดินทาง",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFA726)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "สร้างไกด์การเดินทางสำหรับผู้ใช้อื่นและ\nวางแผนเส้นทางสำหรับทริป",
                fontSize = 14.sp,
                color = Color(0xFF9E9E9E),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ── ช่อง "ที่ไหน" ───────────────────────────────
            OutlinedTextField(
                value = titleText,
                onValueChange = { titleText = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "เมืองที่คุณต้องการไปเที่ยว",
                        color = Color(0xFFBDBDBD)
                    )
                },
                label = {
                    Text(
                        text = "ที่ไหน:",
                        color = Color(0xFF1A1A1A),
                        fontWeight = FontWeight.SemiBold
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFFA726),
                    unfocusedBorderColor = Color(0xFFBDBDBD),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── ช่อง "คำแนะนำ" ──────────────────────────────
            OutlinedTextField(
                value = descriptionText,
                onValueChange = { descriptionText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                placeholder = {
                    Text(
                        text = "คำแนะนำ",
                        color = Color(0xFFBDBDBD)
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFFA726),
                    unfocusedBorderColor = Color(0xFFBDBDBD),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── ปุ่ม "เพิ่มรูปภาพ" ──────────────────────────
            if (selectedImageUri != null) {
                // แสดงรูปที่เลือกแล้ว
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { imagePickerLauncher.launch("image/*") }
                ) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "รูปภาพที่เลือก",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            } else {
                // ปุ่มเลือกรูป
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { imagePickerLauncher.launch("image/*") },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "เพิ่มรูปภาพ",
                        tint = Color(0xFF1A1A1A),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "เพิ่มรูปภาพ",
                        fontSize = 15.sp,
                        color = Color(0xFF1A1A1A)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── ปุ่ม Submit ──────────────────────────────────
            Button(
                onClick = {
                    // Validate
                    if (titleText.isBlank()) {
                        Toast.makeText(context, "กรุณากรอกสถานที่", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (userId.isBlank()) {
                        Toast.makeText(context, "กรุณาเข้าสู่ระบบก่อน", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // ส่งข้อมูลขึ้น Server
                    isLoading = true
                    CoroutineScope(Dispatchers.IO).launch {
                        val success = uploadGuide(
                            context = context,
                            title = titleText,
                            description = descriptionText,
                            userId = userId,
                            imageUri = selectedImageUri
                        )
                        withContext(Dispatchers.Main) {
                            isLoading = false
                            if (success) {
                                Toast.makeText(context, "✅ สร้างไกด์สำเร็จ!", Toast.LENGTH_SHORT).show()
                                (context as? Activity)?.finish()
                            } else {
                                Toast.makeText(context, "❌ เกิดข้อผิดพลาด กรุณาลองใหม่", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFA726)
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "ไกด์การเดินทาง",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ── ฟังก์ชัน Upload ไปยัง Server ─────────────────────────────────────────────
suspend fun uploadGuide(
    context: android.content.Context,
    title: String,
    description: String,
    userId: String,
    imageUri: Uri?
): Boolean {
    return try {
        val client = OkHttpClient()
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)

        builder.addFormDataPart("title", title)
        builder.addFormDataPart("description", description)
        builder.addFormDataPart("user_id", userId)

        // แนบรูปภาพ (ถ้ามี)
        if (imageUri != null) {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val tempFile = File(context.cacheDir, "guide_upload.jpg")
            val outputStream = FileOutputStream(tempFile)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            builder.addFormDataPart(
                "image",
                tempFile.name,
                tempFile.asRequestBody("image/jpeg".toMediaType())
            )
        }

        val request = Request.Builder()
            .url("http://192.168.1.11:3000/guides")  // ✅ FIXED: port 3000
            .post(builder.build())
            .build()

        val response = client.newCall(request).execute()
        val body = response.body?.string()
        val json = JSONObject(body ?: "{}")

        !json.optBoolean("error", true)

    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}