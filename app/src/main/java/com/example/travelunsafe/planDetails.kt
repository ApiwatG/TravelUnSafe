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
    // 💡 แก้ไข: เพิ่ม tripId เข้าไปในพารามิเตอร์นี้
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

    var memberToRemove by remember { mutableStateOf<Friend?>(null) }

    // 💡 แก้ไข: ใช้ DisposableEffect เพื่อดึงข้อมูลใหม่ทุกครั้งที่กลับมาหน้านี้ (รวมถึงตอนเด้งกลับมาจากหน้าโรงแรม)
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
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
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
                        val province = trip?.province?.ifBlank { "all" } ?: "all"
                        // 💡 ส่ง tripId ไปพร้อมกับ province ด้วย
                        onNavigateToHotels(province, tripId)
                    },
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, Color(0xFF00B0FF)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(imageVector = Icons.Default.Hotel, contentDescription = null, tint = Color(0xFF00B0FF), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("โรงแรม", color = Color(0xFF00B0FF), fontSize = 13.sp)
                }
                Icon(Icons.Default.Add, contentDescription = "เพิ่มสถานที่", tint = Color(0xFFF7B05B), modifier = Modifier.clickable { showItineraryDialog = true })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        viewModel.itineraryList.forEach { itinerary ->
            PlaceItemMockup(
                name = itinerary.place_name ?: "ไม่มีชื่อสถานที่",
                time = "เวลา : ${itinerary.start_time ?: "-"} ถึง ${itinerary.end_time ?: "-"} น.",
                onDelete = { viewModel.deleteItinerary(tripId, itinerary.itinerary_id) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ส่วนของค่าใช้จ่าย (Expense)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "ค่าใช้จ่าย", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Icon(Icons.Default.Add, contentDescription = "เพิ่มค่าใช้จ่าย", tint = Color(0xFFF7B05B), modifier = Modifier.clickable { showExpenseDialog = true })
        }
        Spacer(modifier = Modifier.height(16.dp))
        viewModel.expenseList.forEach { expense ->
            ExpenseRow(
                title = expense.expense_name,
                amount = "${expense.amount} บาท",
                onDelete = { viewModel.deleteExpense(tripId, expense.expense_id) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
        Spacer(modifier = Modifier.height(16.dp))

        ExpenseRow(title = "ยอดโดยรวม\nโดยประมาณ", amount = "${viewModel.totalExpense} บาท", isBold = true)
        Spacer(modifier = Modifier.height(100.dp))
    }

    // กล่อง Popup ต่างๆ คงเดิมตามที่คุณส่งมา...
    if (memberToRemove != null) { /* ... */ }
    if (showAddFriendDialog) { /* ... */ }
    if (showEditNameDialog) { /* ... */ }
    if (showItineraryDialog) { /* ... */ }
    if (showExpenseDialog) { /* ... */ }
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