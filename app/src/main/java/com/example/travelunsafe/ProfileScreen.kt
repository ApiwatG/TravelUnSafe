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

// ── Date helpers ──────────────────────────────────────────────────────────────

/** Converts "2026-03-18T17:00:00.000Z" → "18/03/2026"  (or just "18/03" etc.) */
fun formatIsoDate(raw: String?): String {
    if (raw.isNullOrBlank()) return ""
    // Take only the date part before 'T'
    val datePart = raw.substringBefore("T").trim()   // "2026-03-18"
    val parts = datePart.split("-")
    return if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else datePart
}

fun formatDateRange(start: String?, end: String?): String {
    val s = formatIsoDate(start)
    val e = formatIsoDate(end)
    return when {
        s.isNotBlank() && e.isNotBlank() && s != e -> "$s – $e"
        s.isNotBlank() -> s
        e.isNotBlank() -> e
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
    onFriendsClick: (() -> Unit)? = null,
    onNavigateToPlanDetail: ((String) -> Unit)? = null,
    onEditGuide: ((GuideModel) -> Unit)? = null// ← NEW
) {
    val context = LocalContext.current
    val localPrefs = prefs ?: remember { SharedPreferencesManager(context) }
    val userId = localPrefs.getUserId()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("การเดินทาง", "คู่มือ")

    val imagePickerLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null && viewModel != null) {
            viewModel.uploadProfileImage(
                context   = context,
                userId    = userId,
                imageUri  = uri,
                onSuccess = { _ ->
                    android.widget.Toast.makeText(context, "อัพโหลดรูปสำเร็จ", android.widget.Toast.LENGTH_SHORT).show()
                    viewModel.loadProfile(userId)
                },
                onError = { msg ->
                    android.widget.Toast.makeText(context, "อัพโหลดไม่สำเร็จ: $msg", android.widget.Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    LaunchedEffect(userId) {
        if (userId.isNotBlank() && viewModel != null) {
            viewModel.loadProfile(userId)
        }
    }

    val profile   = viewModel?.profileSummary
    val isLoading = viewModel?.isLoading ?: false

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        Box {
            Column {
                ProfileHeaderWithMap(
                    onFriendsClick = onFriendsClick,
                    onLogout       = onLogout
                )
                ProfileInfoRow(
                    username   = profile?.user?.username ?: localPrefs.getUsername().ifBlank { "ผู้ใช้" },
                    email      = profile?.user?.email    ?: localPrefs.getEmail(),
                    tripCount  = profile?.stats?.tripCount  ?: 0,
                    guideCount = profile?.stats?.guideCount ?: 0
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 16.dp)
                    .offset(y = 160.dp)
                    .zIndex(2f)
            ) {
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
                        if (!imageUrl.isNullOrBlank()) {
                            val fullUrl = "http://192.168.1.11:3000/$imageUrl"
                            AsyncImage(
                                model            = fullUrl,
                                contentDescription = "Profile Image",
                                contentScale     = ContentScale.Crop,
                                modifier         = Modifier.fillMaxSize().clip(CircleShape)
                            )
                        } else {
                            Text(text = "👤", fontSize = 32.sp)
                        }
                    }
                }
                IconButton(
                    onClick  = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier
                        .size(26.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(OrangeColor)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "เปลี่ยนรูป", tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }

        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor   = Color.White,
            contentColor     = OrangeColor,
            indicator        = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    height   = 3.dp,
                    color    = OrangeColor
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick  = { selectedTab = index },
                    text     = {
                        Text(
                            text       = title,
                            fontSize   = 13.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color      = if (selectedTab == index) OrangeColor else Color(0xFF757575)
                        )
                    }
                )
            }
        }

        if (isLoading) {
            Box(
                modifier            = Modifier.fillMaxWidth().padding(48.dp),
                contentAlignment    = Alignment.Center
            ) { CircularProgressIndicator(color = OrangeColor) }
        } else {
            when (selectedTab) {
                0 -> TravelContent(
                    trips                  = profile?.trips ?: emptyList(),
                    viewModel              = viewModel,
                    userId                 = userId,
                    onNavigateToPlanDetail = onNavigateToPlanDetail
                )
                1 -> GuideContent(guides = profile?.guides ?: emptyList(),
                    viewModel   = viewModel,        // ✅ เพิ่ม
                    userId      = userId,           // ✅ เพิ่ม
                    onEditGuide = onEditGuide )

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

    Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.linearGradient(listOf(Color(0xFFA5D6A7), Color(0xFF80CBC4), Color(0xFF81D4FA), Color(0xFF4FC3F7)))
            )
        )
        Box(modifier = Modifier.fillMaxSize().background(Brush.radialGradient(listOf(Color(0x22FFFFFF), Color.Transparent))))

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
                expanded          = showMenu,
                onDismissRequest  = { showMenu = false },
                modifier          = Modifier.background(Color.White)
            ) {
                DropdownMenuItem(
                    text    = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.People, null, tint = Color(0xFF42A5F5), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("เพื่อน", fontSize = 15.sp)
                        }
                    },
                    onClick = { showMenu = false; onFriendsClick?.invoke() }
                )
                HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFEEEEEE))
                DropdownMenuItem(
                    text    = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.ExitToApp, null, tint = Color(0xFFEF5350), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("ออกจากระบบ", fontSize = 15.sp, color = Color(0xFFEF5350))
                        }
                    },
                    onClick = { showMenu = false; onLogout?.invoke() }
                )
            }
        }
    }
}

