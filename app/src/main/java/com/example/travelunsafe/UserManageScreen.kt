package com.example.travelunsafe



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun UserManageScreen(viewModel: UserViewModel = viewModel()) {
    val users by viewModel.users.collectAsState()
    val deletedUsers by viewModel.deletedUsers.collectAsState()
    val message by viewModel.message.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showTrash by remember { mutableStateOf(false) }  // toggle หน้าหลัก / ถังขยะ

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(message) {
        message?.let { snackbarHostState.showSnackbar(it); viewModel.clearMessage() }
    }
    LaunchedEffect(Unit) {
        viewModel.getAllUsers()
    }
    // โหลดถังขยะเมื่อเปิด tab ถังขยะ
    LaunchedEffect(showTrash) {
        if (showTrash) viewModel.getDeletedUsers()
        else viewModel.getAllUsers()
    }

    val filteredUsers = users.filter {
        it.username.contains(searchQuery, ignoreCase = true) ||
                it.email.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1565C0))
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(48.dp))
                Text(
                    text = if (showTrash) "ถังขยะผู้ใช้" else "จัดการผู้ใช้",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                // ปุ่มสลับถังขยะ
                IconButton(onClick = { showTrash = !showTrash }) {
                    Icon(
                        imageVector = if (showTrash) Icons.Default.Person else Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {

                if (!showTrash) {
                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("ค้นหาผู้ใช้") },
                        leadingIcon = { Icon(Icons.Default.Search, null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // รายชื่อ User
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(filteredUsers) { user ->
                            UserCard(
                                user = user,
                                onBan = { viewModel.banUser(user.user_id) },
                                onUnban = { viewModel.unbanUser(user.user_id) },
                                onDelete = { viewModel.softDeleteUser(user.user_id) }
                            )
                        }
                    }
                } else {
                    // ถังขยะ
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(deletedUsers) { user ->
                            DeletedUserCard(
                                user = user,
                                onRestore = { viewModel.restoreUser(user.user_id) },
                                onHardDelete = { viewModel.hardDeleteUser(user.user_id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserCard(user: User, onBan: () -> Unit, onUnban: () -> Unit, onDelete: () -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val isBanned = user.status == 0

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("ยืนยันการลบ") },
            text = { Text("ต้องการลบ \"${user.username}\" ออกจากระบบ?") },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDeleteDialog = false }) {
                    Text("ลบ", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("ยกเลิก") }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(user.username, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(user.email, fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                // Status Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isBanned) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
                ) {
                    Text(
                        text = if (isBanned) "⛔ Banned" else "✅ Active",
                        fontSize = 11.sp,
                        color = if (isBanned) Color.Red else Color(0xFF2E7D32),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            // ปุ่ม Ban / Unban
            IconButton(onClick = { if (isBanned) onUnban() else onBan() }) {
                Icon(
                    imageVector = Icons.Default.Block,
                    contentDescription = null,
                    tint = if (isBanned) Color(0xFF9E9E9E) else Color.Red
                )
            }

            // ปุ่ม ลบ
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
            }
        }
    }
}

@Composable
fun DeletedUserCard(user: User, onRestore: () -> Unit, onHardDelete: () -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("ลบถาวร") },
            text = { Text("ต้องการลบ \"${user.username}\" ถาวร?\nจะไม่สามารถกู้คืนได้อีก") },
            confirmButton = {
                TextButton(onClick = { onHardDelete(); showDeleteDialog = false }) {
                    Text("ลบถาวร", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("ยกเลิก") }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(user.username, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Gray)
                Text(user.email, fontSize = 12.sp, color = Color.Gray)
            }
            IconButton(onClick = onRestore) {
                Icon(Icons.Default.Restore, contentDescription = "กู้คืน", tint = Color(0xFF1565C0))
            }
            IconButton(onClick = { showDeleteDialog = true }) {  // ← เพิ่มปุ่มกลับมา
                Icon(Icons.Default.DeleteForever, contentDescription = "ลบถาวร", tint = Color.Red)
            }
        }
    }
}