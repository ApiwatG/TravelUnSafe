package com.example.travelunsafe

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val FriendOrange   = Color(0xFFFBB040)
private val FriendBlue     = Color(0xFF42A5F5)
private val FriendGreen    = Color(0xFF4CAF50)
private val FriendRed      = Color(0xFFEF5350)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendScreen(
    viewModel: TravelViewModel,
    prefs: SharedPreferencesManager,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val userId  = prefs.getUserId()
    val keyboard = LocalSoftwareKeyboardController.current

    var addEmail    by remember { mutableStateOf("") }
    var addMsg      by remember { mutableStateOf<String?>(null) }
    var addIsError  by remember { mutableStateOf(false) }
    var showAddBar  by remember { mutableStateOf(false) }

    val friends          = viewModel.friends
    val receivedRequests = viewModel.receivedRequests
    val sentRequests     = viewModel.sentRequests
    val isLoading        = viewModel.isLoading

    // Load on first open
    LaunchedEffect(userId) { viewModel.loadFriends(userId) }

    // Clear feedback after 3s
    LaunchedEffect(addMsg) {
        if (addMsg != null) {
            kotlinx.coroutines.delay(3000)
            addMsg = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("เพื่อน", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddBar = !showAddBar; addMsg = null }) {
                        Icon(
                            if (showAddBar) Icons.Filled.Close else Icons.Filled.PersonAdd,
                            contentDescription = "Add friend",
                            tint = FriendOrange
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ===== ADD FRIEND BAR =====
            AnimatedVisibility(
                visible = showAddBar,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text("เพิ่มเพื่อนด้วย Email", fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = addEmail,
                            onValueChange = { addEmail = it; addMsg = null },
                            placeholder = { Text("email@example.com", color = Color.Gray) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Send
                            ),
                            keyboardActions = KeyboardActions(onSend = {
                                keyboard?.hide()
                                doSendRequest(viewModel, userId, addEmail, { msg ->
                                    addMsg = msg; addIsError = false; addEmail = ""
                                }, { msg ->
                                    addMsg = msg; addIsError = true
                                })
                            }),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FriendOrange,
                                focusedLabelColor  = FriendOrange,
                                cursorColor        = FriendOrange,
                                focusedTextColor   = Color.Black,
                                unfocusedTextColor = Color.Black
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                keyboard?.hide()
                                doSendRequest(viewModel, userId, addEmail, { msg ->
                                    addMsg = msg; addIsError = false; addEmail = ""
                                }, { msg ->
                                    addMsg = msg; addIsError = true
                                })
                            },
                            enabled = addEmail.isNotBlank() && !isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = FriendOrange),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Color.White)
                            } else {
                                Icon(Icons.Filled.Send, contentDescription = "ส่ง", tint = Color.White)
                            }
                        }
                    }
                    addMsg?.let {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = it,
                            color = if (addIsError) FriendRed else FriendGreen,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {

                // ===== INCOMING REQUESTS =====
                if (receivedRequests.isNotEmpty()) {
                    item {
                        SectionHeader(
                            icon = Icons.Filled.NotificationsActive,
                            title = "คำขอเพื่อน (${receivedRequests.size})",
                            color = FriendOrange
                        )
                    }
                    items(receivedRequests, key = { "recv_${it.friendship_id}" }) { req ->
                        ReceivedRequestCard(
                            request = req,
                            onAccept  = { viewModel.acceptFriendRequest(userId, req.friendship_id) },
                            onDecline = { viewModel.declineFriendRequest(userId, req.friendship_id) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }

                // ===== SENT (PENDING) =====
                if (sentRequests.isNotEmpty()) {
                    item {
                        SectionHeader(
                            icon = Icons.Filled.Schedule,
                            title = "รอการตอบรับ (${sentRequests.size})",
                            color = FriendBlue
                        )
                    }
                    items(sentRequests, key = { "sent_${it.friendship_id}" }) { req ->
                        SentRequestCard(request = req)
                    }
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }

                // ===== FRIENDS LIST =====
                item {
                    SectionHeader(
                        icon = Icons.Filled.People,
                        title = "เพื่อน (${friends.size})",
                        color = Color(0xFF616161)
                    )
                }

                if (friends.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("😊", fontSize = 48.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("ยังไม่มีเพื่อน", fontSize = 15.sp, color = Color.Gray)
                                Text("กดปุ่ม + เพื่อเพิ่มเพื่อนด้วย Email", fontSize = 13.sp, color = Color.LightGray)
                            }
                        }
                    }
                } else {
                    items(friends, key = { "friend_${it.friendship_id}" }) { friend ->
                        FriendCard(
                            friend = friend,
                            onUnfriend = { viewModel.unfriend(context, userId, friend.friendship_id) }
                        )
                    }
                }
            }
        }
    }
}

