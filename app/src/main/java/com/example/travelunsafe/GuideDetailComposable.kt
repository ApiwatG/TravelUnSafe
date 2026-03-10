package com.example.travelunsafe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

// ─────────────────────────────────────────────────────────────
//  Convenience overload — pass guideId + TravelViewModel,
//  and it wires GuideViewModel internally
// ─────────────────────────────────────────────────────────────
@Composable
fun GuideDetailScreen(
    guideId: String,
    viewModel: TravelViewModel,
    sharedPrefsManager: SharedPreferencesManager,
    onBack: () -> Unit
) {
    val guideViewModel: GuideViewModel = viewModel()
    val guide = viewModel.guides.find { it.guide_id == guideId }

    LaunchedEffect(guideId) {
        if (guide != null) guideViewModel.loadGuide(guide)
    }

    if (guide == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("ไม่พบคู่มือ", color = Color.Gray)
        }
        return
    }

    GuideDetailScreen(
        uiState = guideViewModel.uiState,
        prefs   = sharedPrefsManager,
        onBack  = onBack,
        onPost  = { title, detail ->
            guideViewModel.createPost(
                userId    = sharedPrefsManager.getUserId(),
                title     = title,
                detail    = detail,
                onSuccess = {},
                onError   = {}
            )
        }
    )
}

private val Orange     = Color(0xFFFFA726)
private val OrangeDeep = Color(0xFFF57C00)
private val TextPrim   = Color(0xFF212121)
private val TextSec    = Color(0xFF757575)

// ─────────────────────────────────────────────────────────────
//  Root
// ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideDetailComposable(
    uiState: GuideDetailUiState,
    prefs: SharedPreferencesManager,
    onBack: () -> Unit
) {
    var showPostDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = uiState.title.ifBlank { "คู่มือท่องเที่ยว" },
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", modifier = Modifier.size(22.dp))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showPostDialog = true },
                containerColor = Orange,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "เพิ่มโพสต์", modifier = Modifier.size(28.dp))
            }
        },
        containerColor = Color(0xFFF5F5F5)
    ) { innerPadding ->
        when {
            uiState.isLoading -> LoadingView(innerPadding)
            uiState.error != null -> ErrorView(uiState.error, innerPadding)
            else -> GuideContent(uiState = uiState, padding = innerPadding)
        }
    }

    if (showPostDialog) {
        PostDialog(
            onDismiss = { showPostDialog = false },
            onPost    = { title, detail ->
                showPostDialog = false
                // caller handles createPost via viewModel — pass up via callback if needed
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────
//  Overload that accepts onPost callback (wired to ViewModel)
// ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideDetailScreen(
    uiState: GuideDetailUiState,
    prefs: SharedPreferencesManager,
    onBack: () -> Unit,
    onPost: (title: String, detail: String?) -> Unit
) {
    var showPostDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = uiState.title.ifBlank { "คู่มือท่องเที่ยว" },
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", modifier = Modifier.size(22.dp))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showPostDialog = true },
                containerColor = Orange,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "เพิ่มโพสต์", modifier = Modifier.size(28.dp))
            }
        },
        containerColor = Color(0xFFF5F5F5)
    ) { innerPadding ->
        when {
            uiState.isLoading -> LoadingView(innerPadding)
            uiState.error != null -> ErrorView(uiState.error, innerPadding)
            else -> GuideContent(uiState = uiState, padding = innerPadding)
        }
    }

    if (showPostDialog) {
        PostDialog(
            onDismiss = { showPostDialog = false },
            onPost    = { title, detail ->
                showPostDialog = false
                onPost(title, detail)
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────
//  Loading / Error
// ─────────────────────────────────────────────────────────────
@Composable
private fun LoadingView(padding: PaddingValues) {
    Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Orange)
    }
}

@Composable
private fun ErrorView(message: String, padding: PaddingValues) {
    Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.ErrorOutline, null, tint = Color(0xFFE57373), modifier = Modifier.size(52.dp))
            Spacer(Modifier.height(12.dp))
            Text(message, color = TextSec, fontSize = 14.sp)
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Main content
// ─────────────────────────────────────────────────────────────
@Composable
private fun GuideContent(uiState: GuideDetailUiState, padding: PaddingValues) {
    LazyColumn(
        modifier = Modifier.padding(padding).fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item { GuideBannerSection(uiState) }
        item { GuideAuthorCard(uiState.author) }

        if (!uiState.description.isNullOrBlank()) {
            item { GuideDescriptionCard(uiState.description) }
        }

        // ── Posts section ──────────────────────────────
        item {
            GuideSectionTitle("โพสต์จากผู้ใช้", Icons.Default.Forum)
        }

        if (uiState.isPostLoading) {
            item {
                Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Orange, modifier = Modifier.size(28.dp))
                }
            }
        } else if (uiState.posts.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("💬", fontSize = 36.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("ยังไม่มีโพสต์ กดปุ่ม + เพื่อเริ่มแรก!", color = TextSec, fontSize = 13.sp)
                    }
                }
            }
        } else {
            items(uiState.posts, key = { it.post_id }) { post ->
                GuidePostCard(post)
            }
        }

        item { Spacer(Modifier.height(80.dp)) }
    }
}

