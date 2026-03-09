package com.example.travelunsafe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

fun formatDateRange(start: String?, end: String?): String {
    return when {
        !start.isNullOrBlank() && !end.isNullOrBlank() -> "$start – $end"
        !start.isNullOrBlank() -> start
        !end.isNullOrBlank() -> end
        else -> ""
    }
}

val OrangeColor = Color(0xFFFF9800)
val LightGrayProfile = Color(0xFFCFD8DC)
@Composable
fun ProfileScreen(
    viewModel: TravelViewModel? = null,
    prefs: SharedPreferencesManager? = null,
    onLogout: (() -> Unit)? = null,
    onFriendsClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val localPrefs = prefs ?: remember { SharedPreferencesManager(context) }
    val userId = localPrefs.getUserId()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("การเดินทาง", "คู่มือ", "บันทึกการเดินทาง")

    // Image picker launcher
    val imagePickerLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null && viewModel != null) {
            viewModel.uploadProfileImage(
                context   = context,
                userId    = userId,
                imageUri  = uri,
                onSuccess = { path ->
                    android.widget.Toast.makeText(context, "อัพโหลดรูปสำเร็จ", android.widget.Toast.LENGTH_SHORT).show()
                    viewModel.loadProfile(userId)
                },
                onError = { msg ->
                    android.widget.Toast.makeText(context, "อัพโหลดไม่สำเร็จ: $msg", android.widget.Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    // Load profile when screen opens
    LaunchedEffect(userId) {
        android.util.Log.d("PROFILE_DEBUG", "LaunchedEffect: userId=[$userId], viewModel=${viewModel != null}")
        if (userId.isNotBlank() && viewModel != null) {
            android.util.Log.d("PROFILE_DEBUG", "Calling loadProfile($userId)")
            viewModel.loadProfile(userId)
        }
    }

    val profile = viewModel?.profileSummary
    val isLoading = viewModel?.isLoading ?: false

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // ===== HEADER + PROFILE PIC OVERLAP =====
        Box {
            Column {
                ProfileHeaderWithMap(
                    onFriendsClick = onFriendsClick,
                    onLogout = onLogout
                )
                ProfileInfoRow(
                    username = profile?.user?.username ?: localPrefs.getUsername().ifBlank { "ผู้ใช้" },
                    email = profile?.user?.email ?: localPrefs.getEmail(),
                    tripCount = profile?.stats?.tripCount ?: 0,
                    guideCount = profile?.stats?.guideCount ?: 0
                )
            }

            // Profile pic — floats above both sections via zIndex
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 16.dp)
                    .offset(y = 160.dp)
                    .zIndex(2f)
            ) {
                // Avatar circle
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(LightGrayProfile),
                        contentAlignment = Alignment.Center
                    ) {
                        val imageUrl = profile?.user?.image_profile
                        android.util.Log.d("PROFILE_DEBUG", "image_profile = [$imageUrl]")
                        android.util.Log.d("PROFILE_DEBUG", "full profile user = ${profile?.user}")
                        if (!imageUrl.isNullOrBlank()) {
                            val fullUrl = "http://192.168.1.11:3000/$imageUrl"
                            AsyncImage(
                                model = fullUrl,
                                contentDescription = "Profile Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize().clip(CircleShape)
                            )
                        } else {
                            Text(text = "👤", fontSize = 32.sp)
                        }
                    }
                }
                // ➕ Upload button (bottom-right of avatar)
                IconButton(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier
                        .size(26.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(OrangeColor)
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "เปลี่ยนรูป",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)

        // ===== TAB ROW =====
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = OrangeColor,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    height = 3.dp,
                    color = OrangeColor
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) OrangeColor else Color(0xFF757575)
                        )
                    }
                )
            }
        }

        // ===== TAB CONTENT =====
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(48.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = OrangeColor)
            }
        } else {
            when (selectedTab) {
                0 -> TravelContent(trips = profile?.trips ?: emptyList())
                1 -> GuideContent(guides = profile?.guides ?: emptyList())
                2 -> TravelPeopleContent()
            }
        }

        Spacer(modifier = Modifier.height(80.dp))

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ===== MAP HEADER =====
@Composable
fun ProfileHeaderWithMap(
    onFriendsClick: (() -> Unit)? = null,
    onLogout: (() -> Unit)? = null
) {
    var showMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxWidth().height(200.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.linearGradient(listOf(Color(0xFFA5D6A7), Color(0xFF80CBC4), Color(0xFF81D4FA), Color(0xFF4FC3F7)))
            )
        )
        Box(modifier = Modifier.fillMaxSize().background(Brush.radialGradient(listOf(Color(0x22FFFFFF), Color.Transparent))))

        // 3-dot button + dropdown
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(4.dp)
        ) {
            IconButton(onClick = { showMenu = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color(0xFF424242))
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier.background(Color.White)
            ) {
                // Friends option
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.People,
                                contentDescription = null,
                                tint = Color(0xFF42A5F5),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("เพื่อน", fontSize = 15.sp)
                        }
                    },
                    onClick = {
                        showMenu = false
                        onFriendsClick?.invoke()
                    }
                )

                HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFEEEEEE))

                // Logout option
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.ExitToApp,
                                contentDescription = null,
                                tint = Color(0xFFEF5350),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("ออกจากระบบ", fontSize = 15.sp, color = Color(0xFFEF5350))
                        }
                    },
                    onClick = {
                        showMenu = false
                        // Clear session before navigating
                        onLogout?.invoke()
                    }
                )
            }
        }
    }
}

