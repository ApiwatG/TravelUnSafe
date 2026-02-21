package com.example.travelunsafe

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ProfileScreen(navController: NavController, viewModel: HotelsViewModel, userId: String) {
    val context   = LocalContext.current
    val spManager = remember { SharedPreferencesManager(context) }

    val profile      by viewModel.profile.collectAsState()
    val profileError by viewModel.profileError.collectAsState()
    val isLoading    by viewModel.isLoading.collectAsState()

    var showLogoutDialog by remember { mutableStateOf(false) }
    var rememberEmail    by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        viewModel.getProfile(userId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "โปรไฟล์",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 48.dp, bottom = 32.dp)
        )

        when {
            isLoading -> CircularProgressIndicator()
            profileError != null -> Text(
                text = profileError ?: "",
                color = MaterialTheme.colorScheme.error
            )
            profile != null -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        ProfileRow("User ID",   profile!!.user_id)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        ProfileRow("Username",  profile!!.username)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        ProfileRow("Email",     profile!!.email)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        ProfileRow("Role",      profile!!.role)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        ProfileRow("สมัครเมื่อ", profile!!.createdAt)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { showLogoutDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Logout")
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Logout Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("ออกจากระบบ") },
            text = {
                Column {
                    Text("คุณต้องการออกจากระบบหรือไม่?")
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = rememberEmail,
                            onCheckedChange = { rememberEmail = it }
                        )
                        Text("จำ Email ไว้สำหรับครั้งต่อไป")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (rememberEmail) {
                        profile?.let { spManager.saveEmail(it.email) }
                    } else {
                        spManager.clearEmail()
                    }
                    showLogoutDialog = false
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("No") }
            }
        )
    }
}

@Composable
fun ProfileRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp
        )
        Text(text = value, fontSize = 14.sp)
    }
}