package com.example.travelunsafe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

private val LoginGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF00D4FF),
        Color(0xFF40C9F0),
        Color(0xFF7DD8F5),
        Color(0xFFB0E0F0),
        Color(0xFFD6C9B8),
        Color(0xFFE0C8A8)
    )
)

private val LoginButtonOrange = Color(0xFFFBB040)

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: TravelViewModel,
    prefs: SharedPreferencesManager
) {
    var email by remember { mutableStateOf(prefs.getSavedEmail()) }
    var password by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var rememberEmail by remember { mutableStateOf(prefs.getSavedEmail().isNotBlank()) }

    val isLoading = viewModel.isLoading

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LoginGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "TravelUnSafe",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "เข้าสู่ระบบ",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Card container for the form
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; errorMsg = null },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            cursorColor = LoginButtonOrange,
                            focusedBorderColor = LoginButtonOrange,
                            focusedLabelColor = LoginButtonOrange
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; errorMsg = null },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            cursorColor = LoginButtonOrange,
                            focusedBorderColor = LoginButtonOrange,
                            focusedLabelColor = LoginButtonOrange
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = rememberEmail,
                            onCheckedChange = { rememberEmail = it },
                            colors = CheckboxDefaults.colors(checkedColor = LoginButtonOrange)
                        )
                        Text("จำ Email ไว้สำหรับครั้งต่อไป", fontSize = 13.sp, color = Color(0xFF757575))
                    }

                    errorMsg?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (email.isBlank() || password.isBlank()) {
                                errorMsg = "กรุณากรอก Email และ Password"
                                return@Button
                            }
                            viewModel.login(
                                email = email,
                                password = password,
                                prefs = prefs,
                                onSuccess = {
                                    if (rememberEmail) prefs.saveEmail(email) else prefs.clearSavedEmail()

                                    // ดึง Role ที่บันทึกไว้ล่าสุดจาก SharedPreferences
                                    val userRole = prefs.getRole()

                                    // เลือกปลายทางตาม Role
                                    val destination = if (userRole == "admin") {
                                        Screen.Admin.route
                                    } else {
                                        Screen.Main.route
                                    }

                                    navController.navigate(destination) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                    }
                                },
                                onError = { msg -> errorMsg = msg }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = LoginButtonOrange),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Text("เข้าสู่ระบบ", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedButton(
                onClick = { navController.navigate(Screen.Register.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                    brush = Brush.horizontalGradient(listOf(Color.White, Color.White))
                )
            ) {
                Text("สมัครสมาชิก", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
