package com.example.ui.screens.penyetor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
fun PenyetorDashboardScreen(
    currentUser: User,
    deposits: List<WasteDeposit>,
    announcements: List<Announcement>,
    onSetorClick: () -> Unit,
    onScanQrClick: () -> Unit,
    onRiwayatClick: () -> Unit,
    onStatistikClick: () -> Unit,
    onDetailDeposit: (WasteDeposit) -> Unit,
    onChatClick: () -> Unit = {},
    onCampaignClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val userDeposits = deposits.filter { it.userId == currentUser.id }

    // Statistics
    val totalCount = userDeposits.size
    val totalWeight = userDeposits.filter { it.status != "Ditolak" }.sumOf { it.weight }
    val thisMonthPrefix = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
    val thisMonthWeight = userDeposits
        .filter { it.date.startsWith(thisMonthPrefix) && it.status != "Ditolak" }
        .sumOf { it.weight }
    val lastDepositWeight = userDeposits.firstOrNull()?.weight ?: 0.0

    // Chart breakdown
    val dailyChartData = remember(userDeposits) {
        val days = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")
        days.mapIndexed { idx, day ->
            val simulatedKg = when (idx) {
                0 -> 12.5; 1 -> 8.0; 2 -> 22.0; 3 -> 15.4; 4 -> 18.2; 5 -> 28.0; else -> 5.5
            }
            Pair(day, simulatedKg)
        }
    }

    val typeBreakdown = remember(userDeposits) {
        val groups = userDeposits.groupBy { it.wasteType }
        listOf(
            Triple("HDPE", groups["HDPE"]?.sumOf { it.weight } ?: 35.0, EmeraldGreen),
            Triple("Organik", groups["Organik"]?.sumOf { it.weight } ?: 45.0, EcoLeaf),
            Triple("Kardus", groups["Kardus"]?.sumOf { it.weight } ?: 52.0, EcoAmber),
            Triple("PET", groups["PET"]?.sumOf { it.weight } ?: 18.0, EcoTeal),
            Triple("Lainnya", 25.0, EcoOlive)
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

        // Welcome Hero Card
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
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Selamat Datang,",
                                fontSize = 13.sp,
                                color = LightMint
                            )
                            Text(
                                text = currentUser.name,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "Unit: ${currentUser.department}",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🌱", fontSize = 24.sp)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onSetorClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = ForestGreenPrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("btn_quick_setor")
                        ) {
                            Icon(
                                Icons.Default.AddCircle,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("+ SETOR SAMPAH", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = onScanQrClick,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("btn_quick_scan_qr")
                        ) {
                            Icon(
                                Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("SCAN QR", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // 4 Stat Cards
        Text(
            text = "Ringkasan Statistik Saya",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Total Setoran",
                value = "$totalCount Transaksi",
                subtitle = "Semua riwayat",
                icon = Icons.Default.Inventory2,
                accentColor = EmeraldGreen,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Total Berat Sampah",
                value = "${String.format(Locale.US, "%.2f", totalWeight)} Kg",
                subtitle = "Terkonfirmasi",
                icon = Icons.Default.Scale,
                accentColor = EcoLeaf,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Setoran Bulan Ini",
                value = "${String.format(Locale.US, "%.2f", thisMonthWeight)} Kg",
                subtitle = "Agustus 2026",
                icon = Icons.Default.CalendarMonth,
                accentColor = EcoAmber,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Setoran Terakhir",
                value = "${String.format(Locale.US, "%.2f", lastDepositWeight)} Kg",
                subtitle = userDeposits.firstOrNull()?.wasteType ?: "-",
                icon = Icons.Default.History,
                accentColor = EcoTeal,
                modifier = Modifier.weight(1f)
            )
        }

        // Quick Communication & Campaign Access Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onChatClick() }
                    .testTag("card_quick_chat"),
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
                            contentDescription = "Chat",
                            tint = ForestGreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Chat TPS 3R",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Tanya Admin",
                            fontSize = 11.sp,
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
                    .testTag("card_quick_campaign"),
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
                            contentDescription = "Campaign",
                            tint = Color(0xFFB78103),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Campaign",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Aksi & Reward",
                            fontSize = 11.sp,
                            color = Color(0xFFB78103),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Charts Section
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Grafik Sampah Harian & Mingguan",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(onClick = onStatistikClick) {
                        Text("Detail", fontSize = 12.sp, color = EmeraldGreen)
                    }
                }

                SimpleBarChart(data = dailyChartData)
            }
        }

        // Composition Donut Chart
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
                    text = "Komposisi Sampah Berdasarkan Jenis",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                WastePieDonutChart(breakdown = typeBreakdown)
            }
        }

        // Announcements banner
        if (announcements.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MintContainer)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.Campaign,
                            contentDescription = null,
                            tint = ForestGreenPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Pengumuman TPS 3R Vale",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = ForestGreenPrimary
                        )
                    }
                    val topAnc = announcements.first()
                    Text(
                        text = topAnc.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = topAnc.content,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Riwayat Setoran Terbaru Table / List
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Riwayat Setoran Terbaru",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(onClick = onRiwayatClick) {
                        Text("Lihat Semua", fontSize = 12.sp, color = EmeraldGreen)
                    }
                }

                if (userDeposits.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Belum ada riwayat setoran. Klik + SETOR SAMPAH untuk mulai!",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    userDeposits.take(5).forEach { item ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onDetailDeposit(item) },
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(EmeraldGreen.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("♻️", fontSize = 16.sp)
                                    }

                                    Column {
                                        Text(
                                            text = item.wasteType,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = "${item.date} • ${item.location}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Column(
                                    horizontalAlignment = Alignment.End,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "${item.weight} Kg",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    StatusBadge(status = item.status)
                                }
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
fun SetorSampahScreen(
    currentUser: User,
    wasteTypes: List<WasteType>,
    prefilledLocation: String = "",
    prefilledWasteType: String = "",
    onSubmitDeposit: (wasteType: String, weight: Double, location: String, notes: String, photo: String) -> Unit,
    onCancel: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val now = remember { Date() }

    var dateText by remember { mutableStateOf(dateFormat.format(now)) }
    var timeText by remember { mutableStateOf(timeFormat.format(now)) }
    var userName by remember { mutableStateOf(currentUser.name) }
    var department by remember { mutableStateOf(currentUser.department) }
    var location by remember { mutableStateOf(prefilledLocation.ifBlank { "TPS 3R Pomalaa" }) }
    var selectedWasteType by remember { mutableStateOf(prefilledWasteType.ifBlank { "Organik" }) }
    var weightText by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedPhotoPreset by remember { mutableStateOf("foto_kantong_sampah") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    val locations = listOf(
        "TPS 3R Pomalaa",
        "Area Kantor Utama Vale",
        "Pabrik Smelter Pomalaa",
        "Area Workshop Central",
        "Kantin Utama IGP",
        "Mess IGP Pomalaa",
        "Perumahan Staff Vale",
        "Gudang Logistik & Supply Chain",
        "Mining Operations Pit Area"
    )
    var expandedLocation by remember { mutableStateOf(false) }
    var expandedType by remember { mutableStateOf(false) }

    val activeTypes = wasteTypes.filter { it.isActive }.ifEmpty {
        listOf(
            WasteType("1", "Organik", "ORG", "Bio", "#2E7D32", "Eco"),
            WasteType("2", "HDPE", "HDPE", "Plastic", "#198754", "WaterDrop"),
            WasteType("3", "PET", "PET", "Plastic", "#0D6EFD", "LocalDrink"),
            WasteType("4", "Kardus", "KRD", "Paper", "#B78103", "Inventory2"),
            WasteType("5", "Paper Box", "PBX", "Paper", "#9C640C", "AllInbox"),
            WasteType("6", "Kertas", "KRT", "Paper", "#6C757D", "Description"),
            WasteType("7", "Residu", "RSD", "Other", "#DC3545", "DeleteSweep"),
            WasteType("8", "E-Waste", "EWT", "B3", "#6F42C1", "DevicesOther")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(LightMint),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AddCircle,
                            contentDescription = null,
                            tint = ForestGreenPrimary
                        )
                    }
                    Column {
                        Text(
                            text = "FORMULIR SETOR SAMPAH",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Pencatatan Penyetoran Sampah Terpadu TPS 3R",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                HorizontalDivider()

                if (errorMessage != null) {
                    Surface(
                        color = Color(0xFFF8D7DA),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            color = EcoRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(10.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Date & Time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = dateText,
                        onValueChange = { dateText = it },
                        label = { Text("Tanggal") },
                        leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                        modifier = Modifier.weight(1f).testTag("setor_date_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = timeText,
                        onValueChange = { timeText = it },
                        label = { Text("Jam (WITA)") },
                        leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                        modifier = Modifier.weight(1f).testTag("setor_time_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // Penyetor & Dept
                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = { Text("Nama Penyetor") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().testTag("setor_user_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    readOnly = true
                )

                OutlinedTextField(
                    value = department,
                    onValueChange = { department = it },
                    label = { Text("Unit / Departemen") },
                    leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().testTag("setor_dept_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                // Lokasi Dropdown
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Lokasi Penyetoran *") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = EmeraldGreen) },
                        trailingIcon = {
                            IconButton(onClick = { expandedLocation = !expandedLocation }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("setor_location_input"),
                        shape = RoundedCornerShape(10.dp)
                    )
                    DropdownMenu(
                        expanded = expandedLocation,
                        onDismissRequest = { expandedLocation = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        locations.forEach { loc ->
                            DropdownMenuItem(
                                text = { Text(loc, fontSize = 13.sp) },
                                onClick = {
                                    location = loc
                                    expandedLocation = false
                                }
                            )
                        }
                    }
                }

                // Jenis Sampah Dropdown
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedWasteType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Jenis Sampah *") },
                        leadingIcon = { Icon(Icons.Default.Category, contentDescription = null, tint = EmeraldGreen) },
                        trailingIcon = {
                            IconButton(onClick = { expandedType = !expandedType }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("setor_type_input"),
                        shape = RoundedCornerShape(10.dp)
                    )
                    DropdownMenu(
                        expanded = expandedType,
                        onDismissRequest = { expandedType = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        activeTypes.forEach { wt ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(wt.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("(${wt.category})", fontSize = 11.sp, color = TextSecondary)
                                    }
                                },
                                onClick = {
                                    selectedWasteType = wt.name
                                    expandedType = false
                                }
                            )
                        }
                    }
                }

                // Berat (Kg)
                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it; errorMessage = null },
                    label = { Text("Berat Sampah (Kg) *") },
                    leadingIcon = { Icon(Icons.Default.Scale, contentDescription = null, tint = EmeraldGreen) },
                    trailingIcon = { Text("Kg", fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 12.dp)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().testTag("setor_weight_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                // Foto Sampah (Visual attachment simulation)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Lampiran Foto Sampah Fisik",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("📸 Ambil Foto Kamera", "🖼️ Pilih Galeri", "✅ Lampiran Otomatis").forEachIndexed { idx, label ->
                            val isSelected = selectedPhotoPreset == "photo_$idx"
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedPhotoPreset = "photo_$idx" },
                                color = if (isSelected) LightMint else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, EmeraldGreen) else null
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) ForestGreenPrimary else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(8.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Catatan
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Catatan Tambahan (Opsional)") },
                    leadingIcon = { Icon(Icons.Default.Notes, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().testTag("setor_notes_input"),
                    maxLines = 3,
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Submit button
                Button(
                    onClick = {
                        val weight = weightText.replace(",", ".").toDoubleOrNull()
                        when {
                            selectedWasteType.isBlank() -> errorMessage = "Jenis sampah wajib dipilih."
                            weight == null || weight <= 0.0 -> errorMessage = "Berat sampah harus lebih dari 0 Kg."
                            location.isBlank() -> errorMessage = "Lokasi penyetoran wajib diisi."
                            else -> onSubmitDeposit(selectedWasteType, weight, location, notes, selectedPhotoPreset)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_submit_deposit"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SIMPAN SETORAN (STATUS: PENDING)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }

                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Batal & Kembali", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        LaikaFooter()
    }
}

@Composable
fun ScanQrScreen(
    onQrResultFound: (type: String, data: String) -> Unit,
    onQuickDeposit: (location: String, wasteType: String) -> Unit,
    onImportDepositFromQr: (WasteDeposit) -> Unit = {}
) {
    var manualCodeInput by remember { mutableStateOf("") }
    var scannedResult by remember { mutableStateOf<String?>(null) }
    var scannedCategory by remember { mutableStateOf<String?>(null) }
    var isFlashOn by remember { mutableStateOf(false) }

    val presetQrCodes = listOf(
        Triple("Transfer Setoran: 5.5 Kg HDPE (Ahmad Fauzi)", "LAIKA_DEP|dep_quick_01|user_penyetor_01|Ahmad Fauzi|Mining Operations IGP|2026-08-19|10:30|TPS 3R Pomalaa|HDPE|5.50|Botol jerigen oli bersih", "transfer_deposit"),
        Triple("Kartu Nasabah: Siti Nurhaliza (HR)", "LAIKA_USER|user_penyetor_02|Siti Nurhaliza|siti_pomalaa|HR & General Affairs|085211223344", "user_card"),
        Triple("Lokasi: TPS 3R Pomalaa", "TPS3R-VALE-01", "lokasi"),
        Triple("Lokasi: Kantin Smelter", "KANTIN-SMELTER-02", "lokasi"),
        Triple("Jenis: HDPE Terpilah", "WASTE-HDPE-01", "sampah"),
        Triple("Jenis: Organik Bioreaktor", "WASTE-ORG-01", "sampah")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "PEMINDAI KODE QR ANTAR HP",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Arahkan kamera ke layar HP lain untuk Scan Transfer Setoran, Kartu Nasabah, Lokasi TPS 3R, atau Jenis Sampah",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                // Interactive Viewfinder
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1E2421)),
                    contentAlignment = Alignment.Center
                ) {
                    // QR Box Outline
                    Box(
                        modifier = Modifier
                            .size(170.dp)
                            .border(2.dp, if (scannedResult != null) EmeraldGreen else LightMint, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                tint = if (scannedResult != null) EmeraldGreen else Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = if (scannedResult != null) "QR Berhasil Dibaca!" else "Memindai Layar HP / QR...",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Flash Toggle
                    IconButton(
                        onClick = { isFlashOn = !isFlashOn },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                            contentDescription = "Flash",
                            tint = if (isFlashOn) EcoAmber else Color.White
                        )
                    }
                }

                // Scanned Result Card
                if (scannedResult != null) {
                    val rawData = scannedResult!!
                    val isP2PDeposit = rawData.startsWith("LAIKA_DEP|")
                    val isUserCard = rawData.startsWith("LAIKA_USER|")

                    Surface(
                        color = LightMint,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ForestGreenPrimary)
                                Text(
                                    text = if (isP2PDeposit) "QR Transfer Setoran Terbaca!"
                                    else if (isUserCard) "QR Kartu Nasabah Terbaca!"
                                    else "Data Terbaca: $scannedResult",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = ForestGreenPrimary
                                )
                            }

                            if (isP2PDeposit) {
                                val parsedDep = com.example.utils.DataTransferHelper.decodeDepositFromQr(rawData)
                                if (parsedDep != null) {
                                    Surface(
                                        color = Color.White,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                            Text("Penyetor: ${parsedDep.userName} (${parsedDep.userDepartment})", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text("Sampah: ${parsedDep.wasteType} • Berat: ${parsedDep.weight} Kg", fontSize = 12.sp, color = ForestGreenPrimary, fontWeight = FontWeight.Bold)
                                            Text("Lokasi: ${parsedDep.location} • Waktu: ${parsedDep.date} ${parsedDep.time}", fontSize = 11.sp, color = TextSecondary)
                                            if (parsedDep.notes.isNotBlank()) {
                                                Text("Catatan: ${parsedDep.notes}", fontSize = 11.sp, color = TextSecondary)
                                            }
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            onImportDepositFromQr(parsedDep)
                                            scannedResult = null
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("SIMPAN SETORAN DARI HP INI KE DATABASE", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            } else if (isUserCard) {
                                val userCardData = com.example.utils.DataTransferHelper.decodeUserCardFromQr(rawData)
                                if (userCardData != null) {
                                    Surface(
                                        color = Color.White,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                            Text("Nasabah: ${userCardData["name"]}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text("Unit / Dept: ${userCardData["department"]}", fontSize = 11.sp, color = TextSecondary)
                                            Text("No. HP: ${userCardData["phone"]}", fontSize = 11.sp, color = TextSecondary)
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            onQuickDeposit("TPS 3R Pomalaa", "Organik")
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("BUAT SETORAN UNTUK NASABAH INI", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            } else {
                                Text(
                                    text = "Kategori: ${scannedCategory?.uppercase() ?: "KODE"}",
                                    fontSize = 11.sp,
                                    color = TextPrimary
                                )

                                Button(
                                    onClick = {
                                        val loc = if (scannedCategory == "lokasi") scannedResult ?: "" else "TPS 3R Pomalaa"
                                        val waste = if (scannedCategory == "sampah") "HDPE" else "Organik"
                                        onQuickDeposit(loc, waste)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Lanjutkan Setor Sampah di Titik Ini", fontSize = 12.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }

                HorizontalDivider()

                // Manual Input Fallback
                Text(
                    text = "Input Kode QR Manual (Fallback):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = manualCodeInput,
                        onValueChange = { manualCodeInput = it },
                        placeholder = { Text("Contoh: LAIKA_DEP|... / TPS3R-VALE-01", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f).testTag("manual_qr_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                    Button(
                        onClick = {
                            if (manualCodeInput.isNotBlank()) {
                                scannedResult = manualCodeInput.trim()
                                scannedCategory = "manual"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Proses", fontSize = 12.sp)
                    }
                }

                // Quick Preset Simulation Buttons
                Text(
                    text = "Simulasi Transfer & Scan Barcode Antar HP:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                presetQrCodes.forEach { (label, code, cat) ->
                    OutlinedButton(
                        onClick = {
                            scannedResult = code
                            scannedCategory = cat
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Medium, maxLines = 1)
                            Text(cat.uppercase(), fontSize = 10.sp, color = EmeraldGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        LaikaFooter()
    }
}


@Composable
fun RiwayatSetoranScreen(
    currentUser: User,
    deposits: List<WasteDeposit>,
    onDetailClick: (WasteDeposit) -> Unit,
    onEditClick: (WasteDeposit) -> Unit,
    onDeleteClick: (WasteDeposit) -> Unit,
    onNewDepositClick: () -> Unit
) {
    var selectedTimeRange by remember { mutableStateOf("Semua") }
    var selectedWasteType by remember { mutableStateOf("Semua") }
    var selectedStatus by remember { mutableStateOf("Semua") }
    var searchQuery by remember { mutableStateOf("") }

    val userDeposits = if (currentUser.role == "Admin") deposits else deposits.filter { it.userId == currentUser.id }

    val filteredDeposits = remember(userDeposits, selectedTimeRange, selectedWasteType, selectedStatus, searchQuery) {
        userDeposits.filter { item ->
            val matchType = selectedWasteType == "Semua" || item.wasteType.equals(selectedWasteType, ignoreCase = true)
            val matchStatus = selectedStatus == "Semua" || item.status.equals(selectedStatus, ignoreCase = true)
            val matchSearch = searchQuery.isBlank() ||
                    item.wasteType.contains(searchQuery, ignoreCase = true) ||
                    item.location.contains(searchQuery, ignoreCase = true) ||
                    item.notes.contains(searchQuery, ignoreCase = true) ||
                    item.userName.contains(searchQuery, ignoreCase = true)

            matchType && matchStatus && matchSearch
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header & Add button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "RIWAYAT SETORAN",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Daftar seluruh transaksi setoran sampah Anda",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = onNewDepositClick,
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Setor Baru", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Cari jenis sampah, lokasi, catatan...", fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().testTag("search_history_input"),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        // Filter Pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("Semua", "Pending", "Diverifikasi", "Ditolak").forEach { status ->
                val isSelected = selectedStatus == status
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedStatus = status },
                    label = { Text(status, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ForestGreenPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // List / Table of deposits
        if (filteredDeposits.isEmpty()) {
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
                    Icon(
                        Icons.Default.Inbox,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "Tidak ada data setoran yang cocok dengan filter.",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredDeposits, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDetailClick(item) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                                        text = item.wasteType,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    StatusBadge(status = item.status)
                                }

                                Text(
                                    text = "${item.weight} Kg",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "📍 ${item.location}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "🕒 ${item.date} • ${item.time}",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(
                                        onClick = { onDetailClick(item) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Visibility,
                                            contentDescription = "Detail",
                                            tint = EmeraldGreen,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    // Only editable if status is Pending
                                    if (item.status == "Pending") {
                                        IconButton(
                                            onClick = { onEditClick(item) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Edit,
                                                contentDescription = "Edit",
                                                tint = EcoAmber,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        IconButton(
                                            onClick = { onDeleteClick(item) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Hapus",
                                                tint = EcoRed,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            if (item.status == "Ditolak" && item.rejectionReason.isNotBlank()) {
                                Surface(
                                    color = Color(0xFFF8D7DA),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Alasan Penolakan: ${item.rejectionReason}",
                                        color = EcoRed,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
