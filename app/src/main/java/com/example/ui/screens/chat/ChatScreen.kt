package com.example.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.data.model.User
import com.example.ui.components.DashboardBrandHeroBanner
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    currentUser: User,
    users: List<User>,
    messages: List<ChatMessage>,
    onSendMessage: (messageText: String, receiverId: String, receiverName: String, quickTopic: String) -> Unit,
    onDeleteMessage: (String) -> Unit
) {
    var selectedUserForAdmin by remember { mutableStateOf<User?>(null) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Percakapan, 1: Broadcast / Info Cepat

    if (currentUser.role == "Admin") {
        if (selectedUserForAdmin != null) {
            // Admin active conversation with selected user
            AdminUserChatView(
                currentUser = currentUser,
                targetUser = selectedUserForAdmin!!,
                allMessages = messages,
                onBack = { selectedUserForAdmin = null },
                onSendMessage = onSendMessage,
                onDeleteMessage = onDeleteMessage
            )
        } else {
            // Admin Inbox List
            AdminChatInboxScreen(
                currentUser = currentUser,
                users = users.filter { it.role == "Penyetor" },
                messages = messages,
                selectedTab = selectedTab,
                onTabChange = { selectedTab = it },
                onSelectUser = { selectedUserForAdmin = it },
                onSendBroadcast = { msg ->
                    onSendMessage(msg, "ALL", "Semua Penyetor", "Broadcast Admin")
                }
            )
        }
    } else {
        // Penyetor Chat Room with Admin TPS 3R
        PenyetorChatRoom(
            currentUser = currentUser,
            messages = messages.filter {
                it.senderId == currentUser.id ||
                it.receiverId == currentUser.id ||
                it.receiverId == "ADMIN_TPS3R" ||
                it.receiverId == "ALL"
            },
            onSendMessage = { text, topic ->
                onSendMessage(text, "ADMIN_TPS3R", "Admin TPS 3R Vale", topic)
            },
            onDeleteMessage = onDeleteMessage
        )
    }
}

/**
 * Screen for Penyetor (User) talking with Admin TPS 3R
 */
@Composable
fun PenyetorChatRoom(
    currentUser: User,
    messages: List<ChatMessage>,
    onSendMessage: (String, String) -> Unit,
    onDeleteMessage: (String) -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    var selectedTopic by remember { mutableStateOf("Umum") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val quickTopics = listOf("Jadwal", "Jenis Sampah", "Status Setoran", "Poin & Reward", "Umum")
    val quickQuestions = listOf(
        "Kapan jadwal penjemputan sampah berikutnya?",
        "Apakah kardus basah atau kotor bisa disetor?",
        "Bagaimana cara menukar poin TPS 3R?",
        "Dimana lokasi drop-point terdekat di Pomalaa?",
        "Berapa minimal berat untuk penjemputan langsung?"
    )

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Support Banner Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = ForestGreenPrimary,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SupportAgent,
                        contentDescription = "Admin CS",
                        tint = ForestGreenPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Layanan Tanya Jawab TPS 3R",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Tim Lingkungan PT VALE IGP Pomalaa • Online",
                        fontSize = 12.sp,
                        color = LightMint
                    )
                }
                Surface(
                    color = EmeraldGreen,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Aktif",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }

        // Quick Question Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0FDF4))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickQuestions) { question ->
                Surface(
                    modifier = Modifier.clickable {
                        onSendMessage(question, "Tanya Cepat")
                    },
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldGreen)
                ) {
                    Text(
                        text = question,
                        fontSize = 11.sp,
                        color = ForestGreenPrimary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            if (messages.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChatBubbleOutline,
                                contentDescription = "Empty Chat",
                                tint = Color.Gray,
                                modifier = Modifier.size(54.dp)
                            )
                            Text(
                                text = "Belum ada pesan.",
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Text(
                                text = "Ketik pertanyaan atau klik salah satu topik cepat di atas untuk konsultasi dengan Admin TPS 3R.",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )
                        }
                    }
                }
            }

            items(messages) { msg ->
                val isMe = msg.senderId == currentUser.id
                ChatBubbleItem(
                    message = msg,
                    isMe = isMe,
                    onDelete = { onDeleteMessage(msg.id) }
                )
            }
        }

        // Topic selector row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(quickTopics) { topic ->
                val isSelected = selectedTopic == topic
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedTopic = topic },
                    label = { Text(topic, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ForestGreenPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // Input Field
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Tulis pesan atau pertanyaan...", fontSize = 13.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreenPrimary,
                        unfocusedBorderColor = Color.LightGray
                    )
                )

                FloatingActionButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            onSendMessage(messageText, selectedTopic)
                            messageText = ""
                            coroutineScope.launch {
                                if (messages.isNotEmpty()) {
                                    listState.animateScrollToItem(messages.size)
                                }
                            }
                        }
                    },
                    containerColor = ForestGreenPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Kirim Pesan",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * Admin Inbox Screen showing list of depositors and recent messages
 */