// ─────────────────────────────────────────────────────────────
//  Hero banner
// ─────────────────────────────────────────────────────────────
@Composable
private fun GuideBannerSection(uiState: GuideDetailUiState) {
    Box(modifier = Modifier.fillMaxWidth().height(240.dp)) {
        if (!uiState.imageUrl.isNullOrBlank()) {
            val imageUrl = if (uiState.imageUrl.startsWith("http")) uiState.imageUrl
            else "http://10.0.2.2:3000/${uiState.imageUrl}"
            AsyncImage(model = imageUrl, contentDescription = uiState.title, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        } else {
            Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Orange, OrangeDeep))), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.MenuBook, null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(80.dp))
            }
        }
        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f)), startY = 120f)))
        Column(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
            Text(uiState.title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
            if (!uiState.createdAt.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(13.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(uiState.createdAt.take(10), color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Author card
// ─────────────────────────────────────────────────────────────
@Composable
private fun GuideAuthorCard(author: GuideAuthor) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            if (!author.image.isNullOrBlank()) {
                val imgUrl = if (author.image.startsWith("http")) author.image else "http://10.0.2.2:3000/${author.image}"
                AsyncImage(model = imgUrl, contentDescription = author.name, contentScale = ContentScale.Crop, modifier = Modifier.size(46.dp).clip(CircleShape))
            } else {
                Box(modifier = Modifier.size(46.dp).clip(CircleShape).background(Color(0xFFCFD8DC)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, null, tint = Color(0xFF607D8B), modifier = Modifier.size(26.dp))
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("ผู้เขียน", fontSize = 11.sp, color = TextSec)
                Text(author.name, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrim)
            }
            Button(
                onClick = { },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Orange),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.PersonAdd, null, modifier = Modifier.size(15.dp), tint = Color.White)
                Spacer(Modifier.width(4.dp))
                Text("ติดตาม", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Description card
// ─────────────────────────────────────────────────────────────
@Composable
private fun GuideDescriptionCard(description: String) {
    var expanded by remember { mutableStateOf(false) }
    val needsToggle = description.length > 50

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(Orange.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.MenuBook, null, tint = Orange, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(10.dp))
                Text("รายละเอียด", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrim)
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color(0xFF424242),
                lineHeight = 22.sp,
                maxLines = if (expanded) Int.MAX_VALUE else 3,
                overflow = if (expanded) TextOverflow.Visible else TextOverflow.Ellipsis
            )
            if (needsToggle) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = if (expanded) "ย่อข้อความ ▲" else "ดูเพิ่มเติม ▼",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Orange,
                    modifier = Modifier.clickable { expanded = !expanded }
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Section title
// ─────────────────────────────────────────────────────────────
@Composable
private fun GuideSectionTitle(title: String, icon: ImageVector) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(Orange.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = Orange, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(10.dp))
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrim)
    }
}

// ─────────────────────────────────────────────────────────────
//  Post card
// ─────────────────────────────────────────────────────────────
@Composable
private fun GuidePostCard(post: GuidePost) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 5.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Author row
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!post.image_profile.isNullOrBlank()) {
                    val imgUrl = if (post.image_profile.startsWith("http")) post.image_profile
                    else "http://10.0.2.2:3000/${post.image_profile}"
                    AsyncImage(model = imgUrl, contentDescription = post.username, contentScale = ContentScale.Crop, modifier = Modifier.size(36.dp).clip(CircleShape))
                } else {
                    Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFFCFD8DC)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, null, tint = Color(0xFF607D8B), modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(post.username, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrim)
                    if (!post.createdAt.isNullOrBlank()) {
                        Text(post.createdAt.take(10), fontSize = 11.sp, color = TextSec)
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(Modifier.height(10.dp))
            // Title
            Text(post.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrim)
            // Detail
            if (!post.detail.isNullOrBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(post.detail, fontSize = 13.sp, color = Color(0xFF424242), lineHeight = 20.sp)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Post dialog
// ─────────────────────────────────────────────────────────────
@Composable
fun PostDialog(
    onDismiss: () -> Unit,
    onPost: (title: String, detail: String?) -> Unit
) {
    var title  by remember { mutableStateOf("") }
    var detail by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape  = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(Orange.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Create, null, tint = Orange, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(10.dp))
                    Text("เขียนโพสต์", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextPrim)
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, null, tint = TextSec, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Title field
                Text("หัวข้อ *", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrim)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; titleError = false },
                    placeholder = { Text("ชื่อหัวข้อ...", color = TextSec) },
                    isError = titleError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = Orange,
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.Bold, fontSize = 15.sp)
                )
                if (titleError) {
                    Text("กรุณากรอกหัวข้อ", fontSize = 11.sp, color = Color(0xFFE53935), modifier = Modifier.padding(start = 4.dp, top = 2.dp))
                }

                Spacer(Modifier.height(14.dp))

                // Detail field
                Text("รายละเอียด", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrim)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = detail,
                    onValueChange = { detail = it },
                    placeholder = { Text("เพิ่มรายละเอียด (ถ้ามี)...", color = TextSec) },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = Orange,
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    maxLines = 6
                )

                Spacer(Modifier.height(20.dp))

                // Post button
                Button(
                    onClick = {
                        if (title.isBlank()) { titleError = true; return@Button }
                        onPost(title.trim(), detail.trim().ifBlank { null })
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Orange)
                ) {
                    Icon(Icons.Default.Send, null, modifier = Modifier.size(18.dp), tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("โพสต์", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Itinerary helpers (kept for future use)
// ─────────────────────────────────────────────────────────────
@Composable
fun GuidePlaceRow(place: PlaceItem, isLast: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.Top) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF4285F4)))
            if (!isLast) Box(modifier = Modifier.width(2.dp).height(36.dp).background(Color(0xFFE0E0E0)))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(place.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrim)
            if (!place.location.isNullOrBlank()) Text(place.location, fontSize = 12.sp, color = TextSec)
            val timeText = buildString {
                if (!place.startTime.isNullOrBlank()) append(place.startTime)
                if (!place.endTime.isNullOrBlank()) append(" – ${place.endTime}")
            }
            if (timeText.isNotBlank()) {
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, null, tint = TextSec, modifier = Modifier.size(12.dp))
                    Spacer(Modifier.width(3.dp))
                    Text(timeText, fontSize = 12.sp, color = TextSec)
                }
            }
            if (!place.note.isNullOrBlank()) {
                Spacer(Modifier.height(3.dp))
                Text(place.note, fontSize = 13.sp, color = Color(0xFF616161), lineHeight = 18.sp)
            }
        }
    }
}