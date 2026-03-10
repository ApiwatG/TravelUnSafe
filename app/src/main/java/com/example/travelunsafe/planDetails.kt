package com.example.travelunsafe

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Hotel
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun PlanDetailScreen(
    viewModel: PlanDetailViewModel,
    tripId: String = "1",
    onBack: () -> Unit,
    onNavigateToHotels: (province: String, tripId: String) -> Unit
) {
    val scrollState = rememberScrollState()
    val trip = viewModel.currentTrip
    val context = LocalContext.current

    val prefs = remember { SharedPreferencesManager(context) }
    val currentUserId = prefs.getUserId()

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

    // 💡 ตัวแปรสำหรับเก็บข้อมูลที่จะลบ (เพื่อใช้โชว์ Popup ยืนยัน)
    var memberToRemove by remember { mutableStateOf<Friend?>(null) }
    var bookingToRemove by remember { mutableStateOf<Booking?>(null) }
    var itineraryToRemove by remember { mutableStateOf<Itinerary?>(null) }
    var expenseToRemove by remember { mutableStateOf<Expense?>(null) }

    // รีเฟรชหน้าจอเมื่อกลับมาหน้านี้
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadTripDetail(tripId, currentUserId)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
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

        // แสดงรายชื่อเพื่อนที่อยู่ในทริป
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(viewModel.tripMembers) { member ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
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

        // 💡 แสดงโรงแรมที่พัก
        if (viewModel.tripBookings.isNotEmpty()) {
            Text(text = "โรงแรมที่พัก", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            viewModel.tripBookings.forEach { booking ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(50.dp).clip(CircleShape).background(Color(0xFF5BB2F9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🏨", fontSize = 24.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = booking.hotel_name ?: "โรงแรม", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "เช็คอิน: ${booking.check_in_date.substringBefore("T")}", fontSize = 12.sp, color = Color.DarkGray)
                            Text(text = "เช็คเอาท์: ${booking.check_out_date.substringBefore("T")}", fontSize = 12.sp, color = Color.DarkGray)
                        }
                        // 💡 ปุ่มลบโรงแรม
                        IconButton(onClick = { bookingToRemove = booking }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // ส่วนของสถานที่ (Itinerary)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "ไปไหนบ้าง", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(
                    onClick = {
                        val provName = trip?.province
                            ?: viewModel.availablePlaces.find { it.provinces_id == trip?.provinces_id }?.province
                            ?: "all"

                        val finalProv = if (provName.isBlank()) "all" else provName
                        onNavigateToHotels(finalProv, tripId)
                    },
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, Color(0xFF00B0FF)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(imageVector = Icons.Default.Hotel, contentDescription = null, tint = Color(0xFF00B0FF), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("โรงแรม", color = Color(0xFF00B0FF), fontSize = 13.sp)
                }
                IconButton(onClick = { showItineraryDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "เพิ่มสถานที่", tint = Color(0xFFF7B05B))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        viewModel.itineraryList.forEach { itinerary ->
            PlaceItemMockup(
                name = itinerary.place_name ?: "ไม่มีชื่อสถานที่",
                time = "เวลา : ${itinerary.start_time ?: "-"} ถึง ${itinerary.end_time ?: "-"} น.",
                // 💡 กดแล้วแสดง Popup
                onDelete = { itineraryToRemove = itinerary }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ส่วนของค่าใช้จ่าย (Expense)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "ค่าใช้จ่าย", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = { showExpenseDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "เพิ่มค่าใช้จ่าย", tint = Color(0xFFF7B05B))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        viewModel.expenseList.forEach { expense ->
            ExpenseRow(
                title = expense.expense_name,
                amount = "${expense.amount} บาท",
                // 💡 กดแล้วแสดง Popup
                onDelete = { expenseToRemove = expense }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
        Spacer(modifier = Modifier.height(16.dp))

        ExpenseRow(title = "ยอดโดยรวม\nโดยประมาณ", amount = "${viewModel.totalExpense} บาท", isBold = true)
        Spacer(modifier = Modifier.height(100.dp))
    }

    // =========================================================================
    // โค้ดกล่อง POPUP สำหรับลบรายการต่างๆ
    // =========================================================================

    // 🔴 1. Popup ยืนยันการลบเพื่อน
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

    // 🔴 2. Popup ยืนยันการลบโรงแรม
    if (bookingToRemove != null) {
        AlertDialog(
            onDismissRequest = { bookingToRemove = null },
            title = { Text("ลบโรงแรมที่พัก", fontWeight = FontWeight.Bold) },
            text = { Text("คุณต้องการลบ ${bookingToRemove!!.hotel_name} ออกจากทริปใช่หรือไม่?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteBooking(tripId, bookingToRemove!!.booking_id)
                        Toast.makeText(context, "ลบโรงแรมเรียบร้อย", Toast.LENGTH_SHORT).show()
                        bookingToRemove = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("ลบเลย", color = Color.White) }
            },
            dismissButton = { TextButton(onClick = { bookingToRemove = null }) { Text("ยกเลิก", color = Color.Gray) } }
        )
    }

    // 🔴 3. Popup ยืนยันการลบสถานที่
    if (itineraryToRemove != null) {
        AlertDialog(
            onDismissRequest = { itineraryToRemove = null },
            title = { Text("ลบสถานที่เที่ยว", fontWeight = FontWeight.Bold) },
            text = { Text("คุณต้องการลบ ${itineraryToRemove!!.place_name} ออกจากแผนการเดินทางใช่หรือไม่?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteItinerary(tripId, itineraryToRemove!!.itinerary_id)
                        Toast.makeText(context, "ลบสถานที่เรียบร้อย", Toast.LENGTH_SHORT).show()
                        itineraryToRemove = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("ลบเลย", color = Color.White) }
            },
            dismissButton = { TextButton(onClick = { itineraryToRemove = null }) { Text("ยกเลิก", color = Color.Gray) } }
        )
    }

    // 🔴 4. Popup ยืนยันการลบค่าใช้จ่าย
    if (expenseToRemove != null) {
        AlertDialog(
            onDismissRequest = { expenseToRemove = null },
            title = { Text("ลบค่าใช้จ่าย", fontWeight = FontWeight.Bold) },
            text = { Text("คุณต้องการลบรายการ ${expenseToRemove!!.expense_name} ใช่หรือไม่?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteExpense(tripId, expenseToRemove!!.expense_id)
                        Toast.makeText(context, "ลบค่าใช้จ่ายเรียบร้อย", Toast.LENGTH_SHORT).show()
                        expenseToRemove = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("ลบเลย", color = Color.White) }
            },
            dismissButton = { TextButton(onClick = { expenseToRemove = null }) { Text("ยกเลิก", color = Color.Gray) } }
        )
    }

    // =========================================================================
    // โค้ดกล่อง POPUP เพิ่มข้อมูล (เหมือนเดิม)
    // =========================================================================

    // กล่อง Popup เชิญเพื่อน
    if (showAddFriendDialog) {
        var searchQuery by remember { mutableStateOf("") }

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

                                    TextButton(onClick = {
                                        viewModel.addMemberToTrip(
                                            tripId = tripId,
                                            userId = friend.user_id,
                                            onSuccess = { Toast.makeText(context, "ส่งคำเชิญให้ ${friend.username} เรียบร้อยแล้ว", Toast.LENGTH_SHORT).show() },
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

    // กล่อง Popup สำหรับแก้ไขชื่อทริป
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

    // กล่อง Popup สำหรับเพิ่มสถานที่
    if (showItineraryDialog) {

        val filteredPlaces = viewModel.availablePlaces.filter { place ->
            val tripProvId = trip?.provinces_id ?: ""
            if (tripProvId.isBlank()) {
                true // ถ้าไม่มีรหัสจังหวัดผูกมา ให้โชว์ทั้งหมดไปเลย
            } else {
                place.provinces_id == tripProvId // เทียบรหัสให้ตรงกันเป๊ะๆ
            }
        }

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
                            if (filteredPlaces.isEmpty()) {
                                DropdownMenuItem(text = { Text("ไม่มีข้อมูลสถานที่ในจังหวัดนี้") }, onClick = { expandedPlaceMenu = false })
                            } else {
                                filteredPlaces.forEach { place ->
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

    // กล่อง Popup สำหรับเพิ่มค่าใช้จ่าย
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

@Composable
fun PlaceItemMockup(name: String, time: String, onDelete: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)).background(Color.LightGray))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            Text(text = time, fontSize = 14.sp, color = Color.DarkGray)
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
        }
    }
}

@Composable
fun ExpenseRow(title: String, amount: String, isBold: Boolean = false, onDelete: (() -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, modifier = Modifier.weight(1f), fontSize = 16.sp, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal)
        Text(text = amount, fontSize = 18.sp, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal, color = if(isBold) Color(0xFFF7B05B) else Color.Black)
        if (onDelete != null) {
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(20.dp))
            }
        }
    }
}