package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Announcement
import com.example.data.model.WasteDeposit
import com.example.data.model.WasteType
import com.example.ui.components.*
import com.example.ui.screens.admin.*
import com.example.ui.screens.auth.*
import com.example.ui.screens.campaign.CampaignScreen
import com.example.ui.screens.chat.ChatScreen
import com.example.ui.screens.penyetor.*
import com.example.ui.screens.share.ShareHubScreen
import com.example.ui.theme.*
import com.example.ui.viewmodel.LaikaViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: LaikaViewModel = viewModel()
            val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()

            MyApplicationTheme(darkTheme = isDarkMode) {
                LaikaApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun LaikaApp(viewModel: LaikaViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val currentRoute by viewModel.currentRoute.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    val users by viewModel.users.collectAsStateWithLifecycle()
    val deposits by viewModel.deposits.collectAsStateWithLifecycle()
    val wasteTypes by viewModel.wasteTypes.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val announcements by viewModel.announcements.collectAsStateWithLifecycle()
    val documentation by viewModel.documentation.collectAsStateWithLifecycle()
    val menuSettings by viewModel.menuSettings.collectAsStateWithLifecycle()
    val featureSettings by viewModel.featureSettings.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val campaigns by viewModel.campaigns.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Temporary states for dialogs and QR pre-fill
    var prefilledLocation by remember { mutableStateOf("") }
    var prefilledWasteType by remember { mutableStateOf("") }
    var selectedDetailDeposit by remember { mutableStateOf<WasteDeposit?>(null) }
    var editingDeposit by remember { mutableStateOf<WasteDeposit?>(null) }

    val pendingCount = deposits.count { it.status == "Pending" }
    val unreadNotifs = notifications.count { !it.isRead }

    val isAuthScreen = currentRoute == "login" || currentRoute == "register" || currentRoute == "forgot_password"

    // Back Handlers:
    // 1. If Drawer is open, close drawer on back
    BackHandler(enabled = drawerState.isOpen) {
        coroutineScope.launch { drawerState.close() }
    }

    // 2. If user is in an inner screen (not dashboard and not auth), back goes to Dashboard
    BackHandler(enabled = !isAuthScreen && currentRoute != "dashboard") {
        viewModel.navigateTo("dashboard")
    }

    // 3. If in register or forgot_password, back goes to login
    BackHandler(enabled = isAuthScreen && currentRoute != "login") {
        viewModel.navigateTo("login")
    }

    // If user is not logged in and route is not an auth screen, force login
    LaunchedEffect(currentUser, currentRoute) {
        if (currentUser == null && !isAuthScreen) {
            viewModel.navigateTo("login")
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        if (isAuthScreen) {
            when (currentRoute) {
                "login" -> LoginScreen(
                    onLoginClick = { u, p ->
                        viewModel.login(u, p, onSuccess = {}, onError = {})
                    },
                    onRegisterClick = { viewModel.navigateTo("register") },
                    onForgotPasswordClick = { viewModel.navigateTo("forgot_password") }
                )
                "register" -> RegisterScreen(
                    onRegisterSubmit = { name, u, phone, pass, dept, addr, role ->
                        viewModel.register(name, u, phone, pass, dept, addr, role, onSuccess = {}, onError = {})
                    },
                    onBackToLogin = { viewModel.navigateTo("login") }
                )
                "forgot_password" -> ForgotPasswordScreen(
                    onResetSubmit = { u, phone ->
                        viewModel.resetPassword(u, phone, onSuccess = {})
                    },
                    onBackToLogin = { viewModel.navigateTo("login") }
                )
            }
        } else {
            // Main Authenticated Scaffold with Navigation Drawer
            ModalNavigationDrawer(
                drawerState = drawerState,
                gesturesEnabled = true,
                drawerContent = {
                    ModalDrawerSheet(
                        modifier = Modifier.width(280.dp),
                        drawerContainerColor = MaterialTheme.colorScheme.surface
                    ) {
                        LaikaSidebarContent(
                            currentUser = currentUser,
                            currentRoute = currentRoute,
                            menuSettings = menuSettings,
                            pendingVerificationCount = pendingCount,
                            onNavigate = { route ->
                                viewModel.navigateTo(route)
                                coroutineScope.launch { drawerState.close() }
                            },
                            onLogout = {
                                viewModel.logout()
                                coroutineScope.launch { drawerState.close() }
                            }
                        )
                    }
                }
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        LaikaTopHeader(
                            currentUser = currentUser,
                            currentRoute = currentRoute,
                            unreadNotifCount = unreadNotifs,
                            onMenuClick = { coroutineScope.launch { drawerState.open() } },
                            onBackClick = { viewModel.navigateTo("dashboard") },
                            onNotifClick = { viewModel.navigateTo("notifikasi") },
                            onProfileClick = { viewModel.navigateTo("profil") },
                            onSettingsClick = { viewModel.navigateTo("pengaturan") },
                            onLogoutClick = { viewModel.logout() }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentRoute) {
                            "dashboard" -> {
                                if (currentUser?.role == "Admin") {
                                    AdminDashboardScreen(
                                        currentUser = currentUser!!,
                                        users = users,
                                        deposits = deposits,
                                        onVerifikasiClick = { viewModel.navigateTo("admin_verifikasi") },
                                        onPenggunaClick = { viewModel.navigateTo("admin_users") },
                                        onLaporanClick = { viewModel.navigateTo("admin_laporan") },
                                        onDetailDeposit = { selectedDetailDeposit = it },
                                        onChatClick = { viewModel.navigateTo("chat") },
                                        onCampaignClick = { viewModel.navigateTo("campaigns") }
                                    )
                                } else if (currentUser != null) {
                                    PenyetorDashboardScreen(
                                        currentUser = currentUser!!,
                                        deposits = deposits,
                                        announcements = announcements,
                                        onSetorClick = {
                                            prefilledLocation = ""
                                            prefilledWasteType = ""
                                            viewModel.navigateTo("setor_sampah")
                                        },
                                        onScanQrClick = { viewModel.navigateTo("scan_qr") },
                                        onRiwayatClick = { viewModel.navigateTo("riwayat") },
                                        onStatistikClick = { viewModel.navigateTo("statistik") },
                                        onDetailDeposit = { selectedDetailDeposit = it },
                                        onChatClick = { viewModel.navigateTo("chat") },
                                        onCampaignClick = { viewModel.navigateTo("campaigns") }
                                    )
                                }
                            }

                            "setor_sampah" -> {
                                if (currentUser != null) {
                                    SetorSampahScreen(
                                        currentUser = currentUser!!,
                                        wasteTypes = wasteTypes,
                                        prefilledLocation = prefilledLocation,
                                        prefilledWasteType = prefilledWasteType,
                                        onSubmitDeposit = { type, weight, loc, notes, photo ->
                                            viewModel.submitDeposit(type, weight, loc, notes, photo) {
                                                viewModel.navigateTo("riwayat")
                                            }
                                        },
                                        onCancel = { viewModel.navigateTo("dashboard") }
                                    )
                                }
                            }

                            "scan_qr" -> {
                                ScanQrScreen(
                                    onQrResultFound = { type, data ->
                                        viewModel.showToast("QR Terbaca: $data ($type)")
                                    },
                                    onQuickDeposit = { loc, waste ->
                                        prefilledLocation = loc
                                        prefilledWasteType = waste
                                        viewModel.navigateTo("setor_sampah")
                                    },
                                    onImportDepositFromQr = { deposit ->
                                        viewModel.importDepositFromQr(deposit) {}
                                    }
                                )
                            }

                            "share_hub", "berbagi" -> {
                                if (currentUser != null) {
                                    ShareHubScreen(
                                        currentUser = currentUser!!,
                                        deposits = deposits,
                                        users = users,
                                        wasteTypes = wasteTypes,
                                        announcements = announcements,
                                        documentation = documentation,
                                        campaigns = campaigns,
                                        onExportFullData = { viewModel.getExportPayload() },
                                        onImportPayload = { payload, overwrite, onDone ->
                                            viewModel.importDataPayload(
                                                payload = payload,
                                                overwrite = overwrite,
                                                onSuccess = { onDone() },
                                                onError = { onDone() }
                                            )
                                        },
                                        onNavigateToScanQr = { viewModel.navigateTo("scan_qr") },
                                        onShowToast = { viewModel.showToast(it) }
                                    )
                                }
                            }

                            "riwayat" -> {
                                if (currentUser != null) {
                                    RiwayatSetoranScreen(
                                        currentUser = currentUser!!,
                                        deposits = deposits,
                                        onDetailClick = { selectedDetailDeposit = it },
                                        onEditClick = { editingDeposit = it },
                                        onDeleteClick = { viewModel.deleteDeposit(it.id) },
                                        onNewDepositClick = {
                                            prefilledLocation = ""
                                            prefilledWasteType = ""
                                            viewModel.navigateTo("setor_sampah")
                                        }
                                    )
                                }
                            }

                            "statistik" -> {
                                StatistikScreen(
                                    deposits = deposits,
                                    wasteTypes = wasteTypes
                                )
                            }

                            "dokumentasi" -> {
                                DokumentasiScreen(
                                    documentationList = documentation,
                                    onAddDocumentation = { title, loc, cat, desc, photo ->
                                        viewModel.saveDocumentation(title, loc, cat, desc, photo) {}
                                    },
                                    onDeleteDoc = { viewModel.deleteDocumentation(it) }
                                )
                            }

                            "chat" -> {
                                if (currentUser != null) {
                                    ChatScreen(
                                        currentUser = currentUser!!,
                                        users = users,
                                        messages = chatMessages,
                                        onSendMessage = { text, receiverId, receiverName, topic ->
                                            viewModel.sendChatMessage(text, receiverId, receiverName, topic)
                                        },
                                        onDeleteMessage = { viewModel.deleteChatMessage(it) }
                                    )
                                }
                            }

                            "campaigns" -> {
                                if (currentUser != null) {
                                    CampaignScreen(
                                        currentUser = currentUser!!,
                                        campaigns = campaigns,
                                        onJoinCampaign = { viewModel.joinCampaign(it) },
                                        onSaveCampaign = { id, title, cat, org, start, end, time, loc, desc, target, reward, status, contact ->
                                            viewModel.saveCampaign(id, title, cat, org, start, end, time, loc, desc, target, reward, status, contact) {}
                                        },
                                        onDeleteCampaign = { viewModel.deleteCampaign(it) }
                                    )
                                }
                            }

                            "notifikasi" -> {
                                NotifikasiScreen(
                                    notifications = notifications,
                                    onMarkAsRead = { viewModel.markNotificationAsRead(it) }
                                )
                            }

                            "profil" -> {
                                if (currentUser != null) {
                                    ProfilScreen(
                                        currentUser = currentUser!!,
                                        onSaveProfile = { updated ->
                                            viewModel.saveUser(updated, isNew = false) {}
                                        }
                                    )
                                }
                            }

                            "pengaturan" -> {
                                PengaturanScreen(
                                    isDarkMode = isDarkMode,
                                    onToggleDarkMode = { viewModel.toggleDarkMode(it) },
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                            }

                            // Admin Routes
                            "admin_users" -> {
                                DataPenggunaScreen(
                                    users = users,
                                    onSaveUser = { u, isNew ->
                                        viewModel.saveUser(u, isNew) {}
                                    },
                                    onToggleStatus = { viewModel.toggleUserStatus(it) },
                                    onDeleteUser = { viewModel.deleteUser(it) }
                                )
                            }

                            "admin_verifikasi" -> {
                                VerifikasiSetoranScreen(
                                    deposits = deposits,
                                    onVerify = { viewModel.verifyDeposit(it) },
                                    onReject = { id, reason -> viewModel.rejectDeposit(id, reason) },
                                    onDetail = { selectedDetailDeposit = it }
                                )
                            }

                            "admin_sampah" -> {
                                DataSampahScreen(
                                    wasteTypes = wasteTypes,
                                    onSaveType = { wt, isNew ->
                                        viewModel.saveWasteType(wt, isNew) {}
                                    },
                                    onToggleType = { wt ->
                                        viewModel.saveWasteType(wt.copy(isActive = !wt.isActive), isNew = false) {}
                                    },
                                    onDeleteType = { viewModel.deleteWasteType(it) }
                                )
                            }

                            "admin_laporan", "laporan" -> {
                                LaporanScreen(
                                    deposits = deposits,
                                    onExportSuccess = { viewModel.showToast(it) }
                                )
                            }

                            "admin_rekap" -> {
                                RekapSampahScreen(deposits = deposits)
                            }

                            "admin_pengumuman" -> {
                                PengumumanScreen(
                                    announcements = announcements,
                                    onSaveAnnouncement = { anc, isNew ->
                                        viewModel.saveAnnouncement(anc.title, anc.content, anc.category) {}
                                    },
                                    onDeleteAnnouncement = { viewModel.deleteAnnouncement(it) }
                                )
                            }

                            "admin_menu" -> {
                                ManajemenMenuScreen(
                                    menuSettings = menuSettings,
                                    onToggleMenu = { viewModel.toggleMenuSetting(it) }
                                )
                            }

                            "admin_fitur" -> {
                                ManajemenFiturScreen(
                                    featureSettings = featureSettings,
                                    onToggleFeature = { viewModel.toggleFeatureSetting(it) }
                                )
                            }

                            "tps_modules" -> {
                                Tps3rModulesScreen()
                            }

                            else -> {
                                // Default fallback to dashboard
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Button(onClick = { viewModel.navigateTo("dashboard") }) {
                                        Text("Kembali ke Dashboard")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Overlay Components: Toast & Dialogs
        ToastOverlay(
            message = toastMessage,
            onDismiss = { viewModel.clearToast() }
        )

        // Detail Dialog
        DepositDetailDialog(
            deposit = selectedDetailDeposit,
            onDismiss = { selectedDetailDeposit = null }
        )

        // Edit Deposit Dialog
        if (editingDeposit != null) {
            EditDepositDialog(
                deposit = editingDeposit!!,
                wasteTypes = wasteTypes,
                onDismiss = { editingDeposit = null },
                onSave = { updated ->
                    viewModel.updateDeposit(updated) {
                        editingDeposit = null
                    }
                }
            )
        }
    }
}

@Composable
fun EditDepositDialog(
    deposit: WasteDeposit,
    wasteTypes: List<WasteType>,
    onDismiss: () -> Unit,
    onSave: (WasteDeposit) -> Unit
) {
    var weightText by remember { mutableStateOf(deposit.weight.toString()) }
    var location by remember { mutableStateOf(deposit.location) }
    var notes by remember { mutableStateOf(deposit.notes) }
    var selectedWasteType by remember { mutableStateOf(deposit.wasteType) }
    var expandedType by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Data Setoran", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedWasteType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Jenis Sampah") },
                        trailingIcon = {
                            IconButton(onClick = { expandedType = !expandedType }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    DropdownMenu(
                        expanded = expandedType,
                        onDismissRequest = { expandedType = false }
                    ) {
                        wasteTypes.filter { it.isActive }.forEach { wt ->
                            DropdownMenuItem(
                                text = { Text(wt.name) },
                                onClick = {
                                    selectedWasteType = wt.name
                                    expandedType = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it },
                    label = { Text("Berat (Kg)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Lokasi Penyetoran") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Catatan") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val w = weightText.replace(",", ".").toDoubleOrNull() ?: deposit.weight
                    val updated = deposit.copy(
                        wasteType = selectedWasteType,
                        weight = w,
                        location = location.trim(),
                        notes = notes.trim()
                    )
                    onSave(updated)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
            ) {
                Text("Simpan Perubahan", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}