@Composable
fun AdminChatInboxScreen(
    currentUser: User,
    users: List<User>,
    messages: List<ChatMessage>,
    selectedTab: Int,
    onTabChange: (Int) -> Unit,
    onSelectUser: (User) -> Unit,
    onSendBroadcast: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var broadcastText by remember { mutableStateOf("") }
    var showBroadcastDialog by remember { mutableStateOf(false) }

    val filteredUsers = users.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
        it.department.contains(searchQuery, ignoreCase = true) ||
        it.username.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Admin Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = ForestGreenPrimary,
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Pusat Pesan & Diskusi Penyetor",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "Admin TPS 3R • PT VALE IGP Pomalaa",
                            fontSize = 12.sp,
                            color = LightMint
                        )
                    }
                    Button(
                        onClick = { showBroadcastDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = EcoAmber),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = "Broadcast",
                            modifier = Modifier.size(16.dp),
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Broadcast", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari nama penyetor atau departemen...", fontSize = 12.sp, color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = LightMint,
                        unfocusedBorderColor = Color(0x66FFFFFF),
                        cursorColor = Color.White
                    ),
                    singleLine = true
                )
            }
        }

        // Depositor Conversation List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daftar Kontak Penyetor (${filteredUsers.size})",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Klik untuk membuka chat",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }

            if (filteredUsers.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Tidak ada penyetor yang sesuai.", color = Color.Gray)
                        }
                    }
                }
            }

            items(filteredUsers) { user ->
                val userMessages = messages.filter {
                    (it.senderId == user.id && it.receiverId == "ADMIN_TPS3R") ||
                    (it.senderId == currentUser.id && it.receiverId == user.id)
                }
                val lastMessage = userMessages.maxByOrNull { it.timestamp }
                val unreadCount = userMessages.count { it.senderId == user.id && !it.isRead }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectUser(user) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // User Avatar
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(ForestGreenPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user.name.take(1).uppercase(),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        // Info & Last Message
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = user.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (lastMessage != null) {
                                    Text(
                                        text = lastMessage.timeStr,
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }
                            }

                            Text(
                                text = user.department,
                                fontSize = 11.sp,
                                color = EmeraldGreen,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = lastMessage?.message ?: "Belum ada riwayat pesan. Klik untuk mulai chat.",
                                fontSize = 12.sp,
                                color = if (unreadCount > 0) MaterialTheme.colorScheme.onSurface else Color.Gray,
                                fontWeight = if (unreadCount > 0) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Unread Badge
                        if (unreadCount > 0) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(EcoAmber),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$unreadCount",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Open Chat",
                                tint = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }

    // Broadcast Dialog
    if (showBroadcastDialog) {
        AlertDialog(
            onDismissRequest = { showBroadcastDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Campaign, contentDescription = "Broadcast", tint = ForestGreenPrimary)
                    Text("Kirim Pesan Broadcast", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Pesan ini akan dikirimkan ke seluruh penyetor di sistem LAIKA SAMPAH PT VALE IGP Pomalaa.",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    OutlinedTextField(
                        value = broadcastText,
                        onValueChange = { broadcastText = it },
                        label = { Text("Isi Pesan Broadcast") },
                        placeholder = { Text("Contoh: Mohon perhatian, besok operasional TPS 3R buka lebih awal pukul 07.00 WITA.") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        maxLines = 5,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (broadcastText.isNotBlank()) {
                            onSendBroadcast(broadcastText)
                            broadcastText = ""
                            showBroadcastDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
                ) {
                    Text("Kirim Sekarang")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBroadcastDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

/**
 * Admin view when chatting with a specific depositor
 */
@Composable
fun AdminUserChatView(
    currentUser: User,
    targetUser: User,
    allMessages: List<ChatMessage>,
    onBack: () -> Unit,
    onSendMessage: (messageText: String, receiverId: String, receiverName: String, quickTopic: String) -> Unit,
    onDeleteMessage: (String) -> Unit
) {
    var replyText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val userMessages = allMessages.filter {
        (it.senderId == targetUser.id && (it.receiverId == "ADMIN_TPS3R" || it.receiverId == currentUser.id)) ||
        (it.senderId == currentUser.id && it.receiverId == targetUser.id)
    }

    val quickAdminReplies = listOf(
        "Siap, tim pick-up TPS 3R segera menuju lokasi.",
        "Terima kasih, sampah terpilah Anda telah kami catat.",
        "Kardus dan plastik bersih dapat disetor setiap hari kerja.",
        "Poin setoran otomatis terakumulasi setelah diverifikasi.",
        "Mohon pastikan sampah tidak bercampur dengan limbah B3/oli."
    )

    LaunchedEffect(userMessages.size) {
        if (userMessages.isNotEmpty()) {
            listState.animateScrollToItem(userMessages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Admin Chat Header with User details
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = ForestGreenPrimary,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back to Inbox",
                        tint = Color.White
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = targetUser.name.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = ForestGreenPrimary,
                        fontSize = 16.sp
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = targetUser.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${targetUser.department} • ${targetUser.phone}",
                        fontSize = 11.sp,
                        color = LightMint
                    )
                }
            }
        }

        // Quick Admin Reply Presets
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0FDF4))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(quickAdminReplies) { quickText ->
                Surface(
                    modifier = Modifier.clickable {
                        onSendMessage(quickText, targetUser.id, targetUser.name, "Jawaban Admin")
                    },
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldGreen)
                ) {
                    Text(
                        text = quickText,
                        fontSize = 11.sp,
                        color = ForestGreenPrimary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        // Messages list
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            if (userMessages.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Belum ada pesan dalam percakapan ini.",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            items(userMessages) { msg ->
                val isMe = msg.senderId == currentUser.id
                ChatBubbleItem(
                    message = msg,
                    isMe = isMe,
                    onDelete = { onDeleteMessage(msg.id) }
                )
            }
        }

        // Input Field
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = replyText,
                    onValueChange = { replyText = it },
                    placeholder = { Text("Balas ke ${targetUser.name}...", fontSize = 13.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreenPrimary,
                        unfocusedBorderColor = Color.LightGray
                    )
                )

                FloatingActionButton(
                    onClick = {
                        if (replyText.isNotBlank()) {
                            onSendMessage(replyText, targetUser.id, targetUser.name, "Jawaban Admin")
                            replyText = ""
                            coroutineScope.launch {
                                if (userMessages.isNotEmpty()) {
                                    listState.animateScrollToItem(userMessages.size)
                                }
                            }
                        }
                    },
                    containerColor = ForestGreenPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Kirim",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * Reusable Chat Bubble Component
 */
@Composable
fun ChatBubbleItem(
    message: ChatMessage,
    isMe: Boolean,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        if (!isMe) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (message.senderRole == "Admin") ForestGreenPrimary else Color(0xFF0288D1)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (message.senderRole == "Admin") "A" else message.senderName.take(1).uppercase(),
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clickable { showMenu = true },
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMe) 16.dp else 4.dp,
                bottomEnd = if (isMe) 4.dp else 16.dp
            ),
            color = if (isMe) ForestGreenPrimary else Color.White,
            shadowElevation = 1.5.dp,
            border = if (!isMe) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)) else null
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Header (Topic & Sender if not me)
                if (!isMe) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = message.senderName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreenPrimary
                        )
                        if (message.quickTopic.isNotBlank()) {
                            Surface(
                                color = LightMint,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = message.quickTopic,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreenPrimary,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                }

                // Message Text
                Text(
                    text = message.message,
                    fontSize = 13.sp,
                    color = if (isMe) Color.White else Color(0xFF1E293B),
                    lineHeight = 18.sp
                )

                // Timestamp & Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message.timeStr,
                        fontSize = 10.sp,
                        color = if (isMe) LightMint else Color.Gray
                    )
                    if (isMe) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "Delivered",
                            tint = LightMint,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Hapus Pesan", color = Color.Red, fontSize = 12.sp) },
                    onClick = {
                        onDelete()
                        showMenu = false
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus",
                            tint = Color.Red,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }
        }
    }
}
