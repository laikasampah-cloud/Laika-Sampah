package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdminDashboardScreen(
    currentUser: User,
    users: List<User>,
    deposits: List<WasteDeposit>,
    onVerifikasiClick: () -> Unit,
    onPenggunaClick: () -> Unit,
    onLaporanClick: () -> Unit,
    onDetailDeposit: (WasteDeposit) -> Unit,
    onChatClick: () -> Unit = {},
    onCampaignClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    val totalUsers = users.size
    val activeUsers = users.count { it.status == "Aktif" }
    val totalTransactions = deposits.size
    val pendingVerifications = deposits.count { it.status == "Pending" }

    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    val thisMonthPrefix = remember { SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date()) }

    val todayWeight = deposits.filter { it.date == todayStr && it.status != "Ditolak" }.sumOf { it.weight }
    val monthWeight = deposits.filter { it.date.startsWith(thisMonthPrefix) && it.status != "Ditolak" }.sumOf { it.weight }

    // Chart Data
    val dailyChartData = remember(deposits) {
        listOf(
            Pair("Sen", 45.0),
            Pair("Sel", 62.5),
            Pair("Rab", 88.0),
            Pair("Kam", 54.0),
            Pair("Jum", 76.5),
            Pair("Sab", 95.0),
            Pair("Min", 32.0)
        )
    }

    val typeBreakdown = remember(deposits) {
        val valid = deposits.filter { it.status != "Ditolak" }
        val g = valid.groupBy { it.wasteType }
        listOf(
            Triple("Organik", g["Organik"]?.sumOf { it.weight } ?: 70.0, EcoLeaf),
            Triple("HDPE", g["HDPE"]?.sumOf { it.weight } ?: 40.5, EmeraldGreen),
            Triple("Kardus", g["Kardus"]?.sumOf { it.weight } ?: 87.7, EcoAmber),
            Triple("PET", g["PET"]?.sumOf { it.weight } ?: 19.4, EcoTeal),
            Triple("Kertas", g["Kertas"]?.sumOf { it.weight } ?: 12.5, Color(0xFF78909C)),
            Triple("Residu", g["Residu"]?.sumOf { it.weight } ?: 22.5, EcoRed)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Official Corporate Branding (Enlarged Dashboard Logos)
        DashboardBrandHeroBanner()

        // Admin Hero Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ForestGreenPrimary),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("PANEL KONTROL ADMIN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LightMint)
                            Text("TPS 3R PT VALE IGP POMALAA", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
                            Text("Administrator: ${currentUser.name}", fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f))
                        }
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👑", fontSize = 22.sp)
                        }
                    }

                    if (pendingVerifications > 0) {
                        Surface(
                            color = EcoOrange,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onVerifikasiClick() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.PendingActions, contentDescription = null, tint = Color.White)
                                    Text(
                                        text = "$pendingVerifications Setoran Menunggu Verifikasi",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                                Text("Verifikasi Sekarang →", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 6 Stat Cards Grid
        Text(
            text = "Indikator Utama Operasional TPS 3R",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "Total Pengguna",
                value = "$totalUsers User",
                subtitle = "$activeUsers Aktif",
                icon = Icons.Default.People,
                accentColor = EcoTeal,
                modifier = Modifier.weight(1f),
                onClick = onPenggunaClick
            )
            StatCard(
                title = "Pending Verifikasi",
                value = "$pendingVerifications",
                subtitle = "Antrean aktif",
                icon = Icons.Default.FactCheck,
                accentColor = EcoOrange,
                modifier = Modifier.weight(1f),
                onClick = onVerifikasiClick
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "Sampah Hari Ini",
                value = "${String.format(Locale.US, "%.1f", todayWeight)} Kg",
                subtitle = "Tercatat di sistem",
                icon = Icons.Default.Today,
                accentColor = EmeraldGreen,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Sampah Bulan Ini",
                value = "${String.format(Locale.US, "%.1f", monthWeight)} Kg",
                subtitle = "Agustus 2026",
                icon = Icons.Default.CalendarMonth,
                accentColor = EcoLeaf,
                modifier = Modifier.weight(1f),
                onClick = onLaporanClick
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "Total Transaksi",
                value = "$totalTransactions",
                subtitle = "Penyetoran domestik",
                icon = Icons.Default.ReceiptLong,
                accentColor = Color(0xFF673AB7),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Pengguna Aktif",
                value = "$activeUsers",
                subtitle = "Penyetor terdaftar",
                icon = Icons.Default.CheckCircle,
                accentColor = EcoOlive,
                modifier = Modifier.weight(1f)
            )
        }

        // Quick Communication & Campaign Access Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onChatClick() }
                    .testTag("admin_card_quick_chat"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MintContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Forum,
                            contentDescription = "Pesan Penyetor",
                            tint = ForestGreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Pesan Penyetor",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Tanya Jawab & Broadcast",
                            fontSize = 10.sp,
                            color = EmeraldGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onCampaignClick() }
                    .testTag("admin_card_quick_campaign"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFF3CD)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = "Campaign Lingkungan",
                            tint = Color(0xFFB78103),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Info Campaign",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Kelola Kegiatan Vale",
                            fontSize = 10.sp,
                            color = Color(0xFFB78103),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Charts
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Sampah Masuk Per Hari (Kg)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                SimpleBarChart(data = dailyChartData)
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Komposisi Sampah Masuk TPS 3R Berdasarkan Jenis",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                WastePieDonutChart(breakdown = typeBreakdown)
            }
        }

        // Pending Queue Preview
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Antrean Verifikasi Terbaru",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(onClick = onVerifikasiClick) {
                        Text("Kelola Semua", fontSize = 12.sp, color = EmeraldGreen)
                    }
                }

                val pendingList = deposits.filter { it.status == "Pending" }
                if (pendingList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✅ Semua setoran telah diverifikasi.", fontSize = 12.sp, color = TextSecondary)
                    }
                } else {
                    pendingList.take(3).forEach { item ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onDetailDeposit(item) },
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${item.userName} • ${item.userDepartment}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "${item.wasteType} (${item.weight} Kg) • ${item.location}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                StatusBadge(status = item.status)
                            }
                        }
                    }
                }
            }
        }

        LaikaFooter()
    }
}

