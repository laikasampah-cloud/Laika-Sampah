package com.example.ui.screens.penyetor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun StatistikScreen(
    deposits: List<WasteDeposit>,
    wasteTypes: List<WasteType>
) {
    var selectedPeriod by remember { mutableStateOf("Bulan") } // Hari, Minggu, Bulan, Tahun
    val scrollState = rememberScrollState()

    val validDeposits = deposits.filter { it.status != "Ditolak" }

    // Summary for all 8 categories
    val categoryTotals = remember(validDeposits, wasteTypes) {
        val masterCategories = listOf("Organik", "HDPE", "PET", "Kardus", "Paper Box", "Kertas", "Residu", "E-Waste")
        masterCategories.map { type ->
            val matching = validDeposits.filter { it.wasteType.equals(type, ignoreCase = true) }
            val totalKg = matching.sumOf { it.weight }
            val count = matching.size
            val color = when (type) {
                "Organik" -> EcoLeaf
                "HDPE" -> EmeraldGreen
                "PET" -> EcoTeal
                "Kardus" -> EcoAmber
                "Paper Box" -> Color(0xFF8D6E63)
                "Kertas" -> Color(0xFF78909C)
                "Residu" -> EcoRed
                else -> Color(0xFF673AB7)
            }
            WasteSummary(
                wasteType = type,
                totalKg = totalKg,
                transactionCount = count,
                colorHex = String.format("#%06X", (0xFFFFFF and color.value.toInt()))
            )
        }
    }

    val totalAllKg = categoryTotals.sumOf { it.totalKg }.coerceAtLeast(0.01)

    val pieData = categoryTotals.map { item ->
        val col = when (item.wasteType) {
            "Organik" -> EcoLeaf
            "HDPE" -> EmeraldGreen
            "PET" -> EcoTeal
            "Kardus" -> EcoAmber
            "Paper Box" -> Color(0xFF8D6E63)
            "Kertas" -> Color(0xFF78909C)
            "Residu" -> EcoRed
            else -> Color(0xFF673AB7)
        }
        Triple(item.wasteType, item.totalKg, col)
    }

    val barData = categoryTotals.map { Pair(it.wasteType.take(4), it.totalKg) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title & Filter
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "STATISTIK & ANALITIK SAMPAH",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Akumulasi volume sampah terpilah TPS 3R Pomalaa",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Period Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Hari", "Minggu", "Bulan", "Tahun").forEach { period ->
                val isSelected = selectedPeriod == period
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedPeriod = period },
                    label = { Text("Filter: $period", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ForestGreenPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // Total Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = ForestGreenPrimary)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "TOTAL SAMPAH TERKELOLA",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = LightMint
                )
                Text(
                    text = "${String.format(Locale.US, "%.2f", totalAllKg)} Kg",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = "Berdasarkan seluruh transaksi valid TPS 3R PT VALE IGP Pomalaa",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }

        // Donut / Pie Chart
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
                    text = "Distribusi Persentase Sampah",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                WastePieDonutChart(breakdown = pieData)
            }
        }

        // Bar Chart
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
                    text = "Grafik Batang Perbandingan Jenis Sampah",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                SimpleBarChart(data = barData)
            }
        }

        // Table Breakdown of 8 Types
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
                Text(
                    text = "Rincian Akumulasi 8 Master Jenis Sampah",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                categoryTotals.forEach { item ->
                    val percentage = (item.totalKg / totalAllKg) * 100
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
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
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(EmeraldGreen.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("♻️", fontSize = 14.sp)
                                }
                                Column {
                                    Text(
                                        text = item.wasteType,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "${item.transactionCount} Transaksi",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${String.format(Locale.US, "%.2f", item.totalKg)} Kg",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "${String.format(Locale.US, "%.1f", percentage)}%",
                                    fontSize = 11.sp,
                                    color = EmeraldGreen,
                                    fontWeight = FontWeight.Bold
                                )
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
fun DokumentasiScreen(
    documentationList: List<DocumentationItem>,
    onAddDocumentation: (title: String, location: String, category: String, desc: String, photo: String) -> Unit,
    onDeleteDoc: (String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("Semua") }
    var showAddDialog by remember { mutableStateOf(false) }

    val categories = listOf(
        "Semua",
        "Penyetoran",
        "Pemilahan",
        "TPS 3R",
        "Maggot",
        "HDPE",
        "PET",
        "Housekeeping",
        "Sosialisasi",
        "Kegiatan Lingkungan"
    )

    val filteredList = if (selectedCategory == "Semua") {
        documentationList
    } else {
        documentationList.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "DOKUMENTASI TPS 3R",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Galeri kegiatan dan program lingkungan PT VALE",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Tambah Foto", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Categories Scroll
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
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

        // Gallery Items
        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("Belum ada dokumentasi untuk kategori ini.", fontSize = 12.sp, color = TextSecondary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredList, key = { it.id }) { doc ->
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
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Simulated Image Banner
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(ForestGreenPrimary.copy(alpha = 0.8f), EmeraldGreen)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("📸", fontSize = 32.sp)
                                    Text(
                                        text = doc.category,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = doc.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )

                                Surface(
                                    color = LightMint,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = doc.category,
                                        color = ForestGreenPrimary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "📍 ${doc.location}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "📅 ${doc.date}",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }

                            Text(
                                text = doc.description,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddDocumentationDialog(
            onDismiss = { showAddDialog = false },
            onSave = { title, loc, cat, desc ->
                onAddDocumentation(title, loc, cat, desc, "")
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AddDocumentationDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, location: String, category: String, desc: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("TPS 3R Pomalaa") }
    var category by remember { mutableStateOf("Penyetor") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Foto Dokumentasi", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul Kegiatan *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Lokasi *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Kategori (TPS 3R, Maggot, dll.)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Keterangan *") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && description.isNotBlank()) {
                        onSave(title, location, category, description)
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
fun NotifikasiScreen(
    notifications: List<AppNotification>,
    onMarkAsRead: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column {
            Text(
                text = "NOTIFIKASI",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Pemberitahuan verifikasi setoran dan info lingkungan",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("Tidak ada notifikasi saat ini.", fontSize = 13.sp, color = TextSecondary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(notifications, key = { it.id }) { notif ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onMarkAsRead(notif.id) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (!notif.isRead) LightMint.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                        ),
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
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (notif.type) {
                                            "deposit" -> EmeraldGreen.copy(alpha = 0.15f)
                                            "alert" -> Color(0xFFF8D7DA)
                                            else -> LightMint
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (notif.type) {
                                        "deposit" -> Icons.Default.Inventory2
                                        "alert" -> Icons.Default.Warning
                                        else -> Icons.Default.Campaign
                                    },
                                    contentDescription = null,
                                    tint = when (notif.type) {
                                        "alert" -> EcoRed
                                        else -> ForestGreenPrimary
                                    },
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = notif.title,
                                    fontWeight = if (!notif.isRead) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = notif.message,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${notif.date} • ${notif.time}",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }

                            if (!notif.isRead) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldGreen)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfilScreen(
    currentUser: User,
    onSaveProfile: (User) -> Unit
) {
    var name by remember { mutableStateOf(currentUser.name) }
    var phone by remember { mutableStateOf(currentUser.phone) }
    var department by remember { mutableStateOf(currentUser.department) }
    var address by remember { mutableStateOf(currentUser.address) }
    val scrollState = rememberScrollState()

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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(if (currentUser.role == "Admin") EcoOlive else EmeraldGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentUser.name.take(1).uppercase(),
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = currentUser.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    color = if (currentUser.role == "Admin") EcoOrange.copy(alpha = 0.15f) else LightMint,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "ROLE: ${currentUser.role.uppercase()} • PT VALE IGP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (currentUser.role == "Admin") EcoOrange else ForestGreenPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                HorizontalDivider()

                OutlinedTextField(
                    value = currentUser.username,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Username") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Lengkap") },
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Nomor HP (WhatsApp)") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = department,
                    onValueChange = { department = it },
                    label = { Text("Unit / Departemen") },
                    leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Alamat / Lokasi Mess") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Button(
                    onClick = {
                        val updated = currentUser.copy(
                            name = name.trim(),
                            phone = phone.trim(),
                            department = department.trim(),
                            address = address.trim()
                        )
                        onSaveProfile(updated)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SIMPAN PERUBAHAN PROFIL", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        LaikaFooter()
    }
}

@Composable
fun PengaturanScreen(
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    onNavigate: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(
                text = "PENGATURAN APLIKASI",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Preferensi tema, bahasa, keamanan, dan informasi sistem",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
                    text = "Tampilan & Tema",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = null,
                            tint = EmeraldGreen
                        )
                        Column {
                            Text("Mode Gelap (Dark Mode)", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                            Text(
                                if (isDarkMode) "Aktif" else "Nonaktif (Mode Terang)",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = onToggleDarkMode
                    )
                }

                HorizontalDivider()

                Text(
                    text = "Bahasa",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.Language, contentDescription = null, tint = EmeraldGreen)
                        Column {
                            Text("Bahasa Utama", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                            Text("Bahasa Indonesia (Default)", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                    Text("ID", fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
                }
            }
        }

        // Berbagi & Sinkronisasi Antar HP Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigate("share_hub") },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = LightMint),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ForestGreenPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Berbagi Aplikasi & Sinkronisasi HP",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = ForestGreenPrimary
                    )
                    Text(
                        text = "Kirim aplikasi APK, transfer database setoran, & scan QR antar HP",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = ForestGreenPrimary
                )
            }
        }

        // Tentang Aplikasi Card
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
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Tentang LAIKA SAMPAH",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Platform digital terintegrasi untuk pencatatan, pemantauan, dan pengelolaan sampah operasional TPS 3R PT VALE IGP Pomalaa.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Versi: 1.0.0 (Release Build 2026)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EmeraldGreen
                )
                Text(
                    text = "Pengembang: Tim Environmental & Digital Innovation PT VALE IGP",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }

        LaikaFooter()
    }
}
