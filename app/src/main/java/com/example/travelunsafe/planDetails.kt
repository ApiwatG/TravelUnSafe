package com.example.travelunsafe

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PlanDetailScreen(
    viewModel: PlanDetailViewModel,
    tripId: String = "1",
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    val trip = viewModel.currentTrip
    val context = LocalContext.current

    var showExpenseDialog by remember { mutableStateOf(false) }
    var expenseNameInput by remember { mutableStateOf("") }
    var expenseAmountInput by remember { mutableStateOf("") }

    var showItineraryDialog by remember { mutableStateOf(false) }
    var expandedPlaceMenu by remember { mutableStateOf(false) }
    var selectedPlace by remember { mutableStateOf<Place?>(null) }
    var startTimeInput by remember { mutableStateOf("") }
    var endTimeInput by remember { mutableStateOf("") }

    var showEditNameDialog by remember { mutableStateOf(false) }
    var newTripNameInput by remember { mutableStateOf("") }
    var showAddFriendDialog by remember { mutableStateOf(false) }

    // 💡 ตัวแปรเก็บข้อมูลเพื่อนที่กำลังจะถูกเตะออก
    var memberToRemove by remember { mutableStateOf<Friend?>(null) }

    LaunchedEffect(tripId) {
        viewModel.loadTripDetail(tripId)
    }

    if (viewModel.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFFF7B05B))
        }
        return
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Top Bar
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(text = trip?.trip_name ?: "กำลังโหลด...", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                modifier = Modifier
                    .size(18.dp)
                    .clickable {
                        newTripNameInput = trip?.trip_name ?: ""
                        showEditNameDialog = true
                    }
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // แสดงรายชื่อเพื่อนที่ *อยู่ในทริปนี้แล้ว*
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(viewModel.tripMembers) { member ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                            // 💡 ถ้าไม่ใช่เจ้าของทริป พอกดที่รูปจะขึ้นเมนูให้เตะออกได้
                            .clickable {
                                if (member.role != "owner") {
                                    memberToRemove = member
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        val initial = member.username.firstOrNull()?.toString()?.uppercase() ?: "?"
                        Text(text = initial, fontSize = 24.sp, color = Color.DarkGray)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = member.username, fontWeight = FontWeight.Bold)

                    // บอกให้รู้ว่าใครคือหัวหน้าทริป
                    if (member.role == "owner") {
                        Text(text = "เจ้าของ", fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF7B05B).copy(alpha = 0.2f))
                            .clickable { showAddFriendDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = Color(0xFFF7B05B), modifier = Modifier.size(32.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ... โค้ดส่วนวันที่, Itinerary และ Expense แบบเดิม ...
        OutlinedTextField(
            value = if (trip != null) {
                val start = trip.start_date?.substringBefore("T") ?: ""
                val end = trip.end_date?.substringBefore("T") ?: ""
                if (start == end || end.isEmpty()) start else "$start - $end"
            } else "ไม่พบข้อมูลวันที่",
            onValueChange = { },
            readOnly = true,
            placeholder = { Text("วันที่เดินทาง", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFF5BB2F9), focusedBorderColor = Color(0xFF5BB2F9))
        )
        Spacer(modifier = Modifier.height(32.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "ไปไหนบ้าง", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Icon(Icons.Default.Add, contentDescription = "เพิ่มสถานที่", tint = Color(0xFFF7B05B), modifier = Modifier.clickable { showItineraryDialog = true } )
        }
        Spacer(modifier = Modifier.height(16.dp))
        viewModel.itineraryList.forEach { itinerary ->
            PlaceItemMockup(name = itinerary.place_name ?: "ไม่มีชื่อสถานที่", time = "เวลา : ${itinerary.start_time ?: "-"} ถึง ${itinerary.end_time ?: "-"} น.")
            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "ค่าใช้จ่าย", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Icon(Icons.Default.Add, contentDescription = "เพิ่มค่าใช้จ่าย", tint = Color(0xFFF7B05B), modifier = Modifier.clickable { showExpenseDialog = true })
        }
        Spacer(modifier = Modifier.height(16.dp))
        viewModel.expenseList.forEach { expense ->
            ExpenseRow(title = expense.expense_name, amount = "${expense.amount} บาท")
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
        Spacer(modifier = Modifier.height(16.dp))

        ExpenseRow(title = "ยอดโดยรวม\nโดยประมาณ", amount = "${viewModel.totalExpense} บาท", isBold = true)
        Spacer(modifier = Modifier.height(100.dp))
    }

    // 💡 กล่อง Popup ยืนยันการลบเพื่อน
    if (memberToRemove != null) {
        AlertDialog(
            onDismissRequest = { memberToRemove = null },
            title = { Text("ลบเพื่อนออกจากทริป", fontWeight = FontWeight.Bold) },
            text = { Text("คุณต้องการเตะ ${memberToRemove!!.username} ออกจากทริปนี้ใช่หรือไม่?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.removeMemberFromTrip(
                            tripId = tripId,
                            userId = memberToRemove!!.user_id,
                            onSuccess = {
                                Toast.makeText(context, "ลบเพื่อนเรียบร้อย", Toast.LENGTH_SHORT).show()
                                memberToRemove = null
                            },
                            onError = { error -> Toast.makeText(context, error, Toast.LENGTH_SHORT).show() }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("ลบเลย", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { memberToRemove = null }) { Text("ยกเลิก", color = Color.Gray) }
            }
        )
    }

    // 💡 กล่อง Popup สำหรับค้นหาและชวนเพื่อนเข้าทริป (แอดเข้าเลย)
    if (showAddFriendDialog) {
        var searchQuery by remember { mutableStateOf("") }

        // กรองเพื่อนที่ยังไม่อยู่ในทริป และตรงกับคำค้นหา
        val filteredFriends = viewModel.friendsList.filter { f ->
            viewModel.tripMembers.none { it.user_id == f.user_id } && f.username.contains(searchQuery, ignoreCase = true)
        }

        AlertDialog(
            onDismissRequest = { showAddFriendDialog = false },
            title = { Text(text = "เชิญเพื่อนร่วมทริป", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("ค้นหาชื่อเพื่อน...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(modifier = Modifier.heightIn(max = 250.dp)) {
                        if (filteredFriends.isEmpty()) {
                            item { Text("ไม่พบเพื่อน หรือเพื่อนทุกคนอยู่ในทริปนี้แล้ว", color = Color.Gray, modifier = Modifier.padding(8.dp)) }
                        } else {
                            items(filteredFriends) { friend ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.LightGray),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(friend.username.firstOrNull()?.toString()?.uppercase() ?: "?", color = Color.DarkGray, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(text = friend.username, modifier = Modifier.weight(1f))

                                    // 💡 พอกดปุ่มเชิญ จะแอดเพื่อนเข้าทริปทันที
                                    TextButton(onClick = {
                                        viewModel.addMemberToTrip(
                                            tripId = tripId,
                                            userId = friend.user_id,
                                            onSuccess = { Toast.makeText(context, "เชิญ ${friend.username} เรียบร้อย", Toast.LENGTH_SHORT).show() },
                                            onError = { error -> Toast.makeText(context, error, Toast.LENGTH_SHORT).show() }
                                        )
                                    }) { Text("เชิญ", color = Color(0xFFF7B05B), fontWeight = FontWeight.Bold) }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showAddFriendDialog = false }) { Text("ปิด", color = Color.Gray) } }
        )
    }

    // ... ส่วนของ showItineraryDialog, showEditNameDialog และ showExpenseDialog ลอกของเดิมมาใส่ได้เลยครับ
    // ==========================================
    // กล่อง Popup สำหรับแก้ไขชื่อทริป
    // ==========================================
    if (showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = { Text(text = "แก้ไขชื่อทริป", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newTripNameInput,
                    onValueChange = { newTripNameInput = it },
                    label = { Text("ชื่อทริป") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTripNameInput.isNotBlank()) {
                            viewModel.editTripName(
                                tripId = tripId,
                                newName = newTripNameInput,
                                onSuccess = { showEditNameDialog = false },
                                onError = { error -> Toast.makeText(context, error, Toast.LENGTH_SHORT).show() }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF7B05B))
                ) { Text("บันทึก", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) { Text("ยกเลิก", color = Color.Gray) }
            }
        )
    }

    // ==========================================
    // กล่อง Popup สำหรับเพิ่มสถานที่ (Itinerary)
    // ==========================================
    if (showItineraryDialog) {
        AlertDialog(
            onDismissRequest = { showItineraryDialog = false },
            title = { Text(text = "เพิ่มสถานที่", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(onClick = { expandedPlaceMenu = true }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
                            Text(selectedPlace?.place_name ?: "กดเพื่อเลือกสถานที่...", color = Color.Black)
                        }
                        DropdownMenu(expanded = expandedPlaceMenu, onDismissRequest = { expandedPlaceMenu = false }) {
                            if (viewModel.availablePlaces.isEmpty()) {
                                DropdownMenuItem(text = { Text("ไม่มีข้อมูลสถานที่ในระบบ") }, onClick = { expandedPlaceMenu = false })
                            } else {
                                viewModel.availablePlaces.forEach { place ->
                                    DropdownMenuItem(text = { Text(place.place_name) }, onClick = { selectedPlace = place; expandedPlaceMenu = false })
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(value = startTimeInput, onValueChange = { startTimeInput = it }, label = { Text("เวลาเริ่ม (เช่น 09:00)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = endTimeInput, onValueChange = { endTimeInput = it }, label = { Text("เวลาจบ (เช่น 11:30)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (selectedPlace != null && startTimeInput.isNotBlank()) {
                            viewModel.addItinerary(tripId = tripId, placeId = selectedPlace!!.place_id, startTime = startTimeInput, endTime = endTimeInput,
                                onSuccess = { selectedPlace = null; startTimeInput = ""; endTimeInput = ""; showItineraryDialog = false },
                                onError = { error -> Toast.makeText(context, "บันทึกไม่ได้: $error", Toast.LENGTH_LONG).show() }
                            )
                        } else { Toast.makeText(context, "กรุณาเลือกสถานที่และใส่เวลา", Toast.LENGTH_SHORT).show() }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF7B05B))
                ) { Text("บันทึก", color = Color.White) }
            },
            dismissButton = { TextButton(onClick = { showItineraryDialog = false }) { Text("ยกเลิก", color = Color.Gray) } }
        )
    }

    // ==========================================
    // กล่อง Popup สำหรับเพิ่มค่าใช้จ่าย (Expense)
    // ==========================================
    if (showExpenseDialog) {
        AlertDialog(
            onDismissRequest = { showExpenseDialog = false },
            title = { Text(text = "เพิ่มค่าใช้จ่าย", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(value = expenseNameInput, onValueChange = { expenseNameInput = it }, label = { Text("ชื่อรายการ (เช่น ค่ากิน, ค่าที่พัก)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = expenseAmountInput, onValueChange = { expenseAmountInput = it }, label = { Text("ราคา (บาท)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = expenseAmountInput.toIntOrNull() ?: 0
                        if (expenseNameInput.isNotBlank() && amount > 0) {
                            viewModel.addExpense(tripId = tripId, name = expenseNameInput, amount = amount,
                                onSuccess = { expenseNameInput = ""; expenseAmountInput = ""; showExpenseDialog = false },
                                onError = { errorMsg -> Toast.makeText(context, "บันทึกไม่ได้: $errorMsg", Toast.LENGTH_LONG).show() }
                            )
                        } else { Toast.makeText(context, "กรุณากรอกชื่อและราคาให้ถูกต้อง", Toast.LENGTH_SHORT).show() }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF7B05B))
                ) { Text("บันทึก", color = Color.White) }
            },
            dismissButton = { TextButton(onClick = { showExpenseDialog = false }) { Text("ยกเลิก", color = Color.Gray) } }
        )
    }
}

// คอมโพเนนต์ย่อยสำหรับแสดงสถานที่
@Composable
fun PlaceItemMockup(name: String, time: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)).background(Color.LightGray))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = name, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            Text(text = time, fontSize = 14.sp, color = Color.DarkGray)
        }
    }
}

// คอมโพเนนต์ย่อยสำหรับแสดงรายการค่าใช้จ่าย
@Composable
fun ExpenseRow(title: String, amount: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 16.sp, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal)
        Text(text = amount, fontSize = 18.sp, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal, color = if(isBold) Color(0xFFF7B05B) else Color.Black)
    }
}