// ===== INFO ROW — real data =====
@Composable
fun ProfileInfoRow(
    username: String = "ผู้ใช้",
    email: String = "",
    tripCount: Int = 0,
    guideCount: Int = 0
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(top = 48.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = username, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121))
                if (email.isNotBlank()) {
                    Text(text = email, fontSize = 13.sp, color = Color(0xFF757575))
                }
            }
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = OrangeColor),
                shape = RoundedCornerShape(24.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.Share, null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("แชร์", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            ProfileStatItem(count = tripCount.toString(), label = "ทริป")
            ProfileStatItem(count = guideCount.toString(), label = "คู่มือ")
        }
    }
}

@Composable
fun ProfileStatItem(count: String, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = count, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121))
        Text(text = label, fontSize = 14.sp, color = Color(0xFF757575))
    }
}

// ===== TAB 1: การเดินทาง — real trips from DB =====
@Composable
fun TravelContent(
    trips: List<Trip> = emptyList(),
    viewModel: TravelViewModel? = null,
    userId: String = ""
) {
    val context = LocalContext.current

    if (trips.isEmpty()) {
        EmptyStateContent(
            emoji = "🗺️",
            title = "คุณยังไม่มีแผนใดๆ",
            subtitle = "เริ่มวางแผนการเดินทางของคุณ",
            buttonText = "วางแผนการเดินทาง",
            onButtonClick = { }
        )
    } else {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            trips.forEach { trip ->
                ProfileTripCard(
                    trip = trip,
                    onEdit = { newName ->
                        // Call update API
                        viewModel?.let { vm ->
                            // You need to add an updateTrip function or reuse existing
                            // For now, use the API directly
                        }
                    },
                    onDelete = {
                        viewModel?.deleteTrip(trip.trip_id, userId)
                        // Reload profile after delete
                        if (userId.isNotBlank()) {
                            viewModel?.loadProfile(userId)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ProfileTripCard(
    trip: Trip,
    onEdit: ((String) -> Unit)? = null,
    onDelete: (() -> Unit)? = null
) {
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(trip.trip_name) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F5F5))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Brush.linearGradient(listOf(Color(0xFF80CBC4), Color(0xFF26A69A)))),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Map, null, tint = Color.White, modifier = Modifier.size(26.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = trip.trip_name,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF212121),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            val dateText = formatDateRange(trip.start_date, trip.end_date)
            if (dateText.isNotBlank()) {
                Text(text = dateText, fontSize = 12.sp, color = Color(0xFF757575))
            }
        }

        // 3-dot menu
        Box {
            IconButton(onClick = { showMenu = true }) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = Color(0xFF757575),
                    modifier = Modifier.size(20.dp)
                )
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("แก้ไขชื่อ") },
                    onClick = {
                        showMenu = false
                        editName = trip.trip_name
                        showEditDialog = true
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Edit, null, tint = Color(0xFF42A5F5))
                    }
                )
                DropdownMenuItem(
                    text = { Text("ลบทริป", color = Color(0xFFE53935)) },
                    onClick = {
                        showMenu = false
                        showDeleteConfirm = true
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Delete, null, tint = Color(0xFFE53935))
                    }
                )
            }
        }
    }

    // Delete confirmation
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("ลบทริป?", fontWeight = FontWeight.Bold) },
            text = { Text("คุณต้องการลบ \"${trip.trip_name}\" ใช่ไหม?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    onDelete?.invoke()
                }) {
                    Text("ลบ", color = Color(0xFFE53935), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("ยกเลิก", color = Color(0xFF757575))
                }
            }
        )
    }

    // Edit dialog
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("แก้ไขชื่อทริป", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = editName,
                    onValueChange = { editName = it },
                    label = { Text("ชื่อทริป") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showEditDialog = false
                    if (editName.isNotBlank()) onEdit?.invoke(editName)
                }) {
                    Text("บันทึก", color = OrangeColor, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("ยกเลิก", color = Color(0xFF757575))
                }
            }
        )
    }
}

// ===== TAB 2: คู่มือ — real guides from DB =====
@Composable
fun GuideContent(guides: List<GuideModel> = emptyList()) {
    if (guides.isEmpty()) {
        EmptyStateContent(emoji = "📖", title = "ยังไม่มีคู่มือในระบบ", subtitle = "สร้างคู่มือการท่องเที่ยวของคุณ", buttonText = "สร้างคู่มือ", onButtonClick = { })
    } else {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            guides.forEach { guide -> ProfileGuideCard(guide = guide) }
        }
    }
}

@Composable
fun ProfileGuideCard(guide: GuideModel) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFFF5F5F5)).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(52.dp).clip(RoundedCornerShape(10.dp)).background(Brush.linearGradient(listOf(Color(0xFFEF9A9A), Color(0xFFE53935)))),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.MenuBook, null, tint = Color.White, modifier = Modifier.size(26.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = guide.guide_name, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF212121), maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (!guide.provinces_name.isNullOrBlank()) {
                Text(text = guide.provinces_name, fontSize = 12.sp, color = Color(0xFF757575))
            }
        }
    }
}

// ===== TAB 3 =====
@Composable
fun TravelPeopleContent() {
    EmptyStateContent(emoji = "👥", title = "คุณยังไม่มีเพื่อนร่วมทาง", subtitle = "เชิญเพื่อนมาเดินทางด้วยกัน", buttonText = "เชิญเพื่อน", onButtonClick = { })
}

@Composable
fun EmptyStateContent(emoji: String, title: String, subtitle: String, buttonText: String, onButtonClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = emoji, fontSize = 64.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121))
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = subtitle, fontSize = 14.sp, color = Color(0xFF757575))
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onButtonClick, colors = ButtonDefaults.buttonColors(containerColor = OrangeColor), shape = RoundedCornerShape(24.dp), modifier = Modifier.height(48.dp)) {
            Text(text = buttonText, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}