// ===== INFO ROW =====
@Composable
fun ProfileInfoRow(
    username: String = "ผู้ใช้",
    email: String    = "",
    tripCount: Int   = 0,
    guideCount: Int  = 0
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(top = 48.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        Row(
            modifier             = Modifier.fillMaxWidth(),
            verticalAlignment    = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = username, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121))
                if (email.isNotBlank()) {
                    Text(text = email, fontSize = 13.sp, color = Color(0xFF757575))
                }
            }
            Button(
                onClick          = { },
                colors           = ButtonDefaults.buttonColors(containerColor = OrangeColor),
                shape            = RoundedCornerShape(24.dp),
                contentPadding   = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.Share, null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("แชร์", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            ProfileStatItem(count = tripCount.toString(),  label = "ทริป")
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

// ===== TAB 1: การเดินทาง =====
@Composable
fun TravelContent(
    trips: List<Trip>                          = emptyList(),
    viewModel: TravelViewModel?                = null,
    userId: String                             = "",
    onNavigateToPlanDetail: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    if (trips.isEmpty()) {
        EmptyStateContent(
            emoji       = "🗺️",
            title       = "คุณยังไม่มีแผนใดๆ",
            subtitle    = "เริ่มวางแผนการเดินทางของคุณ",
            buttonText  = "วางแผนการเดินทาง",
            onButtonClick = { }
        )
    } else {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            trips.forEach { trip ->
                ProfileTripCard(
                    trip                   = trip,
                    onEdit                 = { onNavigateToPlanDetail?.invoke(trip.trip_id) },
                    onDelete               = {
                        viewModel?.deleteTrip(trip.trip_id, userId)
                        if (userId.isNotBlank()) viewModel?.loadProfile(userId)
                    }
                )
            }
        }
    }
}

// ===== TRIP CARD — clean date + edit navigates to PlanDetail =====
@Composable
fun ProfileTripCard(
    trip: Trip,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null
) {
    var showMenu          by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F5F5))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Box(
            modifier           = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Brush.linearGradient(listOf(Color(0xFF80CBC4), Color(0xFF26A69A)))),
            contentAlignment   = Alignment.Center
        ) { Icon(Icons.Default.Map, null, tint = Color.White, modifier = Modifier.size(26.dp)) }

        Spacer(modifier = Modifier.width(12.dp))

        // Info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                trip.trip_name,
                fontSize   = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color      = Color(0xFF212121),
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis
            )
            val dateText = formatDateRange(trip.start_date, trip.end_date)
            if (dateText.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.DateRange,
                        null,
                        tint     = Color(0xFF9E9E9E),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(dateText, fontSize = 12.sp, color = Color(0xFF757575))
                }
            }
            // Province badge
            if (!trip.province.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = Color(0xFF42A5F5), modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(trip.province, fontSize = 11.sp, color = Color(0xFF42A5F5), fontWeight = FontWeight.Medium)
                }
            }
        }

        // 3-dot menu
        Box {
            IconButton(onClick = { showMenu = true }) {
                Icon(Icons.Default.MoreVert, "More", tint = Color(0xFF757575), modifier = Modifier.size(20.dp))
            }
            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                DropdownMenuItem(
                    text         = { Text("ดูรายละเอียด / แก้ไข") },
                    onClick      = { showMenu = false; onEdit?.invoke() },
                    leadingIcon  = { Icon(Icons.Default.Edit, null, tint = Color(0xFF42A5F5)) }
                )
                DropdownMenuItem(
                    text         = { Text("ลบทริป", color = Color(0xFFE53935)) },
                    onClick      = { showMenu = false; showDeleteConfirm = true },
                    leadingIcon  = { Icon(Icons.Default.Delete, null, tint = Color(0xFFE53935)) }
                )
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title            = { Text("ลบทริป?", fontWeight = FontWeight.Bold) },
            text             = { Text("คุณต้องการลบ \"${trip.trip_name}\" ใช่ไหม?") },
            confirmButton    = {
                TextButton(onClick = { showDeleteConfirm = false; onDelete?.invoke() }) {
                    Text("ลบ", color = Color(0xFFE53935), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton    = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("ยกเลิก", color = Color(0xFF757575))
                }
            }
        )
    }
}

