package com.example.travelunsafe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class NotificationItemData(
    val id: String,         // เป็น friendship_id หรือ trip_id
    val title: String,
    val message: String,
    val isFriendRequest: Boolean = false,
    val isTripInvitation: Boolean = false
)

@Composable
fun NotificationScreen(
    viewModel: TravelViewModel,
    prefs: SharedPreferencesManager
) {
    val userId = prefs.getUserId()

    // 💡 โหลดข้อมูล 2 อย่างตอนเปิดหน้า
    LaunchedEffect(userId) {
        viewModel.loadFriends(userId)
        viewModel.loadTripInvitations(userId)
    }

    // 1. แปลงคำขอเพื่อนเป็น Notification
    val friendNotis = viewModel.receivedRequests.map { req ->
        NotificationItemData(
            id = req.friendship_id,
            title = "คำขอเป็นเพื่อน",
            message = "${req.requester_username} ต้องการเพิ่มคุณเป็นเพื่อน",
            isFriendRequest = true
        )
    }

    // 2. แปลงคำเชิญทริปเป็น Notification
    val tripNotis = viewModel.tripInvitations.map { inv ->
        NotificationItemData(
            id = inv.trip_id,
            title = "คำเชิญเข้าทริป",
            message = "${inv.inviter_name} เชิญคุณเข้าร่วมทริป '${inv.trip_name}'",
            isTripInvitation = true
        )
    }

    // 3. นำมาต่อกัน (รวมลิสต์)
    val notifications = friendNotis + tripNotis

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA))
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().background(Color.White).padding(vertical = 20.dp, horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(text = "การแจ้งเตือน", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }

        if (notifications.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("ไม่มีการแจ้งเตือนใหม่", color = Color.Gray, fontSize = 16.sp)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notifications) { notification ->
                    NotificationCard(
                        item = notification,
                        onAccept = {
                            if (notification.isFriendRequest) viewModel.acceptFriendRequest(userId, notification.id)
                            if (notification.isTripInvitation) viewModel.acceptTripInvitation(notification.id, userId)
                        },
                        onDecline = {
                            if (notification.isFriendRequest) viewModel.declineFriendRequest(userId, notification.id)
                            if (notification.isTripInvitation) viewModel.declineTripInvitation(notification.id, userId)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationCard(
    item: NotificationItemData,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFE3F2FD)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Notifications, contentDescription = null, tint = Color(0xFF5BB2F9))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = item.message, fontSize = 14.sp, color = Color.DarkGray)

                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onAccept,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5BB2F9)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Accept", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ยอมรับ", fontSize = 12.sp)
                    }
                    OutlinedButton(
                        onClick = onDecline,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Decline", modifier = Modifier.size(16.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ปฏิเสธ", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}