// helper to call viewModel (avoids lambda capture issues)
private fun doSendRequest(
    viewModel: TravelViewModel,
    userId: String,
    email: String,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit
) {
    if (email.isBlank()) { onError("กรุณาใส่ Email"); return }
    viewModel.sendFriendRequest(
        requesterId    = userId,
        recipientEmail = email.trim(),
        onSuccess      = onSuccess,
        onError        = onError
    )
}

// ===== SECTION HEADER =====
@Composable
private fun SectionHeader(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

// ===== RECEIVED REQUEST CARD =====
@Composable
private fun ReceivedRequestCard(
    request: FriendRequest,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AvatarCircle(name = request.requester_username ?: "?", size = 44)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = request.requester_username ?: "Unknown",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
                Text(
                    text = request.requester_email ?: "",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            // Accept
            IconButton(
                onClick = onAccept,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F5E9))
            ) {
                Icon(Icons.Filled.Check, contentDescription = "ยอมรับ", tint = FriendGreen, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(6.dp))
            // Decline
            IconButton(
                onClick = onDecline,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFEBEE))
            ) {
                Icon(Icons.Filled.Close, contentDescription = "ปฏิเสธ", tint = FriendRed, modifier = Modifier.size(20.dp))
            }
        }
    }
}

// ===== SENT REQUEST CARD =====
@Composable
private fun SentRequestCard(request: FriendRequest) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AvatarCircle(name = request.recipient_username ?: "?", size = 44)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = request.recipient_username ?: "Unknown",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
                Text(
                    text = request.recipient_email ?: "",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFE3F2FD)
            ) {
                Text(
                    text = "รอตอบรับ",
                    fontSize = 12.sp,
                    color = FriendBlue,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

// ===== FRIEND CARD =====
@Composable
private fun FriendCard(
    friend: FriendItem,
    onUnfriend: () -> Unit
) {
    var showConfirm by remember { mutableStateOf(false) }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("ยกเลิกเพื่อน") },
            text  = { Text("ต้องการยกเลิกการเป็นเพื่อนกับ ${friend.friend_username} ใช่หรือไม่?") },
            confirmButton = {
                TextButton(onClick = { showConfirm = false; onUnfriend() }) {
                    Text("ยืนยัน", color = FriendRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) {
                    Text("ยกเลิก")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AvatarCircle(name = friend.friend_username, size = 44)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = friend.friend_username,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
                Text(
                    text = friend.friend_email,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            // Unfriend button
            OutlinedButton(
                onClick = { showConfirm = true },
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = FriendRed),
                border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                    brush = Brush.horizontalGradient(listOf(FriendRed, FriendRed))
                )
            ) {
                Icon(Icons.Filled.PersonRemove, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("เลิกเป็นเพื่อน", fontSize = 12.sp)
            }
        }
    }
}

// ===== AVATAR CIRCLE =====
@Composable
fun AvatarCircle(name: String, size: Int = 40) {
    val colors = listOf(
        Color(0xFF42A5F5), Color(0xFFFBB040), Color(0xFF66BB6A),
        Color(0xFFEF5350), Color(0xFFAB47BC), Color(0xFF26C6DA)
    )
    val color = colors[name.hashCode().and(0x7fffffff) % colors.size]
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.firstOrNull()?.uppercase() ?: "?",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = (size * 0.4f).sp
        )
    }
}
