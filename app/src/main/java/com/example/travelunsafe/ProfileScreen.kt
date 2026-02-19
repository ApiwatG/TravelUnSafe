package com.example.travelunsafe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

// ===== COLORS =====
val OrangeColor = Color(0xFFFF9800)
val LightGrayProfile = Color(0xFFCFD8DC)

@Composable
fun ProfileScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("การเดินทาง", "คู่มือ", "บันทึกการเดินทาง")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // ===== HEADER + PROFILE PIC OVERLAP =====
        // Profile pic must be a SIBLING of both header and info row
        // so zIndex can float it above both
        Box {
            Column {
                ProfileHeaderWithMap()
                ProfileInfoRow()
            }

            // Profile pic — sibling of Column, so zIndex works across both sections
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 16.dp)
                    .offset(y = 160.dp)   // 200dp map height - 40dp (half of 80dp pic)
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
                        Text(text = "👤", fontSize = 32.sp)
                    }
                }
            }
        }

        Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)

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
        when (selectedTab) {
            0 -> TravelContent()
            1 -> GuideContent()
            2 -> TravelPeopleContent()
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

// ===== PROFILE HEADER - MAP BACKGROUND =====
@Composable
fun ProfileHeaderWithMap() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        // Map-like gradient background (greens + blues like Google Maps)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFA5D6A7),  // light green (land)
                            Color(0xFF80CBC4),  // teal
                            Color(0xFF81D4FA),  // sky blue (water)
                            Color(0xFF4FC3F7)   // deeper blue
                        )
                    )
                )
        )

        // Decorative map elements
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0x22FFFFFF),
                            Color.Transparent
                        )
                    )
                )
        )

        // ⋮ Menu button top right
        IconButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(4.dp),
            onClick = { /* options menu */ }
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More options",
                tint = Color(0xFF424242)
            )
        }

    }
}

// ===== NAME / HANDLE / STATS / SHARE =====
@Composable
fun ProfileInfoRow() {
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
            // Name + handle
            Column {
                Text(
                    text = "นายใหญ่",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                Text(
                    text = "@GGez123",
                    fontSize = 13.sp,
                    color = Color(0xFF757575)
                )
            }

            // Share button
            Button(
                onClick = { /* share */ },
                colors = ButtonDefaults.buttonColors(containerColor = OrangeColor),
                shape = RoundedCornerShape(24.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "แชร์",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Stats row
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            ProfileStatItem(count = "0", label = "ผู้ติดตาม")
            ProfileStatItem(count = "0", label = "กำลังติดตาม")
        }
    }
}

@Composable
fun ProfileStatItem(count: String, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = count,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121)
        )
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color(0xFF757575)
        )
    }
}

// ===== TAB 1: การเดินทาง =====
@Composable
fun TravelContent() {
    EmptyStateContent(
        emoji = "🗺️",
        title = "คุณยังไม่มีแผนใดๆ",
        subtitle = "เริ่มวางแผนการเดินทางของคุณ",
        buttonText = "วางแผนการเดินทาง",
        onButtonClick = { }
    )
}

// ===== TAB 2: คู่มือ =====
@Composable
fun GuideContent() {
    EmptyStateContent(
        emoji = "📖",
        title = "คุณยังไม่มีคู่มือใดๆ",
        subtitle = "สร้างคู่มือการท่องเที่ยวของคุณ",
        buttonText = "สร้างคู่มือ",
        onButtonClick = { }
    )
}

// ===== TAB 3: บันทึกการเดินทาง =====
@Composable
fun TravelPeopleContent() {
    EmptyStateContent(
        emoji = "👥",
        title = "คุณยังไม่มีเพื่อนร่วมทาง",
        subtitle = "เชิญเพื่อนมาเดินทางด้วยกัน",
        buttonText = "เชิญเพื่อน",
        onButtonClick = { }
    )
}

// ===== REUSABLE EMPTY STATE =====
@Composable
fun EmptyStateContent(
    emoji: String,
    title: String,
    subtitle: String,
    buttonText: String,
    onButtonClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = emoji, fontSize = 64.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            fontSize = 14.sp,
            color = Color(0xFF757575)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onButtonClick,
            colors = ButtonDefaults.buttonColors(containerColor = OrangeColor),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.height(48.dp)
        ) {
            Text(
                text = buttonText,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}