// ===== TAB 2: คู่มือ =====
@Composable
fun GuideContent(
    guides: List<GuideModel>                   = emptyList(),
    viewModel: TravelViewModel?                = null,
    userId: String                             = "",
    onEditGuide: ((GuideModel) -> Unit)?       = null   // ✅ เพิ่ม
) {
    if (guides.isEmpty()) {
        EmptyStateContent(
            emoji = "📖", title = "ยังไม่มีคู่มือในระบบ",
            subtitle = "สร้างคู่มือการท่องเที่ยวของคุณ",
            buttonText = "สร้างคู่มือ", onButtonClick = { }
        )
    } else {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            guides.forEach { guide ->
                ProfileGuideCard(
                    guide    = guide,
                    onEdit   = { onEditGuide?.invoke(guide) },
                    onDelete = {
                        viewModel?.deleteGuide(guide.guide_id, userId)
                    }
                )
            }
        }
    }
}

@Composable
fun ProfileGuideCard(
    guide: GuideModel,
    onEdit: (() -> Unit)?   = null,
    onDelete: (() -> Unit)? = null
) {
    var showMenu          by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F5F5))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier         = Modifier.size(52.dp).clip(RoundedCornerShape(10.dp))
                .background(Brush.linearGradient(listOf(Color(0xFFEF9A9A), Color(0xFFE53935)))),
            contentAlignment = Alignment.Center
        ) { Icon(Icons.Default.MenuBook, null, tint = Color.White, modifier = Modifier.size(26.dp)) }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(guide.guide_name, fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
                color = Color(0xFF212121), maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (!guide.guide_detail.isNullOrBlank()) {
                Text(guide.guide_detail, fontSize = 12.sp, color = Color(0xFF757575),
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }

        // ✅ 3-dot menu
        Box {
            IconButton(onClick = { showMenu = true }) {
                Icon(Icons.Default.MoreVert, "More", tint = Color(0xFF757575), modifier = Modifier.size(20.dp))
            }
            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                DropdownMenuItem(
                    text        = { Text("แก้ไขไกด์") },
                    onClick     = { showMenu = false; onEdit?.invoke() },
                    leadingIcon = { Icon(Icons.Default.Edit, null, tint = Color(0xFF42A5F5)) }
                )
                DropdownMenuItem(
                    text        = { Text("ลบไกด์", color = Color(0xFFE53935)) },
                    onClick     = { showMenu = false; showDeleteConfirm = true },
                    leadingIcon = { Icon(Icons.Default.Delete, null, tint = Color(0xFFE53935)) }
                )
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title   = { Text("ลบไกด์?", fontWeight = FontWeight.Bold) },
            text    = { Text("คุณต้องการลบ \"${guide.guide_name}\" ใช่ไหม?") },
            confirmButton = {
                TextButton(onClick = { showDeleteConfirm = false; onDelete?.invoke() }) {
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
}

@Composable
fun EmptyStateContent(
    emoji: String,
    title: String,
    subtitle: String,
    buttonText: String,
    onButtonClick: () -> Unit
) {
    Column(
        modifier              = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment   = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = emoji, fontSize = 64.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = title,    fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121))
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = subtitle, fontSize = 14.sp, color = Color(0xFF757575))
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick        = onButtonClick,
            colors         = ButtonDefaults.buttonColors(containerColor = OrangeColor),
            shape          = RoundedCornerShape(24.dp),
            modifier       = Modifier.height(48.dp)
        ) {
            Text(text = buttonText, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}