@Composable
fun DataPenggunaScreen(
    users: List<User>,
    onSaveUser: (User, Boolean) -> Unit,
    onToggleStatus: (User) -> Unit,
    onDeleteUser: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedRoleFilter by remember { mutableStateOf("Semua") }
    var showUserDialog by remember { mutableStateOf(false) }
    var editingUser by remember { mutableStateOf<User?>(null) }

    val filteredUsers = remember(users, searchQuery, selectedRoleFilter) {
        users.filter { user ->
            val matchRole = selectedRoleFilter == "Semua" || user.role.equals(selectedRoleFilter, ignoreCase = true)
            val matchSearch = searchQuery.isBlank() ||
                    user.name.contains(searchQuery, ignoreCase = true) ||
                    user.username.contains(searchQuery, ignoreCase = true) ||
                    user.department.contains(searchQuery, ignoreCase = true) ||
                    user.phone.contains(searchQuery, ignoreCase = true)
            matchRole && matchSearch
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "DATA PENGGUNA SISTEM",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Manajemen akun penyetor dan administrator TPS 3R",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = {
                    editingUser = null
                    showUserDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Tambah User", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Search
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Cari nama, username, unit kerja...", fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().testTag("search_users_input"),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        // Filter Role
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Semua", "Admin", "Penyetor").forEach { role ->
                val isSelected = selectedRoleFilter == role
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedRoleFilter = role },
                    label = { Text(role, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ForestGreenPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // Users List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredUsers, key = { it.id }) { user ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
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
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (user.role == "Admin") EcoOrange else EmeraldGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user.name.take(1).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = user.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Surface(
                                    color = if (user.role == "Admin") EcoOrange.copy(alpha = 0.2f) else LightMint,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = user.role,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (user.role == "Admin") EcoOrange else ForestGreenPrimary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "@${user.username} • ${user.phone}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${user.department} • Reg: ${user.createdAt}",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Surface(
                                color = if (user.status == "Aktif") LightMint else Color(0xFFF8D7DA),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = user.status,
                                    color = if (user.status == "Aktif") ForestGreenPrimary else EcoRed,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                IconButton(
                                    onClick = {
                                        editingUser = user
                                        showUserDialog = true
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = EcoAmber, modifier = Modifier.size(16.dp))
                                }
                                IconButton(
                                    onClick = { onToggleStatus(user) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = if (user.status == "Aktif") Icons.Default.Block else Icons.Default.CheckCircle,
                                        contentDescription = "Toggle Status",
                                        tint = if (user.status == "Aktif") EcoOrange else EmeraldGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { onDeleteUser(user.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = EcoRed, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showUserDialog) {
        UserFormDialog(
            user = editingUser,
            onDismiss = { showUserDialog = false },
            onSave = { savedUser, isNew ->
                onSaveUser(savedUser, isNew)
                showUserDialog = false
            }
        )
    }
}

@Composable
fun UserFormDialog(
    user: User?,
    onDismiss: () -> Unit,
    onSave: (User, Boolean) -> Unit
) {
    var name by remember { mutableStateOf(user?.name ?: "") }
    var username by remember { mutableStateOf(user?.username ?: "") }
    var phone by remember { mutableStateOf(user?.phone ?: "") }
    var password by remember { mutableStateOf(user?.password ?: "user123") }
    var role by remember { mutableStateOf(user?.role ?: "Penyetor") }
    var department by remember { mutableStateOf(user?.department ?: "Mining Operations IGP") }
    var address by remember { mutableStateOf(user?.address ?: "Mess IGP Pomalaa") }

    val isNew = user == null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isNew) "Tambah Pengguna Baru" else "Edit Pengguna", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Lengkap *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Nomor HP *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = department,
                    onValueChange = { department = it },
                    label = { Text("Unit / Departemen") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Alamat") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Penyetor", "Admin").forEach { r ->
                        val isSel = role == r
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { role = r },
                            color = if (isSel) ForestGreenPrimary else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = r,
                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(8.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && username.isNotBlank()) {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val u = User(
                            id = user?.id ?: "user_${UUID.randomUUID().toString().take(8)}",
                            name = name.trim(),
                            username = username.trim(),
                            phone = phone.trim(),
                            password = password.trim(),
                            role = role,
                            department = department.trim(),
                            address = address.trim(),
                            status = user?.status ?: "Aktif",
                            createdAt = user?.createdAt ?: dateFormat.format(Date())
                        )
                        onSave(u, isNew)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
            ) {
                Text("Simpan", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

@Composable
fun VerifikasiSetoranScreen(
    deposits: List<WasteDeposit>,
    onVerify: (String) -> Unit,
    onReject: (String, String) -> Unit,
    onDetail: (WasteDeposit) -> Unit
) {
    var filterPendingOnly by remember { mutableStateOf(true) }
    var showRejectDialog by remember { mutableStateOf(false) }
    var rejectingDepositId by remember { mutableStateOf<String?>(null) }
    var rejectionReason by remember { mutableStateOf("") }

    val filteredList = if (filterPendingOnly) deposits.filter { it.status == "Pending" } else deposits

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column {
            Text(
                text = "VERIFIKASI SETORAN SAMPAH",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Tinjau dan validasi setoran fisik yang masuk ke TPS 3R",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = filterPendingOnly,
                onClick = { filterPendingOnly = true },
                label = { Text("Antrean Pending (${deposits.count { it.status == "Pending" }})", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = EcoOrange,
                    selectedLabelColor = Color.White
                )
            )

            FilterChip(
                selected = !filterPendingOnly,
                onClick = { filterPendingOnly = false },
                label = { Text("Semua Status (${deposits.size})", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ForestGreenPrimary,
                    selectedLabelColor = Color.White
                )
            )
        }

        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(48.dp))
                    Text(
                        text = "Tidak ada setoran yang menunggu verifikasi saat ini.",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredList, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = item.userName,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp
                                    )
                                    StatusBadge(status = item.status)
                                }

                                Text(
                                    text = "${item.weight} Kg",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Text(
                                text = "Unit: ${item.userDepartment} • Titik: ${item.location}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Jenis: ${item.wasteType}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = EmeraldGreen
                                )
                                Text(
                                    text = "${item.date} • ${item.time}",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }

                            if (item.notes.isNotBlank()) {
                                Text(
                                    text = "Catatan: \"${item.notes}\"",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }

                            if (item.status == "Pending") {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { onVerify(item.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("VERIFIKASI", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            rejectingDepositId = item.id
                                            rejectionReason = ""
                                            showRejectDialog = true
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = EcoRed),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("TOLAK", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }

                                    IconButton(onClick = { onDetail(item) }) {
                                        Icon(Icons.Default.Visibility, contentDescription = "Detail", tint = TextSecondary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showRejectDialog) {
        AlertDialog(
            onDismissRequest = { showRejectDialog = false },
            title = { Text("Tolak Setoran Sampah", fontWeight = FontWeight.Bold, color = EcoRed) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Masukkan alasan penolakan untuk dikirim ke penyetor:")
                    OutlinedTextField(
                        value = rejectionReason,
                        onValueChange = { rejectionReason = it },
                        placeholder = { Text("Contoh: Tercampur limbah B3 / Bukan sampah terpilah...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val depId = rejectingDepositId
                        if (depId != null && rejectionReason.isNotBlank()) {
                            onReject(depId, rejectionReason.trim())
                            showRejectDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EcoRed)
                ) {
                    Text("Kirim Penolakan", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRejectDialog = false }) { Text("Batal") }
            }
        )
    }
}
