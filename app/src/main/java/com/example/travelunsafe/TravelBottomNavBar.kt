package com.example.travelunsafe

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ===================================================
//  NAVIGATION DESTINATIONS
// ===================================================
sealed class NavDestination(val route: String) {
    object Home          : NavDestination("home")
    object Notifications : NavDestination("notifications")
    object Favorites     : NavDestination("favorites")
    object Profile       : NavDestination("profile")
}

// ===================================================
//  NAV ITEM MODEL
// ===================================================
data class BottomNavItem(
    val destination: NavDestination,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val labelTh: String,
    val badgeCount: Int = 0
)

// ===================================================
//  MAIN BOTTOM NAV BAR COMPOSABLE
// ===================================================
@Composable
fun TravelBottomNavBar(
    currentDestination: NavDestination,
    onItemSelected: (NavDestination) -> Unit,
    onFabClick: () -> Unit,
    isFabOpen: Boolean
) {
    val context = LocalContext.current
    val prefs = remember { SharedPreferencesManager(context) }
    val userId = prefs.getUserId()

    // 💡 ตัวแปรเก็บจำนวนแจ้งเตือน (เริ่มต้นที่ 0)
    var notificationCount by remember { mutableIntStateOf(0) }

    // 💡 ทริคเด็ด: ให้ดึงตัวเลขแจ้งเตือนใหม่ทุกครั้งที่ผู้ใช้ "เปลี่ยนหน้าจอ" (currentDestination เปลี่ยน)
// 💡 ทริคเด็ด: ให้ดึงตัวเลขแจ้งเตือนใหม่ทุกครั้งที่ผู้ใช้ "เปลี่ยนหน้าจอ" หรือเพิ่งได้ userId มา
    LaunchedEffect(currentDestination, userId) {
        if (userId.isNotEmpty()) {
            try {
                val response = TripPlanClient.apiService.getNotificationCount(userId)
                if (response.isSuccessful) {
                    notificationCount = response.body()?.count ?: 0
                    println("🔔 ดึงแจ้งเตือนสำเร็จ: ได้เลข $notificationCount") // เช็คใน Logcat
                } else {
                    println("❌ ดึงแจ้งเตือนพลาด: Error ${response.code()}")
                }
            } catch (e: Exception) {
                println("❌ ดึงแจ้งเตือนพลาด (Network): ${e.message}")
            }
        }
    }

    // 💡 ย้ายลิสต์เมนูมาสร้างไว้ข้างใน เพื่อให้มันอัปเดตตัวเลข notificationCount ได้ทันที
    val items = listOf(
        BottomNavItem(NavDestination.Home, Icons.Filled.Home, Icons.Outlined.Home, "หน้าหลัก"),
        // ส่งตัวเลขที่ดึงมาจาก API เข้าไปโชว์
        BottomNavItem(NavDestination.Notifications, Icons.Filled.Notifications, Icons.Outlined.Notifications, "แจ้งเตือน", badgeCount = notificationCount),
        BottomNavItem(NavDestination.Favorites, Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder, "ถูกใจ"),
        BottomNavItem(NavDestination.Profile, Icons.Filled.Person, Icons.Outlined.Person, "โปรไฟล์")
    )

    val skyBlueGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFB3E5FC), Color(0xFF81D4FA))
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(brush = skyBlueGradient)
            .navigationBarsPadding()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // First 2 nav items
            items.take(2).forEach { item ->
                BottomNavItem(
                    item = item,
                    isSelected = currentDestination == item.destination,
                    onClick = { onItemSelected(item.destination) }
                )
            }

            // Center FAB (+)
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .shadow(8.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable { onFabClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isFabOpen) Icons.Filled.Close else Icons.Filled.Add,
                    contentDescription = if (isFabOpen) "Close" else "Create",
                    tint = Color(0xFF42A5F5),
                    modifier = Modifier.size(28.dp)
                )
            }

            // Last 2 nav items
            items.takeLast(2).forEach { item ->
                BottomNavItem(
                    item = item,
                    isSelected = currentDestination == item.destination,
                    onClick = { onItemSelected(item.destination) }
                )
            }
        }
    }
}

// ===================================================
//  FAB POPUP MENU
// ===================================================
@Composable
fun FabPopupMenu(
    visible: Boolean,
    onDismiss: () -> Unit,
    onGuideClick: () -> Unit,
    onTravelPlanClick: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onDismiss() }
        )
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut(),
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.BottomCenter)
            .padding(bottom = 100.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FabMenuItem(
                icon = Icons.Filled.MenuBook,
                label = "คู่มือ",
                color = Color(0xFFFF9800),
                onClick = { onGuideClick(); onDismiss() }
            )
            FabMenuItem(
                icon = Icons.Filled.Map,
                label = "แผนการเดินทาง",
                color = Color(0xFF42A5F5),
                onClick = { onTravelPlanClick(); onDismiss() }
            )
        }
    }
}

// ===================================================
//  INDIVIDUAL NAV ITEM (internal use)
// ===================================================
@Composable
private fun BottomNavItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val activeColor   = Color.White
    val inactiveColor = Color.White.copy(alpha = 0.6f)

    Column(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BadgedBox(
            badge = {
                // 💡 ถ้าจำนวนแจ้งเตือนมากกว่า 0 ถึงจะโชว์วงกลมสีแดง
                if (item.badgeCount > 0) {
                    Badge(containerColor = Color.Red) {
                        Text(text = item.badgeCount.toString(), fontSize = 10.sp, color = Color.White)
                    }
                }
            }
        ) {
            Icon(
                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                contentDescription = item.labelTh,
                tint = if (isSelected) activeColor else inactiveColor,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = item.labelTh,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) activeColor else inactiveColor
        )
    }
}

// ===================================================
//  FAB MENU ITEM (internal use)
// ===================================================
@Composable
private fun FabMenuItem(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .shadow(6.dp, CircleShape)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(26.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = label, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
    }
}