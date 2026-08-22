package com.example.data.repository

import com.example.data.local.LaikaDao
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class LaikaRepository(private val dao: LaikaDao) {

    val users: Flow<List<User>> = dao.getAllUsers()
    val deposits: Flow<List<WasteDeposit>> = dao.getAllDeposits()
    val wasteTypes: Flow<List<WasteType>> = dao.getAllWasteTypes()
    val notifications: Flow<List<AppNotification>> = dao.getAllNotifications()
    val announcements: Flow<List<Announcement>> = dao.getAllAnnouncements()
    val documentation: Flow<List<DocumentationItem>> = dao.getAllDocumentation()
    val menuSettings: Flow<List<MenuSetting>> = dao.getAllMenuSettings()
    val featureSettings: Flow<List<FeatureSetting>> = dao.getAllFeatureSettings()
    val chatMessages: Flow<List<ChatMessage>> = dao.getAllChatMessages()
    val campaigns: Flow<List<Campaign>> = dao.getAllCampaigns()

    fun getDepositsByUser(userId: String): Flow<List<WasteDeposit>> = dao.getDepositsByUserId(userId)
    fun getNotificationsForUser(userId: String): Flow<List<AppNotification>> = dao.getNotificationsForUser(userId)
    fun getChatMessagesForUser(userId: String): Flow<List<ChatMessage>> = dao.getChatMessagesForUser(userId)

    suspend fun getUserByUsername(username: String): User? = dao.getUserByUsername(username)
    suspend fun getUserById(id: String): User? = dao.getUserById(id)

    suspend fun registerUser(user: User): Boolean {
        val existing = dao.getUserByUsername(user.username)
        if (existing != null) return false
        dao.insertUser(user)
        return true
    }

    suspend fun updateUser(user: User) = dao.updateUser(user)
    suspend fun deleteUser(userId: String) = dao.deleteUser(userId)

    suspend fun insertDeposit(deposit: WasteDeposit) {
        dao.insertDeposit(deposit)
        // Add notification for admin
        val notif = AppNotification(
            id = "notif_${UUID.randomUUID().toString().take(8)}",
            userId = "ALL",
            title = "Setoran Baru",
            message = "${deposit.userName} telah menyetorkan ${deposit.weight} Kg ${deposit.wasteType} di ${deposit.location}.",
            date = deposit.date,
            time = deposit.time,
            isRead = false,
            type = "deposit"
        )
        dao.insertNotification(notif)
    }

    suspend fun updateDeposit(deposit: WasteDeposit) = dao.updateDeposit(deposit)
    suspend fun deleteDeposit(id: String) = dao.deleteDeposit(id)

    suspend fun verifyDeposit(depositId: String, adminName: String) {
        val deposit = dao.getDepositById(depositId) ?: return
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val updated = deposit.copy(
            status = "Diverifikasi",
            verifiedBy = adminName,
            verifiedAt = dateFormat.format(Date()),
            rejectionReason = ""
        )
        dao.updateDeposit(updated)

        dao.insertNotification(
            AppNotification(
                id = "notif_${UUID.randomUUID().toString().take(8)}",
                userId = deposit.userId,
                title = "Setoran Diverifikasi",
                message = "Setoran Anda sebesar ${deposit.weight} Kg ${deposit.wasteType} telah diverifikasi oleh $adminName.",
                date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                isRead = false,
                type = "deposit"
            )
        )
    }

    suspend fun rejectDeposit(depositId: String, adminName: String, reason: String) {
        val deposit = dao.getDepositById(depositId) ?: return
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val updated = deposit.copy(
            status = "Ditolak",
            verifiedBy = adminName,
            verifiedAt = dateFormat.format(Date()),
            rejectionReason = reason
        )
        dao.updateDeposit(updated)

        dao.insertNotification(
            AppNotification(
                id = "notif_${UUID.randomUUID().toString().take(8)}",
                userId = deposit.userId,
                title = "Setoran Ditolak",
                message = "Setoran Anda sebesar ${deposit.weight} Kg ${deposit.wasteType} ditolak. Alasan: $reason",
                date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                isRead = false,
                type = "alert"
            )
        )
    }

    suspend fun insertWasteType(wasteType: WasteType) = dao.insertWasteType(wasteType)
    suspend fun updateWasteType(wasteType: WasteType) = dao.updateWasteType(wasteType)
    suspend fun deleteWasteType(id: String) = dao.deleteWasteType(id)

    suspend fun markNotificationAsRead(id: String) = dao.markNotificationAsRead(id)
    suspend fun insertAnnouncement(announcement: Announcement) {
        dao.insertAnnouncement(announcement)
        dao.insertNotification(
            AppNotification(
                id = "notif_${UUID.randomUUID().toString().take(8)}",
                userId = "ALL",
                title = "Pengumuman: ${announcement.title}",
                message = announcement.content,
                date = announcement.date,
                time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                isRead = false,
                type = "announcement"
            )
        )
    }

    suspend fun insertDocumentation(doc: DocumentationItem) = dao.insertDocumentation(doc)
    suspend fun deleteDocumentation(id: String) = dao.deleteDocumentation(id)
    suspend fun deleteAnnouncement(id: String) = dao.deleteAnnouncement(id)

    suspend fun updateMenuSetting(menu: MenuSetting) = dao.updateMenuSetting(menu)
    suspend fun insertMenuSetting(menu: MenuSetting) = dao.insertMenuSetting(menu)
    suspend fun deleteMenuSetting(id: String) = dao.deleteMenuSetting(id)

    suspend fun updateFeatureSetting(feature: FeatureSetting) = dao.updateFeatureSetting(feature)

    // Chat Operations
    suspend fun sendChatMessage(message: ChatMessage) {
        dao.insertChatMessage(message)
    }

    suspend fun markChatAsRead(userId: String, isAdmin: Boolean) {
        dao.markChatAsRead(userId, isAdmin)
    }

    suspend fun deleteChatMessage(id: String) {
        dao.deleteChatMessage(id)
    }

    // Campaign Operations
    suspend fun insertCampaign(campaign: Campaign) {
        dao.insertCampaign(campaign)
        dao.insertNotification(
            AppNotification(
                id = "notif_${UUID.randomUUID().toString().take(8)}",
                userId = "ALL",
                title = "Kegiatan Baru: ${campaign.title}",
                message = "Ikuti kegiatan ${campaign.category} di ${campaign.location} pada ${campaign.startDate}. Dapatkan ${campaign.rewardPoints} Poin!",
                date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                isRead = false,
                type = "announcement"
            )
        )
    }

    suspend fun updateCampaign(campaign: Campaign) = dao.updateCampaign(campaign)
    suspend fun deleteCampaign(id: String) = dao.deleteCampaign(id)

    suspend fun joinCampaign(campaignId: String, userId: String, userName: String): Boolean {
        val campaign = dao.getCampaignById(campaignId) ?: return false
        val currentList = campaign.registeredUserIds.split(",").filter { it.isNotBlank() }.toMutableList()
        if (currentList.contains(userId)) {
            // Already joined -> Leave
            currentList.remove(userId)
            val updated = campaign.copy(
                registeredUserIds = currentList.joinToString(","),
                registeredCount = (campaign.registeredCount - 1).coerceAtLeast(0)
            )
            dao.updateCampaign(updated)
            return false
        } else {
            // Join
            currentList.add(userId)
            val updated = campaign.copy(
                registeredUserIds = currentList.joinToString(","),
                registeredCount = campaign.registeredCount + 1
            )
            dao.updateCampaign(updated)

            dao.insertNotification(
                AppNotification(
                    id = "notif_${UUID.randomUUID().toString().take(8)}",
                    userId = userId,
                    title = "Terdaftar di Kegiatan Lingkungan",
                    message = "Selamat $userName! Anda telah terdaftar dalam kegiatan '${campaign.title}'. Jangan lupa hadir pada ${campaign.startDate} di ${campaign.location}.",
                    date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                    time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                    isRead = false,
                    type = "info"
                )
            )
            return true
        }
    }

    fun seedInitialDataIfNeeded(scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            val userCount = dao.getAllUsers().firstOrNull()?.size ?: 0
            if (userCount == 0) {
                // Seed Users
                val initialUsers = listOf(
                    User(
                        id = "user_admin_01",
                        name = "Admin TPS 3R Vale",
                        username = "admin",
                        phone = "081234567890",
                        password = "admin123",
                        role = "Admin",
                        department = "Environmental & Waste Dept",
                        address = "Kompleks Perkantoran PT VALE IGP Pomalaa",
                        status = "Aktif",
                        createdAt = "2026-01-01"
                    ),
                    User(
                        id = "user_penyetor_01",
                        name = "Ahmad Fauzi",
                        username = "user01",
                        phone = "082198765432",
                        password = "user123",
                        role = "Penyetor",
                        department = "Mining Operations IGP",
                        address = "Mess IGP Pomalaa Blok B-12",
                        status = "Aktif",
                        createdAt = "2026-01-10"
                    ),
                    User(
                        id = "user_penyetor_02",
                        name = "Siti Nurhaliza",
                        username = "siti_pomalaa",
                        phone = "085211223344",
                        password = "user123",
                        role = "Penyetor",
                        department = "HR & General Affairs",
                        address = "Perumahan Staff Vale Pomalaa No. 45",
                        status = "Aktif",
                        createdAt = "2026-01-15"
                    ),
                    User(
                        id = "user_penyetor_03",
                        name = "Budi Hartono",
                        username = "budi_smelter",
                        phone = "081377889900",
                        password = "user123",
                        role = "Penyetor",
                        department = "Process Plant Smelter IGP",
                        address = "Area Workshop Central Pomalaa",
                        status = "Aktif",
                        createdAt = "2026-01-20"
                    )
                )
                dao.insertUsers(initialUsers)

                // Seed Waste Types
                val initialWasteTypes = listOf(
                    WasteType("wt_1", "Organik", "ORG", "Daur Ulang Biologis", "#2E7D32", "Eco", true, "Sisa makanan, dedaunan, limbah kantin untuk pakan Maggot BSF dan Kompos"),
                    WasteType("wt_2", "HDPE", "HDPE", "Plastik Keras", "#198754", "WaterDrop", true, "Botol detergen, drum plastik, jerigen, tutup galon HDPE"),
                    WasteType("wt_3", "PET", "PET", "Plastik Bening", "#0D6EFD", "LocalDrink", true, "Botol air mineral bening, botol minuman kemasan PET"),
                    WasteType("wt_4", "Kardus", "KRD", "Kertas & Karton", "#B78103", "Inventory2", true, "Kardus cokelat tebal, kotak kemasan logistik, packaging"),
                    WasteType("wt_5", "Paper Box", "PBX", "Kertas & Karton", "#9C640C", "AllInbox", true, "Kotak nasi kertas, duplex, karton tipis bersih"),
                    WasteType("wt_6", "Kertas", "KRT", "Kertas & Karton", "#6C757D", "Description", true, "Kertas HVS bekas, dokumen arsip, koran, majalah"),
                    WasteType("wt_7", "Residu", "RSD", "Non-Recyclable", "#DC3545", "DeleteSweep", true, "Sampah tidak dapat didaur ulang, tisu kotor, puntung, styrofoam kotor"),
                    WasteType("wt_8", "E-Waste", "EWT", "Limbah Khusus / B3", "#6F42C1", "DevicesOther", true, "Elektronik bekas kantor, kabel, baterai, komponen sirkuit kecil")
                )
                dao.insertWasteTypes(initialWasteTypes)

                // Seed Waste Deposits
                val initialDeposits = listOf(
                    WasteDeposit("dep_101", "user_penyetor_01", "Ahmad Fauzi", "Mining Operations IGP", "2026-08-19", "08:30", "TPS 3R Pomalaa", "HDPE", 5.50, "", "Setoran botol jerigen oli bersih", "Diverifikasi", "Admin TPS 3R Vale", "2026-08-19 09:15"),
                    WasteDeposit("dep_102", "user_penyetor_01", "Ahmad Fauzi", "Mining Operations IGP", "2026-08-18", "14:10", "Area Workshop Central", "Kardus", 18.20, "", "Kardus sparepart alat berat", "Diverifikasi", "Admin TPS 3R Vale", "2026-08-18 15:00"),
                    WasteDeposit("dep_103", "user_penyetor_01", "Ahmad Fauzi", "Mining Operations IGP", "2026-08-17", "10:20", "Kantin Utama IGP", "Organik", 22.00, "", "Sisa sayur & buah persiapan pakan Maggot", "Diverifikasi", "Admin TPS 3R Vale", "2026-08-17 11:00"),
                    WasteDeposit("dep_104", "user_penyetor_01", "Ahmad Fauzi", "Mining Operations IGP", "2026-08-19", "09:45", "Mess IGP Pomalaa", "PET", 3.80, "", "Botol air mineral mess karyawan", "Pending"),
                    WasteDeposit("dep_105", "user_penyetor_02", "Siti Nurhaliza", "HR & General Affairs", "2026-08-19", "08:15", "Gedung Kantor Pusat Vale", "Kertas", 12.50, "", "Kertas arsip kantor bulanan", "Pending"),
                    WasteDeposit("dep_106", "user_penyetor_02", "Siti Nurhaliza", "HR & General Affairs", "2026-08-16", "11:00", "Gedung Kantor Pusat Vale", "Paper Box", 7.40, "", "Box snack rapat dan sosialisasi", "Diverifikasi", "Admin TPS 3R Vale", "2026-08-16 13:00"),
                    WasteDeposit("dep_107", "user_penyetor_02", "Siti Nurhaliza", "HR & General Affairs", "2026-08-14", "15:30", "Mess IGP Pomalaa", "E-Waste", 4.20, "", "Keyboard dan kabel monitor rusak kantor", "Diverifikasi", "Admin TPS 3R Vale", "2026-08-14 16:30"),
                    WasteDeposit("dep_108", "user_penyetor_03", "Budi Hartono", "Process Plant Smelter IGP", "2026-08-19", "07:50", "Pabrik Smelter Pomalaa", "HDPE", 35.00, "", "Pallet plastik pecah dan drum potongan", "Pending"),
                    WasteDeposit("dep_109", "user_penyetor_03", "Budi Hartono", "Process Plant Smelter IGP", "2026-08-18", "16:20", "Pabrik Smelter Pomalaa", "Residu", 14.50, "", "Pembersihan housekeeping area control room", "Diverifikasi", "Admin TPS 3R Vale", "2026-08-18 17:00"),
                    WasteDeposit("dep_110", "user_penyetor_03", "Budi Hartono", "Process Plant Smelter IGP", "2026-08-15", "09:00", "TPS 3R Pomalaa", "Kardus", 45.00, "", "Kemasan filter dan conveyor belt parts", "Diverifikasi", "Admin TPS 3R Vale", "2026-08-15 10:15"),
                    WasteDeposit("dep_111", "user_penyetor_01", "Ahmad Fauzi", "Mining Operations IGP", "2026-08-12", "13:40", "Workshop Tambang", "Residu", 8.00, "", "Majun oli kotor terkontaminasi (ditolak untuk B3 khusus)", "Ditolak", "Admin TPS 3R Vale", "2026-08-12 14:00", "Limbah majun oli harus diserahkan ke TPS LB3 Khusus, bukan TPS 3R domestik."),
                    WasteDeposit("dep_112", "user_penyetor_02", "Siti Nurhaliza", "HR & General Affairs", "2026-08-10", "10:00", "Perumahan Staff Vale", "PET", 15.60, "", "Pengumpulan botol dari warga perumahan", "Diverifikasi", "Admin TPS 3R Vale", "2026-08-10 11:20"),
                    WasteDeposit("dep_113", "user_penyetor_03", "Budi Hartono", "Process Plant Smelter IGP", "2026-08-08", "08:30", "Kantin Smelter", "Organik", 48.00, "", "Limbah organik sisa makanan batch 1", "Diverifikasi", "Admin TPS 3R Vale", "2026-08-08 09:40"),
                    WasteDeposit("dep_114", "user_penyetor_01", "Ahmad Fauzi", "Mining Operations IGP", "2026-08-05", "14:00", "TPS 3R Pomalaa", "Kardus", 24.50, "", "Pembersihan gudang logistik Pomalaa", "Diverifikasi", "Admin TPS 3R Vale", "2026-08-05 15:10")
                )
                dao.insertDeposits(initialDeposits)

                // Seed Notifications
                val initialNotifications = listOf(
                    AppNotification("notif_1", "ALL", "Selamat Datang di LAIKA SAMPAH", "Sistem Pengelolaan Sampah Terintegrasi TPS 3R PT VALE IGP Pomalaa resmi diluncurkan.", "2026-08-01", "08:00", false, "system"),
                    AppNotification("notif_2", "user_penyetor_01", "Setoran Diverifikasi", "Setoran Anda sebesar 5.5 Kg HDPE telah diverifikasi oleh Admin TPS 3R Vale.", "2026-08-19", "09:15", false, "deposit"),
                    AppNotification("notif_3", "ALL", "Program Sedekah Sampah Daur Ulang", "Dapatkan apresiasi lingkungan untuk unit dengan setoran terpilah terbanyak bulan ini!", "2026-08-15", "10:00", true, "announcement"),
                    AppNotification("notif_4", "user_penyetor_01", "Data Setoran Menunggu Verifikasi", "Setoran 3.8 Kg PET Anda sedang dalam antrean verifikasi Admin TPS 3R.", "2026-08-19", "09:46", false, "deposit")
                )
                dao.insertNotifications(initialNotifications)

                // Seed Announcements
                val initialAnnouncements = listOf(
                    Announcement("anc_1", "Jadwal Operasional TPS 3R Pomalaa 2026", "TPS 3R PT VALE IGP Pomalaa buka setiap Senin - Sabtu pukul 07.30 - 17.00 WITA. Layanan timbang sampah organik kantin dibuka setiap pagi pukul 08.00 - 10.00 WITA.", "2026-08-15", "Informasi", "Aktif", "Admin TPS 3R Vale"),
                    Announcement("anc_2", "Pelatihan Budidaya Maggot BSF untuk Unit Kantin", "Sosialisasi reduksi sampah organik berbasis biokonversi Black Soldier Fly (BSF) diadakan di Area TPS 3R pada 25 Agustus 2026.", "2026-08-18", "Kegiatan", "Aktif", "Environmental Dept"),
                    Announcement("anc_3", "Pemisahan Sampah Residu & Limbah B3", "Dihimbau kepada seluruh unit kerja untuk tidak mencampurkan kain majun oli dan kaleng aerosol ke dalam setoran domestik TPS 3R.", "2026-08-12", "Peringatan", "Aktif", "K3L PT VALE IGP")
                )
                dao.insertAnnouncements(initialAnnouncements)

                // Seed Documentation Items
                val initialDocs = listOf(
                    DocumentationItem("doc_1", "Penyetoran Sampah Terpilah Unit Smelter", "2026-08-19", "TPS 3R Pomalaa", "Penyetoran", "Penimbangan berkala kardus dan botol plastik HDPE dari area pabrik smelter."),
                    DocumentationItem("doc_2", "Biokonversi Organik Bioreaktor Maggot BSF", "2026-08-18", "Unit Maggot TPS 3R", "Maggot", "Pemberian pakan limbah organik sayur kantin sebanyak 50 Kg ke biopond larva BSF fase prepupa."),
                    DocumentationItem("doc_3", "Press Baling Sampah Kardus & HDPE", "2026-08-17", "Gudang Pemilahan", "Pemilahan", "Pemadatan hidrolik kardus menjadi baling padat 100 Kg siap kirim ke industri daur ulang mitra."),
                    DocumentationItem("doc_4", "Housekeeping Lingkungan TPS 3R", "2026-08-16", "Area TPS 3R", "Housekeeping", "Pembersihan rutin bak penampungan dan disinfeksi area timbang limbah organik."),
                    DocumentationItem("doc_5", "Sosialisasi Pemilahan Sampah 5R Staff Vale", "2026-08-14", "Ruang Training IGP", "Sosialisasi", "Edukasi staf mengenai klasifikasi 8 jenis sampah pada aplikasi LAIKA SAMPAH.")
                )
                dao.insertDocumentations(initialDocs)

                // Seed Menu Settings
                val initialMenus = listOf(
                    MenuSetting("menu_1", "Dashboard", "dashboard", "Dashboard", "All", true, 1),
                    MenuSetting("menu_2", "Setor Sampah", "setor_sampah", "AddCircle", "Penyetor", true, 2),
                    MenuSetting("menu_3", "Scan QR", "scan_qr", "QrCodeScanner", "Penyetor", true, 3),
                    MenuSetting("menu_4", "Riwayat Setoran", "riwayat", "History", "Penyetor", true, 4),
                    MenuSetting("menu_5", "Chat & Diskusi", "chat", "Forum", "All", true, 5),
                    MenuSetting("menu_6", "Campaign & Info", "campaigns", "Campaign", "All", true, 6),
                    MenuSetting("menu_7", "Statistik", "statistik", "BarChart", "All", true, 7),
                    MenuSetting("menu_8", "Dokumentasi", "dokumentasi", "PhotoLibrary", "All", true, 8),
                    MenuSetting("menu_9", "Notifikasi", "notifikasi", "Notifications", "All", true, 9),
                    MenuSetting("menu_share", "Berbagi Antar HP", "share_hub", "Share", "All", true, 10),
                    MenuSetting("menu_10", "Profil", "profil", "Person", "All", true, 11),
                    MenuSetting("menu_11", "Data Pengguna", "admin_users", "People", "Admin", true, 11),
                    MenuSetting("menu_12", "Verifikasi Setoran", "admin_verifikasi", "FactCheck", "Admin", true, 12),
                    MenuSetting("menu_13", "Data Sampah", "admin_sampah", "Category", "Admin", true, 13),
                    MenuSetting("menu_14", "Laporan", "admin_laporan", "Assessment", "Admin", true, 14),
                    MenuSetting("menu_15", "Rekap Sampah", "admin_rekap", "TableChart", "Admin", true, 15),
                    MenuSetting("menu_16", "Pengumuman", "admin_pengumuman", "Campaign", "Admin", true, 16),
                    MenuSetting("menu_17", "Modul TPS 3R", "tps_modules", "Recycle", "All", true, 17),
                    MenuSetting("menu_18", "Manajemen Menu", "admin_menu", "MenuBook", "Admin", true, 18),
                    MenuSetting("menu_19", "Manajemen Fitur", "admin_fitur", "ToggleOn", "Admin", true, 19),
                    MenuSetting("menu_20", "Pengaturan", "pengaturan", "Settings", "All", true, 20)
                )
                dao.insertMenuSettings(initialMenus)

                // Seed Feature Settings
                val initialFeatures = listOf(
                    FeatureSetting("feat_1", "qr_scanner", "QR Scanner", "Pemindaian barcode lokasi dan identitas penyetor secara cepat", true),
                    FeatureSetting("feat_2", "upload_foto", "Upload Foto", "Lampiran foto sampah fisik saat penyetoran dan verifikasi", true),
                    FeatureSetting("feat_3", "chat_penyetor", "Chat & Diskusi Penyetor", "Fitur tanya jawab langsung antara penyetor dan Admin TPS 3R", true),
                    FeatureSetting("feat_4", "campaign_info", "Info Campaign & Kegiatan", "Publikasi dan pendaftaran kegiatan aksi lingkungan PT Vale", true),
                    FeatureSetting("feat_5", "notifikasi", "Notifikasi Realtime", "Pemberitahuan status verifikasi dan pengumuman", true),
                    FeatureSetting("feat_6", "statistik", "Statistik & Grafik", "Visualisasi tren dan perbandingan komposisi sampah", true),
                    FeatureSetting("feat_7", "laporan", "Laporan Berkala", "Laporan harian, mingguan, bulanan, dan tahunan", true),
                    FeatureSetting("feat_8", "export_excel", "Export Excel (.xlsx)", "Ekspor data transaksi ke spreadsheet Excel", true),
                    FeatureSetting("feat_9", "export_csv", "Export CSV", "Ekspor data mentah format CSV untuk sistem analitik", true),
                    FeatureSetting("feat_10", "dokumentasi", "Dokumentasi Foto", "Galeri foto kegiatan dan aksi lingkungan TPS 3R", true),
                    FeatureSetting("feat_11", "maggot_bsf", "Biokonversi Maggot BSF", "Pencatatan pengolahan sisa makanan menjadi larva pakan ternak", true),
                    FeatureSetting("feat_12", "kompos", "Kompos Organik", "Pencatatan produksi pupuk kompos dari dedaunan dan sisa organik", true)
                )
                dao.insertFeatureSettings(initialFeatures)

                // Seed Initial Chat Messages
                val initialChats = listOf(
                    ChatMessage(
                        id = "chat_1",
                        senderId = "user_penyetor_01",
                        senderName = "Ahmad Fauzi",
                        senderRole = "Penyetor",
                        senderDepartment = "Mining Operations IGP",
                        receiverId = "ADMIN_TPS3R",
                        receiverName = "Admin TPS 3R Vale",
                        message = "Selamat pagi Admin TPS 3R, untuk limbah drum jerigen HDPE dari workshop tambang apakah ada jadwal penjemputan khusus ke lokasi?",
                        timestamp = System.currentTimeMillis() - 7200000,
                        dateStr = "2026-08-19",
                        timeStr = "08:10",
                        isRead = true,
                        quickTopic = "Jadwal"
                    ),
                    ChatMessage(
                        id = "chat_2",
                        senderId = "user_admin_01",
                        senderName = "Admin TPS 3R Vale",
                        senderRole = "Admin",
                        senderDepartment = "Environmental & Waste Dept",
                        receiverId = "user_penyetor_01",
                        receiverName = "Ahmad Fauzi",
                        message = "Pagi Pak Ahmad. Untuk jerigen HDPE dalam jumlah >20 Kg, armada pick-up TPS 3R siap meluncur pukul 10.30 WITA. Mohon disiapkan di drop point Blok B.",
                        timestamp = System.currentTimeMillis() - 5400000,
                        dateStr = "2026-08-19",
                        timeStr = "08:25",
                        isRead = true,
                        quickTopic = "Jadwal"
                    ),
                    ChatMessage(
                        id = "chat_3",
                        senderId = "user_penyetor_01",
                        senderName = "Ahmad Fauzi",
                        senderRole = "Penyetor",
                        senderDepartment = "Mining Operations IGP",
                        receiverId = "ADMIN_TPS3R",
                        receiverName = "Admin TPS 3R Vale",
                        message = "Siap terima kasih infonya Pak. Sudah kami pilah dan bersihkan dari sisa cairan.",
                        timestamp = System.currentTimeMillis() - 3600000,
                        dateStr = "2026-08-19",
                        timeStr = "08:35",
                        isRead = true,
                        quickTopic = "Jenis Sampah"
                    ),
                    ChatMessage(
                        id = "chat_4",
                        senderId = "user_penyetor_02",
                        senderName = "Siti Nurhaliza",
                        senderRole = "Penyetor",
                        senderDepartment = "HR & General Affairs",
                        receiverId = "ADMIN_TPS3R",
                        receiverName = "Admin TPS 3R Vale",
                        message = "Halo Tim TPS 3R, untuk kertas dokumen arsip kantor apakah harus dihancurkan (shredder) dulu sebelum disetor?",
                        timestamp = System.currentTimeMillis() - 1800000,
                        dateStr = "2026-08-19",
                        timeStr = "09:05",
                        isRead = false,
                        quickTopic = "Jenis Sampah"
                    )
                )
                dao.insertChatMessages(initialChats)

                // Seed Initial Campaigns
                val initialCampaigns = listOf(
                    Campaign(
                        id = "camp_1",
                        title = "Gerakan Pilah Sampah Dari Rumah & Mess Pomalaa",
                        category = "Edukasi Pemilahan",
                        organizer = "PT VALE IGP Pomalaa • TPS 3R Terpadu",
                        startDate = "2026-08-15",
                        endDate = "2026-08-31",
                        time = "08:00 - 16:00 WITA",
                        location = "Kawasan Mess IGP & Perumahan Karyawan Pomalaa",
                        description = "Aksi bersama memilah sampah dari sumbernya! Kumpulkan minimal 10 Kg sampah plastik (PET/HDPE) atau kardus bersih dari lingkungan mess/kantor dan tukarkan dengan merchandise eksklusif ramah lingkungan PT Vale.",
                        targetParticipants = 150,
                        registeredCount = 88,
                        rewardPoints = 100,
                        status = "Sedang Berlangsung",
                        contactPerson = "Divisi Environment PT Vale (0812-3456-7890)",
                        registeredUserIds = "user_penyetor_01,user_penyetor_02"
                    ),
                    Campaign(
                        id = "camp_2",
                        title = "Workshop Biokonversi Maggot BSF & Komposting Organik",
                        category = "Pelatihan TPS 3R",
                        organizer = "Departemen Environment & CSR PT Vale",
                        startDate = "2026-08-25",
                        endDate = "2026-08-25",
                        time = "09:00 - 13:00 WITA",
                        location = "Fasilitas Biokonversi TPS 3R Vale Pomalaa",
                        description = "Pelatihan praktis cara budidaya larva Black Soldier Fly (BSF) untuk mereduksi 100% limbah sisa makanan kantin menjadi pakan ternak berprotein tinggi dan kasgot (pupuk organik padat).",
                        targetParticipants = 50,
                        registeredCount = 42,
                        rewardPoints = 75,
                        status = "Akan Datang",
                        contactPerson = "Koordinator TPS 3R Vale (0821-9876-5432)",
                        registeredUserIds = "user_penyetor_01"
                    ),
                    Campaign(
                        id = "camp_3",
                        title = "Aksi Bersih Pesisir Pantai & Penanaman 1.000 Mangrove Kolaka",
                        category = "Aksi Bersih",
                        organizer = "PT VALE Indonesia Tbk • Relawan Vale Peduli",
                        startDate = "2026-09-05",
                        endDate = "2026-09-05",
                        time = "06:30 - 11:30 WITA",
                        location = "Pesisir Pantai Desa Hakatutobu & Teluk Pomalaa",
                        description = "Kolaborasi kepedulian ekosistem pesisir Pomalaa! Membersihkan sampah anorganik di garis pantai serta menanam 1.000 bibit mangrove pelindung abrasi. Sarapan, perlengkapan safety, dan sertifikat partisipasi disediakan.",
                        targetParticipants = 200,
                        registeredCount = 135,
                        rewardPoints = 150,
                        status = "Akan Datang",
                        contactPerson = "Tim CSR & Keanekaragaman Hayati Vale (0852-1122-3344)",
                        registeredUserIds = "user_penyetor_02,user_penyetor_03"
                    ),
                    Campaign(
                        id = "camp_4",
                        title = "Lomba Inovasi Penyetor Teraktif PT Vale IGP 2026",
                        category = "Lomba & Reward",
                        organizer = "Panitia Bulan Lingkungan Hidup PT Vale IGP",
                        startDate = "2026-08-01",
                        endDate = "2026-08-31",
                        time = "Sepanjang Bulan Agustus 2026",
                        location = "Seluruh Area Kerja PT VALE IGP Pomalaa",
                        description = "Kompetisi pemilahan dan akumulasi setoran sampah terpilah tertinggi antar unit kerja / departemen. Raih piala bergilir Direksi PT Vale, voucher belanja ramah lingkungan, dan dana pembinaan 5R.",
                        targetParticipants = 300,
                        registeredCount = 210,
                        rewardPoints = 200,
                        status = "Sedang Berlangsung",
                        contactPerson = "Sekretariat K3L PT Vale IGP (0813-7788-9900)",
                        registeredUserIds = "user_penyetor_01,user_penyetor_02,user_penyetor_03"
                    )
                )
                dao.insertCampaigns(initialCampaigns)
            }
        }
    }

    suspend fun getFullSyncPayload(operatorName: String): LaikaSyncPayload {
        val userList = dao.getAllUsers().firstOrNull() ?: emptyList()
        val depositList = dao.getAllDeposits().firstOrNull() ?: emptyList()
        val typeList = dao.getAllWasteTypes().firstOrNull() ?: emptyList()
        val ancList = dao.getAllAnnouncements().firstOrNull() ?: emptyList()
        val docList = dao.getAllDocumentation().firstOrNull() ?: emptyList()
        val campList = dao.getAllCampaigns().firstOrNull() ?: emptyList()
        val nowFormatted = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        return LaikaSyncPayload(
            version = 1,
            appName = "LAIKA SAMPAH",
            exportedAt = nowFormatted,
            exportedBy = operatorName,
            users = userList,
            deposits = depositList,
            wasteTypes = typeList,
            announcements = ancList,
            documentation = docList,
            campaigns = campList
        )
    }

    suspend fun importSyncPayload(payload: LaikaSyncPayload, overwrite: Boolean): Triple<Int, Int, Int> {
        if (overwrite) {
            dao.deleteAllDeposits()
            dao.deleteAllUsers()
            dao.deleteAllWasteTypes()
            dao.deleteAllAnnouncements()
            dao.deleteAllDocumentation()
            dao.deleteAllCampaigns()
        }

        if (payload.users.isNotEmpty()) dao.insertUsers(payload.users)
        if (payload.deposits.isNotEmpty()) dao.insertDeposits(payload.deposits)
        if (payload.wasteTypes.isNotEmpty()) dao.insertWasteTypes(payload.wasteTypes)
        if (payload.announcements.isNotEmpty()) dao.insertAnnouncements(payload.announcements)
        if (payload.documentation.isNotEmpty()) dao.insertDocumentations(payload.documentation)
        if (payload.campaigns.isNotEmpty()) dao.insertCampaigns(payload.campaigns)

        return Triple(payload.deposits.size, payload.users.size, payload.wasteTypes.size)
    }
}
