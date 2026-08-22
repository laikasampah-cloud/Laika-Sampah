package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.LaikaRepository
import com.example.utils.NotificationHelper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class LaikaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LaikaRepository

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _currentRoute = MutableStateFlow("login")
    val currentRoute: StateFlow<String> = _currentRoute.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Filters
    val selectedTimeRange = MutableStateFlow("Semua") // "Hari", "Minggu", "Bulan", "Semua"
    val selectedWasteTypeFilter = MutableStateFlow("Semua")
    val selectedStatusFilter = MutableStateFlow("Semua")
    val searchQuery = MutableStateFlow("")

    val users: StateFlow<List<User>>
    val deposits: StateFlow<List<WasteDeposit>>
    val wasteTypes: StateFlow<List<WasteType>>
    val notifications: StateFlow<List<AppNotification>>
    val announcements: StateFlow<List<Announcement>>
    val documentation: StateFlow<List<DocumentationItem>>
    val menuSettings: StateFlow<List<MenuSetting>>
    val featureSettings: StateFlow<List<FeatureSetting>>
    val chatMessages: StateFlow<List<ChatMessage>>
    val campaigns: StateFlow<List<Campaign>>

    init {
        NotificationHelper.initChannels(application)
        val db = AppDatabase.getDatabase(application)
        repository = LaikaRepository(db.laikaDao())
        repository.seedInitialDataIfNeeded(viewModelScope)

        users = repository.users.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        deposits = repository.deposits.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        wasteTypes = repository.wasteTypes.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        notifications = repository.notifications.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        announcements = repository.announcements.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        documentation = repository.documentation.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        menuSettings = repository.menuSettings.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        featureSettings = repository.featureSettings.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        chatMessages = repository.chatMessages.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        campaigns = repository.campaigns.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun navigateTo(route: String) {
        _currentRoute.value = route
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun toggleDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
    }

    fun login(username: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val user = repository.getUserByUsername(username.trim())
            if (user != null && user.password == pass.trim()) {
                if (user.status == "Nonaktif") {
                    onError("Akun Anda telah dinonaktifkan oleh Admin.")
                    showToast("Akun Anda telah dinonaktifkan.")
                    return@launch
                }
                _currentUser.value = user
                showToast("Login berhasil. Selamat datang ${user.name}!")
                _currentRoute.value = "dashboard"
                onSuccess()
            } else {
                onError("Username atau password salah.")
                showToast("Username atau password salah.")
            }
        }
    }

    fun register(
        name: String,
        username: String,
        phone: String,
        password: String,
        department: String,
        address: String,
        role: String = "Penyetor",
        photo: String = "",
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val existing = repository.getUserByUsername(username.trim())
            if (existing != null) {
                onError("Username sudah digunakan. Silakan pilih username lain.")
                showToast("Username sudah digunakan.")
                return@launch
            }

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val prefix = if (role == "Admin") "admin_" else "user_"
            val newUser = User(
                id = "$prefix${UUID.randomUUID().toString().take(8)}",
                name = name.trim(),
                username = username.trim(),
                phone = phone.trim(),
                password = password.trim(),
                role = role,
                department = department.trim(),
                address = address.trim(),
                photo = photo,
                status = "Aktif",
                createdAt = dateFormat.format(Date())
            )

            repository.registerUser(newUser)
            showToast("Registrasi $role berhasil. Silakan login.")
            _currentRoute.value = "login"
            onSuccess()
        }
    }

    fun resetPassword(username: String, phone: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            showToast("Permintaan reset password berhasil dikirim ke Admin.")
            _currentRoute.value = "login"
            onSuccess()
        }
    }

    fun logout() {
        _currentUser.value = null
        _currentRoute.value = "login"
        showToast("Anda telah keluar dari aplikasi.")
    }

    fun submitDeposit(
        wasteType: String,
        weight: Double,
        location: String,
        notes: String,
        photo: String = "",
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val now = Date()

            val deposit = WasteDeposit(
                id = "dep_${UUID.randomUUID().toString().take(8)}",
                userId = user.id,
                userName = user.name,
                userDepartment = user.department,
                date = dateFormat.format(now),
                time = timeFormat.format(now),
                location = location,
                wasteType = wasteType,
                weight = weight,
                photo = photo,
                notes = notes,
                status = "Pending"
            )

            repository.insertDeposit(deposit)
            NotificationHelper.showDepositNotification(
                context = getApplication(),
                title = "📦 Setoran Sampah Diajukan!",
                message = "Setoran $wasteType (${weight} kg) di $location telah dikirim. Menunggu verifikasi timbangan petugas TPS 3R.",
                targetRoute = "riwayat"
            )
            showToast("Setoran berhasil dikirim! Menunggu verifikasi Admin.")
            _currentRoute.value = "riwayat"
            onSuccess()
        }
    }

    fun updateDeposit(deposit: WasteDeposit, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.updateDeposit(deposit)
            showToast("Data setoran berhasil diperbarui.")
            onSuccess()
        }
    }

    fun deleteDeposit(id: String) {
        viewModelScope.launch {
            repository.deleteDeposit(id)
            showToast("Data setoran berhasil dihapus.")
        }
    }

    fun verifyDeposit(depositId: String) {
        viewModelScope.launch {
            val adminName = _currentUser.value?.name ?: "Admin TPS 3R"
            val targetDep = repository.deposits.firstOrNull()?.find { it.id == depositId }
            repository.verifyDeposit(depositId, adminName)
            NotificationHelper.showDepositNotification(
                context = getApplication(),
                title = "✅ Setoran Sampah Disetujui!",
                message = "Setoran ${targetDep?.wasteType ?: "sampah"} (${targetDep?.weight ?: 0.0} kg) telah diverifikasi & disetujui oleh $adminName. Poin reward Anda bertambah!",
                targetRoute = "riwayat"
            )
            showToast("Setoran berhasil diverifikasi.")
        }
    }

    fun rejectDeposit(depositId: String, reason: String) {
        viewModelScope.launch {
            val adminName = _currentUser.value?.name ?: "Admin TPS 3R"
            repository.rejectDeposit(depositId, adminName, reason)
            NotificationHelper.showDepositNotification(
                context = getApplication(),
                title = "❌ Setoran Sampah Ditolak",
                message = "Setoran ditolak oleh $adminName. Alasan: $reason.",
                targetRoute = "riwayat"
            )
            showToast("Setoran ditolak.")
        }
    }

    fun saveUser(user: User, isNew: Boolean, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (isNew) {
                val existing = repository.getUserByUsername(user.username)
                if (existing != null) {
                    showToast("Username sudah digunakan.")
                    return@launch
                }
                repository.registerUser(user)
                showToast("Pengguna baru berhasil ditambahkan.")
            } else {
                repository.updateUser(user)
                if (_currentUser.value?.id == user.id) {
                    _currentUser.value = user
                }
                showToast("Data pengguna berhasil diperbarui.")
            }
            onSuccess()
        }
    }

    fun toggleUserStatus(user: User) {
        viewModelScope.launch {
            val newStatus = if (user.status == "Aktif") "Nonaktif" else "Aktif"
            repository.updateUser(user.copy(status = newStatus))
            showToast("Status pengguna diubah menjadi $newStatus.")
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            if (_currentUser.value?.id == userId) {
                showToast("Tidak dapat menghapus akun Anda sendiri.")
                return@launch
            }
            repository.deleteUser(userId)
            showToast("Pengguna berhasil dihapus.")
        }
    }

    fun saveWasteType(wasteType: WasteType, isNew: Boolean, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (isNew) {
                repository.insertWasteType(wasteType)
                showToast("Jenis sampah baru berhasil ditambahkan.")
            } else {
                repository.updateWasteType(wasteType)
                showToast("Jenis sampah berhasil diperbarui.")
            }
            onSuccess()
        }
    }

    fun deleteWasteType(id: String) {
        viewModelScope.launch {
            repository.deleteWasteType(id)
            showToast("Jenis sampah berhasil dihapus.")
        }
    }

    fun saveAnnouncement(title: String, content: String, category: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val author = _currentUser.value?.name ?: "Admin TPS 3R Vale"
            val announcement = Announcement(
                id = "anc_${UUID.randomUUID().toString().take(8)}",
                title = title.trim(),
                content = content.trim(),
                date = dateFormat.format(Date()),
                category = category,
                status = "Aktif",
                author = author
            )
            repository.insertAnnouncement(announcement)
            showToast("Pengumuman berhasil dipublikasikan.")
            onSuccess()
        }
    }

    fun deleteAnnouncement(id: String) {
        viewModelScope.launch {
            repository.deleteAnnouncement(id)
            showToast("Pengumuman berhasil dihapus.")
        }
    }

    fun saveDocumentation(title: String, location: String, category: String, description: String, photo: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val author = _currentUser.value?.name ?: "Tim Lingkungan Vale"
            val doc = DocumentationItem(
                id = "doc_${UUID.randomUUID().toString().take(8)}",
                title = title.trim(),
                date = dateFormat.format(Date()),
                location = location.trim(),
                category = category,
                description = description.trim(),
                photo = photo,
                author = author
            )
            repository.insertDocumentation(doc)
            showToast("Dokumentasi berhasil disimpan.")
            onSuccess()
        }
    }

    fun deleteDocumentation(id: String) {
        viewModelScope.launch {
            repository.deleteDocumentation(id)
            showToast("Dokumentasi berhasil dihapus.")
        }
    }

    fun toggleMenuSetting(menu: MenuSetting) {
        viewModelScope.launch {
            repository.updateMenuSetting(menu.copy(isEnabled = !menu.isEnabled))
            showToast("Status menu ${menu.name} diperbarui.")
        }
    }

    fun saveMenuSetting(menu: MenuSetting, isNew: Boolean, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (isNew) {
                repository.insertMenuSetting(menu)
                showToast("Menu baru berhasil ditambahkan.")
            } else {
                repository.updateMenuSetting(menu)
                showToast("Menu berhasil diperbarui.")
            }
            onSuccess()
        }
    }

    fun deleteMenuSetting(id: String) {
        viewModelScope.launch {
            repository.deleteMenuSetting(id)
            showToast("Menu berhasil dihapus.")
        }
    }

    fun toggleFeatureSetting(feature: FeatureSetting) {
        viewModelScope.launch {
            repository.updateFeatureSetting(feature.copy(isEnabled = !feature.isEnabled))
            showToast("Fitur ${feature.name} ${if (!feature.isEnabled) "diaktifkan" else "dinonaktifkan"}.")
        }
    }

    fun markNotificationAsRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    // Chat Operations
    fun sendChatMessage(
        messageText: String,
        receiverId: String = "ADMIN_TPS3R",
        receiverName: String = "Admin TPS 3R Vale",
        quickTopic: String = "Umum"
    ) {
        if (messageText.isBlank()) return
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val now = Date()
            val msg = ChatMessage(
                id = "chat_${UUID.randomUUID().toString().take(8)}",
                senderId = user.id,
                senderName = user.name,
                senderRole = user.role,
                senderDepartment = user.department,
                receiverId = receiverId,
                receiverName = receiverName,
                message = messageText.trim(),
                timestamp = System.currentTimeMillis(),
                dateStr = dateFormat.format(now),
                timeStr = timeFormat.format(now),
                isRead = false,
                quickTopic = quickTopic
            )
            repository.sendChatMessage(msg)
            NotificationHelper.showChatNotification(
                context = getApplication(),
                senderName = user.name,
                message = messageText.trim(),
                targetRoute = "chat"
            )
        }
    }

    fun adminReplyChat(targetUserId: String, targetUserName: String, replyText: String) {
        if (replyText.isBlank()) return
        val admin = _currentUser.value ?: return
        viewModelScope.launch {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val now = Date()
            val msg = ChatMessage(
                id = "chat_${UUID.randomUUID().toString().take(8)}",
                senderId = admin.id,
                senderName = admin.name,
                senderRole = "Admin",
                senderDepartment = admin.department,
                receiverId = targetUserId,
                receiverName = targetUserName,
                message = replyText.trim(),
                timestamp = System.currentTimeMillis(),
                dateStr = dateFormat.format(now),
                timeStr = timeFormat.format(now),
                isRead = false,
                quickTopic = "Jawaban Admin"
            )
            repository.sendChatMessage(msg)
            NotificationHelper.showChatNotification(
                context = getApplication(),
                senderName = "Admin TPS 3R (PT Vale)",
                message = replyText.trim(),
                targetRoute = "chat"
            )
            showToast("Pesan balasan terkirim ke $targetUserName.")
        }
    }

    fun deleteChatMessage(id: String) {
        viewModelScope.launch {
            repository.deleteChatMessage(id)
            showToast("Pesan telah dihapus.")
        }
    }

    fun markChatAsRead() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.markChatAsRead(user.id, user.role == "Admin")
        }
    }

    // Campaign Operations
    fun joinCampaign(campaignId: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val joined = repository.joinCampaign(campaignId, user.id, user.name)
            if (joined) {
                val targetCampaign = repository.campaigns.firstOrNull()?.find { it.id == campaignId }
                NotificationHelper.showCampaignNotification(
                    context = getApplication(),
                    title = "🎉 Pendaftaran Kegiatan Berhasil!",
                    message = "Anda terdaftar pada: ${targetCampaign?.title ?: "Kegiatan Lingkungan PT Vale"}. Poin partisipasi telah ditambahkan!",
                    targetRoute = "campaigns"
                )
                showToast("Berhasil mendaftar kegiatan! Poin partisipasi telah ditambahkan.")
            } else {
                showToast("Pendaftaran dibatalkan.")
            }
        }
    }

    fun saveCampaign(
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
        contact: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            if (id == null) {
                val newCampaign = Campaign(
                    id = "camp_${UUID.randomUUID().toString().take(8)}",
                    title = title.trim(),
                    category = category,
                    organizer = organizer.trim(),
                    startDate = startDate,
                    endDate = endDate,
                    time = time,
                    location = location.trim(),
                    description = description.trim(),
                    targetParticipants = targetParticipants,
                    registeredCount = 0,
                    rewardPoints = rewardPoints,
                    status = status,
                    contactPerson = contact.trim(),
                    registeredUserIds = ""
                )
                repository.insertCampaign(newCampaign)
                NotificationHelper.showCampaignNotification(
                    context = getApplication(),
                    title = "🌱 Info Campaign Baru: ${title.trim()}",
                    message = "Kegiatan di $location ($startDate). Ikuti dan raih +$rewardPoints Poin reward!",
                    targetRoute = "campaigns"
                )
                showToast("Campaign kegiatan berhasil dipublikasikan!")
            } else {
                val existing = repository.campaigns.firstOrNull()?.find { it.id == id }
                val updated = existing?.copy(
                    title = title.trim(),
                    category = category,
                    organizer = organizer.trim(),
                    startDate = startDate,
                    endDate = endDate,
                    time = time,
                    location = location.trim(),
                    description = description.trim(),
                    targetParticipants = targetParticipants,
                    rewardPoints = rewardPoints,
                    status = status,
                    contactPerson = contact.trim()
                ) ?: Campaign(
                    id = id,
                    title = title.trim(),
                    category = category,
                    organizer = organizer.trim(),
                    startDate = startDate,
                    endDate = endDate,
                    time = time,
                    location = location.trim(),
                    description = description.trim(),
                    targetParticipants = targetParticipants,
                    rewardPoints = rewardPoints,
                    status = status,
                    contactPerson = contact.trim()
                )
                repository.updateCampaign(updated)
                NotificationHelper.showCampaignNotification(
                    context = getApplication(),
                    title = "📢 Update Kegiatan Lingkungan",
                    message = "Pembaruan info kegiatan: ${title.trim()} di $location ($startDate).",
                    targetRoute = "campaigns"
                )
                showToast("Data kegiatan berhasil diperbarui.")
            }
            onSuccess()
        }
    }

    fun deleteCampaign(id: String) {
        viewModelScope.launch {
            repository.deleteCampaign(id)
            showToast("Kegiatan telah dihapus.")
        }
    }

    /**
     * Test push / system notification to status bar & phone screen
     */
    fun testSystemNotification(type: String) {
        when (type) {
            "deposit" -> NotificationHelper.showDepositNotification(
                context = getApplication(),
                title = "📦 [Uji Notifikasi] Setoran Sampah",
                message = "Simulasi: Setoran Botol Plastik PET (5.2 kg) berhasil dicatat oleh Petugas TPS 3R.",
                targetRoute = "riwayat"
            )
            "campaign" -> NotificationHelper.showCampaignNotification(
                context = getApplication(),
                title = "🌱 [Uji Notifikasi] Campaign Lingkungan PT Vale",
                message = "Simulasi: Aksi Bersih Pesisir & Tanam Mangrove Pomalaa (Hadiah: 50 Poin).",
                targetRoute = "campaigns"
            )
            "chat" -> NotificationHelper.showChatNotification(
                context = getApplication(),
                senderName = "Admin TPS 3R PT Vale",
                message = "Simulasi: Halo, setoran sampah anorganik Anda telah diverifikasi!",
                targetRoute = "chat"
            )
            else -> NotificationHelper.showGeneralNotification(
                context = getApplication(),
                title = "🔔 [Uji Notifikasi] Laika Sampah",
                message = "Notifikasi sistem Android pada layar utama HP berfungsi normal.",
                targetRoute = "dashboard"
            )
        }
        showToast("Notifikasi uji coba berhasil dikirim ke layar HP!")
    }

    // P2P Data Sharing & Synchronization Operations
    suspend fun getExportPayload(): LaikaSyncPayload {
        val operator = _currentUser.value?.name ?: "Pengguna Laika"
        return repository.getFullSyncPayload(operator)
    }

    fun importDataPayload(
        payload: LaikaSyncPayload,
        overwrite: Boolean,
        onSuccess: (Triple<Int, Int, Int>) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val counts = repository.importSyncPayload(payload, overwrite)
                showToast("Sinkronisasi berhasil! ${counts.first} Setoran, ${counts.second} Pengguna diimpor.")
                onSuccess(counts)
            } catch (e: Exception) {
                e.printStackTrace()
                onError(e.message ?: "Gagal memproses data impor.")
                showToast("Gagal impor data: ${e.message}")
            }
        }
    }

    fun importDepositFromQr(deposit: WasteDeposit, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.insertDeposit(deposit)
                showToast("Setoran dari QR berhasil disimpan ke sistem!")
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
                showToast("Gagal menyimpan setoran: ${e.message}")
            }
        }
    }
}
