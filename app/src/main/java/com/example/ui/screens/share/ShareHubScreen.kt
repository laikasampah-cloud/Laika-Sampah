package com.example.ui.screens.share

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.utils.DataTransferHelper
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareHubScreen(
    currentUser: User,
    deposits: List<WasteDeposit>,
    users: List<User>,
    wasteTypes: List<WasteType>,
    announcements: List<Announcement>,
    documentation: List<DocumentationItem>,
    campaigns: List<Campaign>,
    onExportFullData: (suspend () -> LaikaSyncPayload),
    onImportPayload: (payload: LaikaSyncPayload, overwrite: Boolean, onDone: () -> Unit) -> Unit,
    onNavigateToScanQr: () -> Unit,
    onShowToast: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Bagikan Aplikasi, 1: Transfer & Sinkronisasi Data, 2: QR Transfer Setoran & Kartu
    val scrollState = rememberScrollState()

    // Import Preview State
    var pendingImportPayload by remember { mutableStateOf<LaikaSyncPayload?>(null) }
    var showImportDialog by remember { mutableStateOf(false) }
    var importIsOverwrite by remember { mutableStateOf(false) }
    var showManualPasteDialog by remember { mutableStateOf(false) }
    var pastedJsonText by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }

    // File picker for JSON files
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val reader = BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8))
                    val jsonStr = reader.readText()
                    val parsed = DataTransferHelper.parseJsonToPayload(jsonStr)
                    if (parsed != null) {
                        pendingImportPayload = parsed
                        showImportDialog = true
                    } else {
                        onShowToast("File tidak valid atau format data salah.")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onShowToast("Gagal membaca file: ${e.message}")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Corporate Hero Banner
        DashboardBrandHeroBanner()

        // Screen Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ForestGreenPrimary),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "BERBAGI ANTAR HP",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "Kirim Aplikasi, Data Cadangan & Setoran",
                                fontSize = 12.sp,
                                color = LightMint
                            )
                        }
                    }

                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "P2P SYNC",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Text(
                    text = "Fitur lengkap untuk membagikan aplikasi ke HP rekan kerja, mentransfer basis data setoran tanpa internet via Bluetooth / Quick Share, serta scan QR setoran langsung antar layar HP.",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    lineHeight = 16.sp
                )
            }
        }

        // Tab Navigation Bar
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = EmeraldGreen,
            modifier = Modifier.clip(RoundedCornerShape(14.dp))
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("1. Bagikan App", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Smartphone, contentDescription = null, modifier = Modifier.size(20.dp)) },
                modifier = Modifier.testTag("tab_share_app")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("2. Kirim Data", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.SyncAlt, contentDescription = null, modifier = Modifier.size(20.dp)) },
                modifier = Modifier.testTag("tab_sync_data")
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("3. QR Transfer", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(20.dp)) },
                modifier = Modifier.testTag("tab_qr_transfer")
            )
        }

        // Content Based on Selected Tab
        when (selectedTab) {
            0 -> ShareAppTab(
                context = context,
                onShowToast = onShowToast
            )
            1 -> SyncDataTab(
                context = context,
                currentUser = currentUser,
                deposits = deposits,
                users = users,
                wasteTypes = wasteTypes,
                announcements = announcements,
                documentation = documentation,
                campaigns = campaigns,
                onExportFullData = {
                    coroutineScope.launch {
                        isProcessing = true
                        val payload = onExportFullData()
                        DataTransferHelper.exportAndShareDataFile(context, payload)
                        isProcessing = false
                    }
                },
                onCopyJsonData = {
                    coroutineScope.launch {
                        val payload = onExportFullData()
                        val json = DataTransferHelper.serializePayloadToJson(payload)
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("LaikaSampahBackup", json)
                        clipboard.setPrimaryClip(clip)
                        onShowToast("Teks JSON Cadangan Data berhasil disalin ke clipboard!")
                    }
                },
                onPickFile = {
                    filePickerLauncher.launch("application/json")
                },
                onManualPaste = {
                    showManualPasteDialog = true
                }
            )
            2 -> QrTransferTab(
                currentUser = currentUser,
                deposits = deposits,
                onNavigateToScanQr = onNavigateToScanQr,
                onShowToast = onShowToast
            )
        }

        LaikaFooter()
    }

    // Confirmation Import Dialog
    if (showImportDialog && pendingImportPayload != null) {
        val payload = pendingImportPayload!!
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.CloudDownload, contentDescription = null, tint = EmeraldGreen)
                    Text("Konfirmasi Impor Data", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "File cadangan data dari HP pengirim siap dimasukkan ke dalam aplikasi:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("👤 Pengirim: ${payload.exportedBy.ifBlank { "Anonim" }}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("📅 Waktu Ekspor: ${payload.exportedAt}", fontSize = 11.sp, color = TextSecondary)
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            Text("📦 Setoran Sampah: ${payload.deposits.size} Transaksi", fontSize = 12.sp)
                            Text("👥 Data Pengguna: ${payload.users.size} Orang", fontSize = 12.sp)
                            Text("♻️ Master Jenis: ${payload.wasteTypes.size} Tipe", fontSize = 12.sp)
                            Text("📢 Pengumuman & Doc: ${payload.announcements.size + payload.documentation.size} Item", fontSize = 12.sp)
                        }
                    }

                    Text("Pilih Metode Penggabungan:", fontWeight = FontWeight.Bold, fontSize = 12.sp)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { importIsOverwrite = false }
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(selected = !importIsOverwrite, onClick = { importIsOverwrite = false })
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text("Gabungkan Data (Merge)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Menambahkan data baru tanpa menghapus data yang sudah ada.", fontSize = 11.sp, color = TextSecondary)
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { importIsOverwrite = true }
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(selected = importIsOverwrite, onClick = { importIsOverwrite = true })
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text("Gantikan Semua Data (Restore Bersih)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = EcoRed)
                            Text("Menghapus data lama di HP ini dan menggantinya dengan data baru.", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showImportDialog = false
                        onImportPayload(payload, importIsOverwrite) {
                            pendingImportPayload = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
                ) {
                    Text("PROSES IMPOR DATA")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false; pendingImportPayload = null }) {
                    Text("Batal")
                }
            }
        )
    }

    // Manual Paste JSON Dialog
    if (showManualPasteDialog) {
        AlertDialog(
            onDismissRequest = { showManualPasteDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.ContentPaste, contentDescription = null, tint = EmeraldGreen)
                    Text("Tempel Kode Data JSON", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Tempel teks cadangan JSON yang Anda terima dari pesan WhatsApp/Bluetooth:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = pastedJsonText,
                        onValueChange = { pastedJsonText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        placeholder = { Text("{\"version\": 1, \"appName\": \"LAIKA SAMPAH\"...}", fontSize = 11.sp) },
                        textStyle = LocalTextStyle.current.copy(fontSize = 11.sp),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = DataTransferHelper.parseJsonToPayload(pastedJsonText)
                        if (parsed != null) {
                            showManualPasteDialog = false
                            pendingImportPayload = parsed
                            showImportDialog = true
                        } else {
                            onShowToast("Format JSON tidak valid atau rusak.")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
                ) {
                    Text("Verifikasi Data")
                }
            },
            dismissButton = {
                TextButton(onClick = { showManualPasteDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }
}

// ----------------------------------------------------
// TAB 1: BAGIKAN APLIKASI
// ----------------------------------------------------
@Composable
private fun ShareAppTab(
    context: Context,
    onShowToast: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // App Sharing Card with Visual QR
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "INSTALASI APLIKASI DI HP LAIN",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Arahkan kamera HP lain ke kode QR di bawah untuk mengunduh, atau klik tombol bagikan untuk mengirim file APK aplikasi langsung via Bluetooth / Quick Share / WhatsApp.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                // High-contrast QR Display Box
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(3.dp, EmeraldGreen, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.QrCode2,
                            contentDescription = "QR Unduh Aplikasi",
                            tint = Color(0xFF1E2421),
                            modifier = Modifier.size(120.dp)
                        )
                        Text(
                            text = "LAIKA SAMPAH VALE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = ForestGreenPrimary
                        )
                    }
                }

                // Action Buttons
                Button(
                    onClick = { DataTransferHelper.shareAppToOtherPhone(context) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_share_app_apk"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("KIRIM APLIKASI KE HP LAIN (BLUETOOTH / WHATSAPP)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = {
                        val text = "Unduh dan pasang aplikasi LAIKA SAMPAH PT VALE IGP Pomalaa untuk mencatat setoran sampah terpilah dan mendukung zero waste."
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("LaikaAppInvite", text)
                        clipboard.setPrimaryClip(clip)
                        onShowToast("Teks informasi aplikasi berhasil disalin!")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Salin Info Aplikasi & Petunjuk", fontSize = 12.sp)
                }
            }
        }

        // Instructions Steps Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MintContainer)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.HelpOutline, contentDescription = null, tint = ForestGreenPrimary)
                    Text("Cara Membagikan Aplikasi Antar HP:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ForestGreenPrimary)
                }

                val steps = listOf(
                    "1. Tekan tombol **KIRIM APLIKASI** di atas.",
                    "2. Pilih media pengiriman: **Quick Share**, **Bluetooth**, atau **WhatsApp** ke HP penerima.",
                    "3. Pada HP penerima, buka notifikasi atau file yang diterima dan tap **Pasang / Install**.",
                    "4. Selesai! HP penerima kini dapat langsung login atau registrasi sebagai nasabah TPS 3R."
                )

                steps.forEach { step ->
                    Text(
                        text = step,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// TAB 2: TRANSFER & SINKRONISASI DATA
// ----------------------------------------------------
@Composable
private fun SyncDataTab(
    context: Context,
    currentUser: User,
    deposits: List<WasteDeposit>,
    users: List<User>,
    wasteTypes: List<WasteType>,
    announcements: List<Announcement>,
    documentation: List<DocumentationItem>,
    campaigns: List<Campaign>,
    onExportFullData: () -> Unit,
    onCopyJsonData: () -> Unit,
    onPickFile: () -> Unit,
    onManualPaste: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Section 1: KIRIM DATA KE HP LAIN (EKSPOR)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(LightMint),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, tint = ForestGreenPrimary)
                    }
                    Column {
                        Text(
                            text = "KIRIM DATA KE HP LAIN (EKSPOR)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Cadangkan semua transaksi & kirim via Bluetooth / WhatsApp",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Data summary pills
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Ringkasan Data Saat Ini di HP Ini:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            DataPill(label = "Setoran", value = "${deposits.size}", icon = Icons.Default.Inventory2)
                            DataPill(label = "Pengguna", value = "${users.size}", icon = Icons.Default.People)
                            DataPill(label = "Jenis Sampah", value = "${wasteTypes.size}", icon = Icons.Default.Category)
                            DataPill(label = "Kegiatan", value = "${campaigns.size}", icon = Icons.Default.Campaign)
                        }
                    }
                }

                // Export buttons
                Button(
                    onClick = onExportFullData,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_export_share_data"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("BAGIKAN FILE DATA KE HP LAIN (.JSON)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onCopyJsonData,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Salin Kode Cadangan (Teks JSON)", fontSize = 12.sp)
                }
            }
        }

        // Section 2: TERIMA DATA DARI HP LAIN (IMPOR)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFFFF3CD)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, tint = Color(0xFFB78103))
                    }
                    Column {
                        Text(
                            text = "TERIMA DATA DARI HP LAIN (IMPOR)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Buka file .json yang diterima dari HP rekan untuk disinkronkan",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = "Jika Anda baru saja menerima file cadangan (.json) dari HP pengirim melalui WhatsApp, Bluetooth, atau Quick Share, buka file tersebut di sini.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onPickFile,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("btn_pick_import_file"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                    ) {
                        Icon(Icons.Default.FolderOpen, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("PILIH FILE (.JSON)", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = onManualPaste,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("btn_paste_json"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("TEMPEL TEKS JSON", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// TAB 3: QR TRANSFER SETORAN & KARTU NASABAH
// ----------------------------------------------------
@Composable
private fun QrTransferTab(
    currentUser: User,
    deposits: List<WasteDeposit>,
    onNavigateToScanQr: () -> Unit,
    onShowToast: (String) -> Unit
) {
    val context = LocalContext.current
    var selectedDepositForQr by remember { mutableStateOf<WasteDeposit?>(deposits.firstOrNull()) }
    var activeMode by remember { mutableStateOf("card") } // "card" or "deposit"

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Mode Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { activeMode = "card" },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeMode == "card") ForestGreenPrimary else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (activeMode == "card") Color.White else MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("📇 Kartu Nasabah Saya", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { activeMode = "deposit" },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeMode == "deposit") ForestGreenPrimary else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (activeMode == "deposit") Color.White else MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("📦 QR Transfer Setoran", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (activeMode == "card") {
            // Digital ID Card QR
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "KARTU DIGITAL NASABAH TPS 3R",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "Tunjukkan QR ini ke HP Petugas TPS 3R untuk memindai identitas Anda seketika saat proses penimbangan.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    // Card QR Container
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .border(3.dp, EmeraldGreen, RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.QrCode2,
                                contentDescription = "QR Kartu Nasabah",
                                tint = Color(0xFF1E2421),
                                modifier = Modifier.size(120.dp)
                            )
                            Text(
                                text = currentUser.name.uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = ForestGreenPrimary
                            )
                            Text(
                                text = currentUser.department,
                                fontSize = 9.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Surface(
                        color = LightMint,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Nama: ${currentUser.name}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ForestGreenPrimary)
                                Text("ID: ${currentUser.id} • Dept: ${currentUser.department}", fontSize = 11.sp, color = TextSecondary)
                            }
                            Text("✅ VALID", fontWeight = FontWeight.Black, fontSize = 11.sp, color = EmeraldGreen)
                        }
                    }
                }
            }
        } else {
            // Deposit Transfer QR
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "TRANSFER DATA SETORAN VIA QR",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "Pilih transaksi setoran di bawah untuk menampilkan kode QR. HP Petugas / rekan dapat memindainya langsung dari layar Anda untuk mengimpor transaksi ini.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    if (deposits.isEmpty()) {
                        Text("Belum ada riwayat transaksi setoran.", fontSize = 12.sp, color = TextSecondary)
                    } else {
                        // Deposit Selector Dropdown / Row
                        Text("Pilih Transaksi Setoran:", fontSize = 11.sp, fontWeight = FontWeight.Bold)

                        val currentDep = selectedDepositForQr ?: deposits.first()

                        Box(
                            modifier = Modifier
                                .size(200.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .border(3.dp, EmeraldGreen, RoundedCornerShape(16.dp))
                                .padding(14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Default.QrCode2,
                                    contentDescription = "QR Setoran",
                                    tint = Color(0xFF1E2421),
                                    modifier = Modifier.size(110.dp)
                                )
                                Text(
                                    text = "${currentDep.wasteType} • ${currentDep.weight} Kg",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = ForestGreenPrimary
                                )
                                Text(
                                    text = "${currentDep.date} - ${currentDep.location}",
                                    fontSize = 9.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        // WhatsApp Receipt Share Button
                        Button(
                            onClick = {
                                DataTransferHelper.shareDepositReceipt(context, currentDep)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Bagikan Bukti Struk via WhatsApp", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Quick Button to open QR Scanner
        Button(
            onClick = onNavigateToScanQr,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("btn_open_scanner_from_share"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
        ) {
            Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("BUKA SCANNER QR UNTUK MEMINDAI HP LAIN", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

@Composable
private fun DataPill(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(icon, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(18.dp))
        Text(text = value, fontWeight = FontWeight.Black, fontSize = 13.sp, color = ForestGreenPrimary)
        Text(text = label, fontSize = 10.sp, color = TextSecondary)
    }
}
