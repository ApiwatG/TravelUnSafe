package com.example.travelunsafe

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
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
                    onBackClick = { finish() },
                    onSuccessFinish = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGuideScreen(
    onBackClick: () -> Unit,
    onSuccessFinish: (() -> Unit)? = null,
    editGuide: GuideModel? = null // ถ้าส่งค่านี้มา จะกลายเป็นหน้า Update ทันที
) {
    val context = LocalContext.current
    val isEditMode = editGuide != null

    // กำหนดค่าเริ่มต้น (ถ้าเป็นการแก้ไข ให้ดึงข้อมูลเก่ามาแสดง)
    var titleText by remember { mutableStateOf(editGuide?.guide_name ?: "") }
    var descriptionText by remember { mutableStateOf(editGuide?.guide_detail ?: "") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val localPrefs = remember { SharedPreferencesManager(context) }
    val userId = localPrefs.getUserId()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> selectedImageUri = uri }

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

            Text(
                text = if (isEditMode) "แก้ไขไกด์การเดินทาง" else "สร้างไกด์การเดินทาง",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFA726)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isEditMode) "แก้ไขข้อมูลไกด์การเดินทางของคุณ"
                else "สร้างไกด์การเดินทางสำหรับผู้ใช้อื่นและ\nวางแผนเส้นทางสำหรับทริป",
                fontSize = 14.sp,
                color = Color(0xFF9E9E9E),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ── ที่ไหน (หัวข้อ) ──────────────────────────────────
            OutlinedTextField(
                value = titleText,
                onValueChange = { titleText = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("เมืองที่คุณต้องการไปเที่ยว", color = Color(0xFFBDBDBD)) },
                label = { Text("ที่ไหน:", color = Color(0xFF1A1A1A), fontWeight = FontWeight.SemiBold) },
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

            // ── คำแนะนำ (รายละเอียด) ──────────────────────────────────
            OutlinedTextField(
                value = descriptionText,
                onValueChange = { descriptionText = it },
                modifier = Modifier.fillMaxWidth().height(200.dp),
                placeholder = { Text("คำแนะนำ", color = Color(0xFFBDBDBD)) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFFA726),
                    unfocusedBorderColor = Color(0xFFBDBDBD),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── การแสดงผลรูปภาพ ──────────────────────────────────
            when {
                selectedImageUri != null -> {
                    // เลือกรูปใหม่
                    Box(
                        modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(12.dp))
                            .clickable { imagePickerLauncher.launch("image/*") }
                    ) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "รูปภาพที่เลือก",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                isEditMode && !editGuide!!.image_guide.isNullOrBlank() -> {
                    // แสดงรูปเดิมจากเซิร์ฟเวอร์
                    val existingUrl = if (editGuide.image_guide!!.startsWith("http")) {
                        editGuide.image_guide
                    } else {
                        "http://10.0.2.2:3000/${editGuide.image_guide}"
                    }
                    Box(
                        modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(12.dp))
                            .clickable { imagePickerLauncher.launch("image/*") }
                    ) {
                        AsyncImage(
                            model = existingUrl,
                            contentDescription = "รูปภาพปัจจุบัน",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier.fillMaxSize().background(Color(0x55000000)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("แตะเพื่อเปลี่ยนรูป", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
                else -> {
                    // ไม่มีรูป
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { imagePickerLauncher.launch("image/*") },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, "เพิ่มรูปภาพ", tint = Color(0xFF1A1A1A), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("เพิ่มรูปภาพ", fontSize = 15.sp, color = Color(0xFF1A1A1A))
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── ปุ่ม Submit (บันทึก / สร้าง) ──────────────────────────────
            Button(
                onClick = {
                    if (titleText.isBlank()) {
                        Toast.makeText(context, "กรุณากรอกสถานที่", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (!isEditMode && userId.isBlank()) {
                        Toast.makeText(context, "กรุณาเข้าสู่ระบบก่อน", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true

                    CoroutineScope(Dispatchers.IO).launch {
                        val success = if (isEditMode) {
                            updateGuide(
                                context = context,
                                guideId = editGuide!!.guide_id,
                                title = titleText,
                                description = descriptionText,
                                imageUri = selectedImageUri
                            )
                        } else {
                            uploadGuide(
                                context = context,
                                title = titleText,
                                description = descriptionText,
                                userId = userId,
                                imageUri = selectedImageUri
                            )
                        }

                        withContext(Dispatchers.Main) {
                            isLoading = false
                            if (success) {
                                Toast.makeText(context, if (isEditMode) "✅ แก้ไขสำเร็จ!" else "✅ สร้างไกด์สำเร็จ!", Toast.LENGTH_SHORT).show()
                                onSuccessFinish?.invoke() ?: onBackClick()
                            } else {
                                Toast.makeText(context, "❌ เกิดข้อผิดพลาด กรุณาลองใหม่", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA726)),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text(
                        text = if (isEditMode) "บันทึก" else "สร้างไกด์การเดินทาง",
                        fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
suspend fun updateGuide(
    context: android.content.Context,
    guideId: String,
    title: String,
    description: String,
    imageUri: Uri?
): Boolean {
    return withContext(Dispatchers.IO) { // ย้ายไปทำงาน Background thread
        try {
            val client = OkHttpClient()
            val builder = MultipartBody.Builder().setType(MultipartBody.FORM)

            // 1. ใส่ข้อมูล Text (ต้องใส่แบบนี้ Multer ถึงจะอ่านค่า body.title ออก)
            builder.addFormDataPart("title", title)
            builder.addFormDataPart("description", description)

            // 2. ถ้ามีการเลือกรูปใหม่
            if (imageUri != null) {
                val inputStream = context.contentResolver.openInputStream(imageUri)
                val tempFile = File(context.cacheDir, "update_${System.currentTimeMillis()}.jpg")
                tempFile.outputStream().use { inputStream?.copyTo(it) }
                inputStream?.close()

                builder.addFormDataPart(
                    "image", // ชื่อฟิลด์ต้องตรงกับ upload.single("image") ใน Node.js
                    tempFile.name,
                    tempFile.asRequestBody("image/jpeg".toMediaType())
                )
            }

            // 🚨 จุดสำคัญ: ตรวจสอบ URL อีกครั้ง
            // ตรวจสอบให้แน่ใจว่าไม่มีช่องว่างหรือตัวอักษรแปลกๆ ใน guideId
            val cleanId = guideId.trim()
            val request = Request.Builder()
                .url("http://10.0.2.2:3000/guides/$cleanId") // ต้องเป็น /guides/GI008
                .put(builder.build())
                .build()

            Log.d("API_UPDATE_GUIDE", "Sending PUT to: ${request.url}")

            client.newCall(request).execute().use { response ->
                val bodyStr = response.body?.string() ?: ""
                Log.d("API_UPDATE_GUIDE", "Status Code: ${response.code}")
                Log.d("API_UPDATE_GUIDE", "Response: $bodyStr")

                response.isSuccessful
            }
        } catch (e: Exception) {
            Log.e("API_UPDATE_GUIDE", "Error: ${e.message}")
            false
        }
    }
}

// ── ฟังก์ชันสำหรับการสร้าง (POST) ─────────────────────────────────────────────
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

        if (imageUri != null) {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val tempFile = File(context.cacheDir, "guide_upload_${System.currentTimeMillis()}.jpg")
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
            .url("http://192.168.1.11:3000/guides")
            .post(builder.build())
            .build()

        val response = client.newCall(request).execute()
        response.isSuccessful
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}