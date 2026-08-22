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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.utils.ReportExporter
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DataSampahScreen(
    wasteTypes: List<WasteType>,
    onSaveType: (WasteType, Boolean) -> Unit,
    onToggleType: (WasteType) -> Unit,
    onDeleteType: (String) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingType by remember { mutableStateOf<WasteType?>(null) }

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
                    text = "MASTER DATA JENIS SAMPAH",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Kelola 8 kategori utama sampah terpilah TPS 3R",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = {
                    editingType = null
                    showAddDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Tambah Jenis", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(wasteTypes, key = { it.id }) { type ->
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
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(LightMint),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("♻️", fontSize = 18.sp)
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = type.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "[${type.code}]",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = EmeraldGreen
                                )
                            }
                            Text(
                                text = "Kategori: ${type.category}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Switch(
                                checked = type.isActive,
                                onCheckedChange = { onToggleType(type) }
                            )

                            IconButton(
                                onClick = {
                                    editingType = type
                                    showAddDialog = true
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = EcoAmber, modifier = Modifier.size(18.dp))
                            }

                            IconButton(
                                onClick = { onDeleteType(type.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = EcoRed, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        WasteTypeFormDialog(
            type = editingType,
            onDismiss = { showAddDialog = false },
            onSave = { item, isNew ->
                onSaveType(item, isNew)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun WasteTypeFormDialog(
    type: WasteType?,
    onDismiss: () -> Unit,
    onSave: (WasteType, Boolean) -> Unit
) {
    var name by remember { mutableStateOf(type?.name ?: "") }
    var code by remember { mutableStateOf(type?.code ?: "") }
    var category by remember { mutableStateOf(type?.category ?: "Plastik") }
    var colorHex by remember { mutableStateOf(type?.colorHex ?: "#198754") }

    val isNew = type == null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isNew) "Tambah Master Jenis Sampah" else "Edit Jenis Sampah", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Jenis Sampah *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Kode Singkatan (e.g. HDPE, PET, ORG) *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Kategori (e.g. Organik, Plastik, Kertas, B3)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && code.isNotBlank()) {
                        val wt = WasteType(
                            id = type?.id ?: "wt_${UUID.randomUUID().toString().take(6)}",
                            name = name.trim(),
                            code = code.trim().uppercase(),
                            category = category.trim(),
                            colorHex = colorHex,
                            iconName = "Category",
                            isActive = type?.isActive ?: true
                        )
                        onSave(wt, isNew)
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
fun LaporanScreen(
    deposits: List<WasteDeposit>,
    onExportSuccess: (String) -> Unit = {}
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) } // 0: Harian, 1: Mingguan, 2: Bulanan, 3: Tahunan, 4: Semua Data
    val scrollState = rememberScrollState()

    val validDeposits = deposits.filter { it.status != "Ditolak" }

    // Date / Period Pickers State
    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    var selectedDate by remember { mutableStateOf("2026-08-19") }
    var selectedMonth by remember { mutableStateOf("2026-08") }
    var selectedYear by remember { mutableStateOf("2026") }
    var selectedWeek by remember { mutableStateOf("Minggu Ini") }

    // Filter deposits based on selected period
    val (filteredDeposits, periodTitle, periodLabel, reportEnum) = remember(
        validDeposits, selectedTab, selectedDate, selectedMonth, selectedYear, selectedWeek
    ) {
        when (selectedTab) {
            0 -> {
                val list = validDeposits.filter { it.date == selectedDate }
                val title = "Laporan Harian - $selectedDate"
                val label = "Tanggal $selectedDate"
                listOf(list, title, label, ReportExporter.ReportPeriod.HARIAN)
            }
            1 -> {
                // Mingguan: filter around current week or all august items
                val list = if (selectedWeek == "Minggu Ini") {
                    validDeposits.filter { it.date >= "2026-08-17" && it.date <= "2026-08-23" }
                } else {
                    validDeposits.filter { it.date >= "2026-08-10" && it.date <= "2026-08-16" }
                }
                val title = "Laporan Mingguan ($selectedWeek)"
                val label = "Periode $selectedWeek (Agustus 2026)"
                listOf(list, title, label, ReportExporter.ReportPeriod.MINGGUAN)
            }
            2 -> {
                val list = validDeposits.filter { it.date.startsWith(selectedMonth) }
                val monthName = when (selectedMonth.takeLast(2)) {
                    "01" -> "Januari"; "02" -> "Februari"; "03" -> "Maret"; "04" -> "April"
                    "05" -> "Mei"; "06" -> "Juni"; "07" -> "Juli"; "08" -> "Agustus"
                    "09" -> "September"; "10" -> "Oktober"; "11" -> "November"; "12" -> "Desember"
                    else -> selectedMonth
                }
                val title = "Laporan Bulanan - $monthName 2026"
                val label = "Bulan $monthName 2026"
                listOf(list, title, label, ReportExporter.ReportPeriod.BULANAN)
            }
            3 -> {
                val list = validDeposits.filter { it.date.startsWith(selectedYear) }
                val title = "Laporan Tahunan - Tahun $selectedYear"
                val label = "Tahun Anggaran $selectedYear"
                listOf(list, title, label, ReportExporter.ReportPeriod.TAHUNAN)
            }
            else -> {
                val title = "Laporan Master Akumulasi Keseluruhan"
                val label = "Semua Periode Transaksi"
                listOf(validDeposits, title, label, ReportExporter.ReportPeriod.SEMUA)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    val currentDeposits = filteredDeposits as List<WasteDeposit>
    val currentPeriodTitle = periodTitle as String
    val currentPeriodLabel = periodLabel as String
    val currentReportEnum = reportEnum as ReportExporter.ReportPeriod

    val totalKg = currentDeposits.sumOf { it.weight }
    val totalTransactions = currentDeposits.size
    val avgWeight = if (totalTransactions > 0) totalKg / totalTransactions else 0.0
    val co2Saved = totalKg * 2.15

    val categoryRows = remember(currentDeposits) {
        val groups = currentDeposits.groupBy { it.wasteType }
        groups.map { (wasteType, items) ->
            val sum = items.sumOf { it.weight }
            val pct = if (totalKg > 0) (sum / totalKg) * 100.0 else 0.0
            Triple(wasteType, items.size, sum) to pct
        }.sortedByDescending { it.first.third }
    }

    val deptRows = remember(currentDeposits) {
        val groups = currentDeposits.groupBy { it.userDepartment.ifBlank { "Umum" } }
        groups.map { (dept, items) ->
            val sum = items.sumOf { it.weight }
            val pct = if (totalKg > 0) (sum / totalKg) * 100.0 else 0.0
            Triple(dept, items.size, sum) to pct
        }.sortedByDescending { it.first.third }
    }

    fun handleExportExcel() {
        val file = ReportExporter.exportToExcelCsv(
            context = context,
            period = currentReportEnum,
            periodDetail = currentPeriodLabel,
            deposits = if (currentDeposits.isNotEmpty()) currentDeposits else validDeposits,
            operatorName = "Admin TPS 3R PT Vale IGP"
        )
        if (file != null) {
            ReportExporter.shareReportFile(context, file, currentPeriodTitle)
            onExportSuccess("Berhasil membuat file Excel: ${file.name}")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "REKAPITULASI & LAPORAN TPS 3R",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "PT Vale Indonesia Tbk - IGP Pomalaa",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldGreen
                    )
                    Text(
                        text = "Laporan harian, mingguan, bulanan & tahunan siap ekspor Excel",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(LightMint),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Assessment, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(26.dp))
                }
            }
        }

        // Period Tab Selector (Harian, Mingguan, Bulanan, Tahunan, Semua)
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 0.dp,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = ForestGreenPrimary,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
        ) {
            listOf("📅 Harian", "🗓️ Mingguan", "📊 Bulanan", "📈 Tahunan", "📑 Semua").forEachIndexed { idx, title ->
                Tab(
                    selected = selectedTab == idx,
                    onClick = { selectedTab = idx },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == idx) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp,
                            color = if (selectedTab == idx) ForestGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }

        // Sub-filter Pickers based on Tab
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                when (selectedTab) {
                    0 -> {
                        // Harian: Select Date Chips
                        Text("Pilih Tanggal:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("2026-08-19" to "Hari Ini", "2026-08-18" to "Kemarin", "2026-08-17" to "17 Agust").forEach { (d, label) ->
                                val isSel = selectedDate == d
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable { selectedDate = d },
                                    color = if (isSel) ForestGreenPrimary else MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                    1 -> {
                        // Mingguan: Select Week
                        Text("Pilih Minggu:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Minggu Ini" to "Mgg Ini (17-23)", "Minggu Lalu" to "Mgg Lalu (10-16)").forEach { (w, label) ->
                                val isSel = selectedWeek == w
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable { selectedWeek = w },
                                    color = if (isSel) ForestGreenPrimary else MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                    2 -> {
                        // Bulanan: Select Month
                        Text("Pilih Bulan:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("2026-08" to "Agustus", "2026-07" to "Juli", "2026-06" to "Juni").forEach { (m, label) ->
                                val isSel = selectedMonth == m
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable { selectedMonth = m },
                                    color = if (isSel) ForestGreenPrimary else MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                    3 -> {
                        // Tahunan: Select Year
                        Text("Pilih Tahun:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("2026" to "Tahun 2026", "2025" to "Tahun 2025").forEach { (y, label) ->
                                val isSel = selectedYear == y
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable { selectedYear = y },
                                    color = if (isSel) ForestGreenPrimary else MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                    else -> {
                        Text("Menampilkan seluruh data tercatat dari awal operasional", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Export Action Card (Excel .xlsx / .csv)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = ForestGreenPrimary)
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
                    Column {
                        Text(
                            text = "EKSPOR LAPORAN RESMI (EXCEL)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = LightMint
                        )
                        Text(
                            text = currentPeriodTitle,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                    Text("📊 .XLSX / .CSV", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LightMint)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { handleExportExcel() },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_export_excel")
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Unduh File Excel", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    OutlinedButton(
                        onClick = { handleExportExcel() },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
                        modifier = Modifier.weight(0.9f)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Bagikan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Executive KPI Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("TOTAL SAMPAH", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "${String.format(Locale.US, "%.1f", totalKg)} Kg",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("${totalTransactions} Transaksi", fontSize = 10.sp, color = EmeraldGreen, fontWeight = FontWeight.Bold)
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("REDUKSI KARBON", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "${String.format(Locale.US, "%.1f", co2Saved)} Kg",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = ForestGreenPrimary
                    )
                    Text("CO2e Terhindar", fontSize = 10.sp, color = TextSecondary)
                }
            }
        }

        // Breakdown per Jenis Sampah Table
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
                        text = "1. Rincian Berdasarkan Jenis Sampah",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text("${categoryRows.size} Kategori", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                // Table Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("JENIS SAMPAH", fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.weight(1.5f))
                    Text("SETORAN", fontWeight = FontWeight.Bold, fontSize = 10.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(0.8f))
                    Text("TOTAL (KG)", fontWeight = FontWeight.Bold, fontSize = 10.sp, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
                    Text("PORSI", fontWeight = FontWeight.Bold, fontSize = 10.sp, textAlign = TextAlign.End, modifier = Modifier.weight(0.7f))
                }

                if (categoryRows.isEmpty()) {
                    Text(
                        text = "Belum ada transaksi pada periode ini.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 12.dp),
                        textAlign = TextAlign.Center
                    )
                } else {
                    categoryRows.forEach { (triple, pct) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(triple.first, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, modifier = Modifier.weight(1.5f))
                            Text("${triple.second}", fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(0.8f))
                            Text(
                                "${String.format(Locale.US, "%.1f", triple.third)} Kg",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.End,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                "${String.format(Locale.US, "%.1f", pct)}%",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldGreen,
                                textAlign = TextAlign.End,
                                modifier = Modifier.weight(0.7f)
                            )
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    }

                    // Total Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("TOTAL", fontWeight = FontWeight.Black, fontSize = 12.sp, modifier = Modifier.weight(1.5f))
                        Text("$totalTransactions", fontWeight = FontWeight.Black, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(0.8f))
                        Text(
                            "${String.format(Locale.US, "%.1f", totalKg)} Kg",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = ForestGreenPrimary,
                            textAlign = TextAlign.End,
                            modifier = Modifier.weight(1f)
                        )
                        Text("100%", fontWeight = FontWeight.Black, fontSize = 11.sp, textAlign = TextAlign.End, modifier = Modifier.weight(0.7f))
                    }
                }
            }
        }

        // Breakdown per Departemen
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
                    text = "2. Kontribusi per Departemen / Unit",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (deptRows.isEmpty()) {
                    Text("Belum ada data.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    deptRows.take(5).forEach { (triple, pct) ->
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(triple.first, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                Text("${String.format(Locale.US, "%.1f", triple.third)} Kg (${String.format(Locale.US, "%.1f", pct)}%)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                            LinearProgressIndicator(
                                progress = { (pct / 100.0).toFloat().coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = EmeraldGreen,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Ledger of Recent Deposits
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
                        text = "3. Riwayat Transaksi Buku Besar",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text("${currentDeposits.size} Item", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                if (currentDeposits.isEmpty()) {
                    Text("Tidak ada transaksi untuk ditampilkan.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    currentDeposits.take(8).forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.userName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("${item.date} ${item.time} • ${item.wasteType}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(
                                "${item.weight} Kg",
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    }
                }
            }
        }

        LaikaFooter()
    }
}

@Composable
fun RekapSampahScreen(
    deposits: List<WasteDeposit>
) {
    val valid = deposits.filter { it.status != "Ditolak" }

    val masterCategories = listOf(
        "Organik",
        "HDPE",
        "PET",
        "Kardus",
        "Paper Box",
        "Kertas",
        "Residu",
        "E-Waste"
    )

    val rows = masterCategories.map { type ->
        val matching = valid.filter { it.wasteType.equals(type, ignoreCase = true) }
        Pair(type, matching.sumOf { it.weight })
    }

    val totalKg = rows.sumOf { it.second }.coerceAtLeast(0.01)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(
                text = "REKAPITULASI SAMPAH MASUK",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Akumulasi komprehensif 8 jenis sampah operasional TPS 3R",
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
                // Table header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(6.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("JENIS SAMPAH", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1.5f))
                    Text("TOTAL (KG)", fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
                    Text("PERSENTASE", fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
                }

                rows.forEach { (type, kg) ->
                    val pct = (kg / totalKg) * 100
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(type, fontWeight = FontWeight.Medium, fontSize = 13.sp, modifier = Modifier.weight(1.5f))
                        Text(
                            "${String.format(Locale.US, "%.2f", kg)} Kg",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            textAlign = TextAlign.End,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            "${String.format(Locale.US, "%.1f", pct)}%",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = EmeraldGreen,
                            textAlign = TextAlign.End,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    HorizontalDivider()
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("TOTAL SAMPAH MASUK", fontWeight = FontWeight.Black, fontSize = 13.sp, modifier = Modifier.weight(1.5f))
                    Text(
                        "${String.format(Locale.US, "%.2f", totalKg)} Kg",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = ForestGreenPrimary,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f)
                    )
                    Text("100.0%", fontWeight = FontWeight.Black, fontSize = 13.sp, color = EmeraldGreen, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
                }
            }
        }

        LaikaFooter()
    }
}

@Composable
fun PengumumanScreen(
    announcements: List<Announcement>,
    onSaveAnnouncement: (Announcement, Boolean) -> Unit,
    onDeleteAnnouncement: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<Announcement?>(null) }

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
                    text = "PENGUMUMAN TPS 3R",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Siaran informasi jadwal, regulasi, dan kegiatan lingkungan",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = {
                    editingItem = null
                    showDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Buat Info", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(announcements, key = { it.id }) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
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
                                Surface(
                                    color = LightMint,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = item.category,
                                        color = ForestGreenPrimary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = item.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                IconButton(
                                    onClick = {
                                        editingItem = item
                                        showDialog = true
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = EcoAmber, modifier = Modifier.size(16.dp))
                                }
                                IconButton(
                                    onClick = { onDeleteAnnouncement(item.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = EcoRed, modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        Text(
                            text = item.content,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "Dipublikasikan: ${item.date} oleh ${item.author}",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        AnnouncementFormDialog(
            item = editingItem,
            onDismiss = { showDialog = false },
            onSave = { anc, isNew ->
                onSaveAnnouncement(anc, isNew)
                showDialog = false
            }
        )
    }
}

@Composable
fun AnnouncementFormDialog(
    item: Announcement?,
    onDismiss: () -> Unit,
    onSave: (Announcement, Boolean) -> Unit
) {
    var title by remember { mutableStateOf(item?.title ?: "") }
    var content by remember { mutableStateOf(item?.content ?: "") }
    var category by remember { mutableStateOf(item?.category ?: "Informasi") }
    val isNew = item == null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isNew) "Buat Pengumuman Baru" else "Edit Pengumuman", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul Pengumuman *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Kategori (Informasi, Kegiatan, Sosialisasi)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Isi Pengumuman *") },
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val anc = Announcement(
                            id = item?.id ?: "anc_${UUID.randomUUID().toString().take(6)}",
                            title = title.trim(),
                            content = content.trim(),
                            category = category.trim(),
                            date = item?.date ?: dateFormat.format(Date()),
                            author = "Admin TPS 3R",
                            status = "Aktif"
                        )
                        onSave(anc, isNew)
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
fun ManajemenMenuScreen(
    menuSettings: List<MenuSetting>,
    onToggleMenu: (MenuSetting) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column {
            Text(
                text = "MANAJEMEN MENU & NAVIGASI",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Atur visibilitas dan hak akses menu untuk Admin dan Penyetor",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(menuSettings.sortedBy { it.orderIndex }, key = { it.id }) { menu ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                color = if (menu.role == "Admin") EcoOrange.copy(alpha = 0.15f) else LightMint,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = menu.role,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (menu.role == "Admin") EcoOrange else ForestGreenPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = menu.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "Rute: /${menu.route} • Urutan: #${menu.orderIndex}",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Switch(
                            checked = menu.isEnabled,
                            onCheckedChange = { onToggleMenu(menu) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ManajemenFiturScreen(
    featureSettings: List<FeatureSetting>,
    onToggleFeature: (FeatureSetting) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column {
            Text(
                text = "MANAJEMEN FITUR SISTEM",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Aktifkan atau nonaktifkan modul fungsional secara dinamis",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(featureSettings, key = { it.key }) { feat ->
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
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (feat.isEnabled) LightMint else Color(0xFFEEEEEE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (feat.isEnabled) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                contentDescription = null,
                                tint = if (feat.isEnabled) ForestGreenPrimary else Color.Gray,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = feat.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = feat.description,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Kunci: ${feat.key}",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }

                        Switch(
                            checked = feat.isEnabled,
                            onCheckedChange = { onToggleFeature(feat) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Tps3rModulesScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Biokonversi Maggot", "Komposting", "Baling & Manifest", "Checklist K3")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column {
            Text(
                text = "MODUL OPERASIONAL TPS 3R",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Sistem pemantauan teknis pengolahan sampah terpadu PT VALE IGP",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        when (selectedTab) {
            0 -> MaggotModuleContent()
            1 -> CompostingModuleContent()
            2 -> BalingModuleContent()
            3 -> ChecklistModuleContent()
        }
    }
}

@Composable
fun MaggotModuleContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("🐛 Unit Biokonversi Larva BSF (Black Soldier Fly)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Biokonversi sampah sisa makanan mess dan kantin menjadi pakan berprotein tinggi & pupuk kasgot.", fontSize = 12.sp, color = TextSecondary)
                HorizontalDivider()
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Kapasitas Pakan Masuk (Organik):", fontSize = 12.sp)
                    Text("150 Kg / Hari", fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Estimasi Panen Maggot Basah:", fontSize = 12.sp)
                    Text("35 Kg / Siklus", fontWeight = FontWeight.Bold, color = EmeraldGreen)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Produksi Kasgot (Pupuk Kasgot):", fontSize = 12.sp)
                    Text("45 Kg / Minggu", fontWeight = FontWeight.Bold, color = EcoAmber)
                }
            }
        }
    }
}

@Composable
fun CompostingModuleContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("🌱 Unit Komposting Organik & Dedaunan Vale", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Pengolahan daun gugur, sisa pemangkasan rumput, dan biomassa menjadi pupuk kompos.", fontSize = 12.sp, color = TextSecondary)
                HorizontalDivider()
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Bak Aerator Aktif:", fontSize = 12.sp)
                    Text("4 Unit (Suhu 55°C)", fontWeight = FontWeight.Bold, color = EmeraldGreen)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Waktu Pematangan Rata-rata:", fontSize = 12.sp)
                    Text("21 Hari", fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Distribusi Kompos:", fontSize = 12.sp)
                    Text("1,250 Kg (Nursery & Reklamasi)", fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
                }
            }
        }
    }
}

@Composable
fun BalingModuleContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("📦 Mesin Press Baling & Logistik Daur Ulang", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Pemadatan sampah anorganik (HDPE, PET, Kardus) untuk pengiriman ke off-taker daur ulang.", fontSize = 12.sp, color = TextSecondary)
                HorizontalDivider()
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Bale HDPE Siap Kirim:", fontSize = 12.sp)
                    Text("12 Bale (480 Kg)", fontWeight = FontWeight.Bold, color = EmeraldGreen)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Bale Kardus & Paper Box:", fontSize = 12.sp)
                    Text("20 Bale (1,100 Kg)", fontWeight = FontWeight.Bold, color = EcoAmber)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Nomor Manifest Terakhir:", fontSize = 12.sp)
                    Text("VALE-TPS3R-2026-088", fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
                }
            }
        }
    }
}

@Composable
fun ChecklistModuleContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("📋 Checklist Standar K3L & Housekeeping TPS 3R", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                HorizontalDivider()
                listOf(
                    "Penyimpanan APD Lengkap (Masker, Sarung Tangan, Safety Shoes)" to true,
                    "Ketersediaan Eye Wash & Kotak P3K Terkalibrasi" to true,
                    "Pengendalian Bau & Drainase Bak Penampung Lindi" to true,
                    "Pemisahan Khusus Limbah B3 dari Alur Domestik 3R" to true,
                    "Inspeksi Pemadam Api Ringan (APAR) Siap Pakai" to true
                ).forEach { (item, isChecked) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(18.dp))
                        Text(item, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}
