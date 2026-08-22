package com.example.ui.screens.campaign

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Campaign
import com.example.data.model.User
import com.example.ui.components.DashboardBrandHeroBanner
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaignScreen(
    currentUser: User,
    campaigns: List<Campaign>,
    onJoinCampaign: (String) -> Unit,
    onSaveCampaign: (
        id: String?,
        title: String,
        category: String,
        organizer: String,
        startDate: String,
        endDate: String,
        time: String,
        location: String,
        description: String,
        targetParticipants: Int,
        rewardPoints: Int,
        status: String,
        contact: String
    ) -> Unit,
    onDeleteCampaign: (String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("Semua") }
    var selectedStatus by remember { mutableStateOf("Semua") }
    var searchQuery by remember { mutableStateOf("") }
    var showAddEditDialog by remember { mutableStateOf(false) }
    var campaignToEdit by remember { mutableStateOf<Campaign?>(null) }
    var selectedCampaignDetail by remember { mutableStateOf<Campaign?>(null) }

    val categories = listOf("Semua", "Edukasi Pemilahan", "Pelatihan TPS 3R", "Aksi Bersih", "Lomba & Reward", "Penghijauan")
    val statuses = listOf("Semua", "Sedang Berlangsung", "Akan Datang", "Selesai")

    val filteredCampaigns = campaigns.filter { camp ->
        (selectedCategory == "Semua" || camp.category == selectedCategory) &&
        (selectedStatus == "Semua" || camp.status == selectedStatus) &&
        (searchQuery.isBlank() || camp.title.contains(searchQuery, ignoreCase = true) || camp.location.contains(searchQuery, ignoreCase = true))
    }

    Scaffold(
        floatingActionButton = {
            if (currentUser.role == "Admin") {
                ExtendedFloatingActionButton(
                    onClick = {
                        campaignToEdit = null
                        showAddEditDialog = true
                    },
                    containerColor = ForestGreenPrimary,
                    contentColor = Color.White,
                    icon = { Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Campaign") },
                    text = { Text("Tambah Kegiatan", fontWeight = FontWeight.Bold) }
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Brand Banner
            item {
                DashboardBrandHeroBanner()
            }

            // Screen Title Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ForestGreenPrimary)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Campaign & Kegiatan Lingkungan",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Surface(
                                color = EcoAmber,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "PT VALE IGP",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Ikuti gerakan peduli lingkungan hidup, kumpulkan poin reward kebersihan, dan wujudkan operasional tambang ramah lingkungan di Pomalaa.",
                            fontSize = 12.sp,
                            color = LightMint,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari kegiatan, tema, atau lokasi...", fontSize = 13.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = ForestGreenPrimary)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreenPrimary,
                        unfocusedBorderColor = Color.LightGray
                    ),
                    singleLine = true
                )
            }

            // Category Chips
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Kategori Kegiatan",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(categories) { cat ->
                            val isSelected = selectedCategory == cat
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ForestGreenPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Status Filter Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daftar Kegiatan (${filteredCampaigns.size})",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(statuses) { st ->
                            val isSelected = selectedStatus == st
                            Surface(
                                modifier = Modifier.clickable { selectedStatus = st },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) EmeraldGreen else Color.Transparent,
                                border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray) else null
                            ) {
                                Text(
                                    text = st,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else Color.Gray,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (filteredCampaigns.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EventBusy,
                                    contentDescription = "Empty",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(48.dp)
                                )
                                Text("Tidak ada kegiatan ditemukan.", fontWeight = FontWeight.Bold, color = Color.Gray)
                            }
                        }
                    }
                }
            }

            // Campaign Cards List
            items(filteredCampaigns) { campaign ->
                val hasJoined = campaign.registeredUserIds.split(",").contains(currentUser.id)

                CampaignCardItem(
                    campaign = campaign,
                    hasJoined = hasJoined,
                    isAdmin = currentUser.role == "Admin",
                    onJoinClick = { onJoinCampaign(campaign.id) },
                    onDetailClick = { selectedCampaignDetail = campaign },
                    onEditClick = {
                        campaignToEdit = campaign
                        showAddEditDialog = true
                    },
                    onDeleteClick = { onDeleteCampaign(campaign.id) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }

    // Detail Dialog
    if (selectedCampaignDetail != null) {
        val camp = selectedCampaignDetail!!
        val hasJoined = camp.registeredUserIds.split(",").contains(currentUser.id)

        AlertDialog(
            onDismissRequest = { selectedCampaignDetail = null },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Eco, contentDescription = "Eco", tint = ForestGreenPrimary)
                    Text(camp.title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            color = LightMint,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = camp.category,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreenPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Surface(
                            color = if (camp.status == "Sedang Berlangsung") EmeraldGreen else Color(0xFF0288D1),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = camp.status,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Text(
                        text = "Penyelenggara: ${camp.organizer}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF007E8A)
                    )

                    HorizontalDivider()

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(imageVector = Icons.Default.CalendarToday, contentDescription = "Date", tint = ForestGreenPrimary, modifier = Modifier.size(16.dp))
                        Text("${camp.startDate} s/d ${camp.endDate} (${camp.time})", fontSize = 12.sp)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Loc", tint = Color.Red, modifier = Modifier.size(16.dp))
                        Text(camp.location, fontSize = 12.sp)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(imageVector = Icons.Default.Stars, contentDescription = "Points", tint = EcoAmber, modifier = Modifier.size(16.dp))
                        Text("Reward Poin Partisipasi: +${camp.rewardPoints} Poin", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    HorizontalDivider()

                    Text("Deskripsi Kegiatan:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(camp.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 17.sp)

                    HorizontalDivider()

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(imageVector = Icons.Default.Phone, contentDescription = "Contact", tint = ForestGreenPrimary, modifier = Modifier.size(16.dp))
                        Text("Hotline / CP: ${camp.contactPerson}", fontSize = 11.sp, color = Color.Gray)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onJoinCampaign(camp.id)
                        selectedCampaignDetail = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (hasJoined) Color.Red else ForestGreenPrimary
                    )
                ) {
                    Text(if (hasJoined) "Batalkan Pendaftaran" else "Daftar / Ikuti Sekarang")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedCampaignDetail = null }) {
                    Text("Tutup")
                }
            }
        )
    }

    // Add / Edit Dialog for Admin
    if (showAddEditDialog) {
        var title by remember { mutableStateOf(campaignToEdit?.title ?: "") }
        var category by remember { mutableStateOf(campaignToEdit?.category ?: "Edukasi Pemilahan") }
        var organizer by remember { mutableStateOf(campaignToEdit?.organizer ?: "PT VALE IGP Pomalaa • TPS 3R") }
        var startDate by remember { mutableStateOf(campaignToEdit?.startDate ?: "2026-08-25") }
        var endDate by remember { mutableStateOf(campaignToEdit?.endDate ?: "2026-08-31") }
        var time by remember { mutableStateOf(campaignToEdit?.time ?: "08:00 - 16:00 WITA") }
        var location by remember { mutableStateOf(campaignToEdit?.location ?: "TPS 3R PT VALE Pomalaa") }
        var description by remember { mutableStateOf(campaignToEdit?.description ?: "") }
        var targetParticipants by remember { mutableStateOf(campaignToEdit?.targetParticipants?.toString() ?: "100") }
        var rewardPoints by remember { mutableStateOf(campaignToEdit?.rewardPoints?.toString() ?: "100") }
        var status by remember { mutableStateOf(campaignToEdit?.status ?: "Sedang Berlangsung") }
        var contact by remember { mutableStateOf(campaignToEdit?.contactPerson ?: "Tim Lingkungan Vale (0812-3456-7890)") }

        AlertDialog(
            onDismissRequest = { showAddEditDialog = false },
            title = {
                Text(
                    text = if (campaignToEdit == null) "Tambah Kegiatan Baru" else "Edit Data Kegiatan",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 450.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Nama Kegiatan / Campaign") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = category,
                            onValueChange = { category = it },
                            label = { Text("Kategori (Edukasi / Maggot / Aksi Bersih / Lomba)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = organizer,
                            onValueChange = { organizer = it },
                            label = { Text("Penyelenggara") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = startDate,
                                onValueChange = { startDate = it },
                                label = { Text("Mulai (YYYY-MM-DD)") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = endDate,
                                onValueChange = { endDate = it },
                                label = { Text("Selesai") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = time,
                            onValueChange = { time = it },
                            label = { Text("Waktu Pelaksanaan") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text("Lokasi") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Deskripsi & Tujuan") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp),
                            maxLines = 4
                        )
                    }
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = targetParticipants,
                                onValueChange = { targetParticipants = it },
                                label = { Text("Target Peserta") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = rewardPoints,
                                onValueChange = { rewardPoints = it },
                                label = { Text("Poin Reward") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = status,
                            onValueChange = { status = it },
                            label = { Text("Status (Sedang Berlangsung / Akan Datang / Selesai)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = contact,
                            onValueChange = { contact = it },
                            label = { Text("Kontak Person / Hotline") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onSaveCampaign(
                                campaignToEdit?.id,
                                title,
                                category,
                                organizer,
                                startDate,
                                endDate,
                                time,
                                location,
                                description,
                                targetParticipants.toIntOrNull() ?: 100,
                                rewardPoints.toIntOrNull() ?: 100,
                                status,
                                contact
                            )
                            showAddEditDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddEditDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

/**
 * Reusable Campaign Card Item
 */
@Composable
fun CampaignCardItem(
    campaign: Campaign,
    hasJoined: Boolean,
    isAdmin: Boolean,
    onJoinClick: () -> Unit,
    onDetailClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDetailClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Category & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = LightMint,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = campaign.category,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreenPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Surface(
                    color = when (campaign.status) {
                        "Sedang Berlangsung" -> EmeraldGreen
                        "Akan Datang" -> Color(0xFF0288D1)
                        else -> Color.Gray
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = campaign.status,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // Title
            Text(
                text = campaign.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )

            // Organizer & Details
            Text(
                text = campaign.organizer,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF007E8A)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Date",
                        tint = ForestGreenPrimary,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = campaign.startDate,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = Color.Red,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = campaign.location,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Progress Bar of Participants
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                val progress = if (campaign.targetParticipants > 0) {
                    (campaign.registeredCount.toFloat() / campaign.targetParticipants.toFloat()).coerceIn(0f, 1f)
                } else 0f

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Partisipasi Peserta: ${campaign.registeredCount}/${campaign.targetParticipants}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ForestGreenPrimary
                    )
                    Text(
                        text = "+${campaign.rewardPoints} Poin",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = EcoAmber
                    )
                }

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = EmeraldGreen,
                    trackColor = Color(0xFFE2E8F0)
                )
            }

            HorizontalDivider(color = Color(0xFFF1F5F9))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onDetailClick,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("Lihat Info Detail", fontSize = 12.sp, color = ForestGreenPrimary, fontWeight = FontWeight.Bold)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (isAdmin) {
                        IconButton(onClick = onEditClick, modifier = Modifier.size(32.dp)) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = ForestGreenPrimary, modifier = Modifier.size(18.dp))
                        }
                        IconButton(onClick = onDeleteClick, modifier = Modifier.size(32.dp)) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(18.dp))
                        }
                    }

                    Button(
                        onClick = onJoinClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (hasJoined) Color(0xFFE2E8F0) else ForestGreenPrimary,
                            contentColor = if (hasJoined) Color.DarkGray else Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        if (hasJoined) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Joined", tint = EmeraldGreen, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Terdaftar", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
                        } else {
                            Icon(imageVector = Icons.Default.GroupAdd, contentDescription = "Join", modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ikuti", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
