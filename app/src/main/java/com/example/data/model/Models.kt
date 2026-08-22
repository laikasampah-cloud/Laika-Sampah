package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    val name: String,
    val username: String,
    val phone: String,
    val password: String,
    val role: String, // "Admin" or "Penyetor"
    val department: String,
    val address: String,
    val photo: String = "",
    val status: String = "Aktif", // "Aktif", "Nonaktif"
    val createdAt: String
)

@Entity(tableName = "waste_deposits")
data class WasteDeposit(
    @PrimaryKey val id: String,
    val userId: String,
    val userName: String,
    val userDepartment: String,
    val date: String, // YYYY-MM-DD
    val time: String, // HH:mm
    val location: String,
    val wasteType: String, // Organik, HDPE, PET, Kardus, Paper Box, Kertas, Residu, E-Waste
    val weight: Double, // in Kg
    val photo: String = "",
    val notes: String = "",
    val status: String = "Pending", // "Pending", "Diverifikasi", "Ditolak"
    val verifiedBy: String = "",
    val verifiedAt: String = "",
    val rejectionReason: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "waste_types")
data class WasteType(
    @PrimaryKey val id: String,
    val name: String,
    val code: String,
    val category: String,
    val colorHex: String,
    val iconName: String,
    val isActive: Boolean = true,
    val description: String = ""
)

@Entity(tableName = "notifications")
data class AppNotification(
    @PrimaryKey val id: String,
    val userId: String, // specific userId or "ALL"
    val title: String,
    val message: String,
    val date: String,
    val time: String,
    val isRead: Boolean = false,
    val type: String = "info" // deposit, announcement, alert
)

@Entity(tableName = "announcements")
data class Announcement(
    @PrimaryKey val id: String,
    val title: String,
    val content: String,
    val date: String,
    val category: String, // Informasi, Kegiatan, Sosialisasi, Peringatan, Lingkungan
    val status: String = "Aktif",
    val author: String = "Admin TPS 3R"
)

@Entity(tableName = "documents")
data class DocumentationItem(
    @PrimaryKey val id: String,
    val title: String,
    val date: String,
    val location: String,
    val category: String, // Penyetoran, Pemilahan, TPS 3R, Maggot, HDPE, PET, Housekeeping, Sosialisasi, Kegiatan Lingkungan
    val description: String,
    val photo: String = "",
    val author: String = ""
)

@Entity(tableName = "menu_settings")
data class MenuSetting(
    @PrimaryKey val id: String,
    val name: String,
    val route: String,
    val iconName: String,
    val role: String, // "Admin", "Penyetor", "All"
    val isEnabled: Boolean = true,
    val orderIndex: Int
)

@Entity(tableName = "feature_settings")
data class FeatureSetting(
    @PrimaryKey val id: String,
    val key: String,
    val name: String,
    val description: String,
    val isEnabled: Boolean = true
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey val id: String,
    val senderId: String,
    val senderName: String,
    val senderRole: String, // "Penyetor" or "Admin"
    val senderDepartment: String = "",
    val receiverId: String, // specific userId or "ADMIN_TPS3R" or "BROADCAST"
    val receiverName: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val dateStr: String,
    val timeStr: String,
    val isRead: Boolean = false,
    val quickTopic: String = "" // "Jadwal", "Jenis Sampah", "Status Setoran", "Poin", "Umum"
)

@Entity(tableName = "campaigns")
data class Campaign(
    @PrimaryKey val id: String,
    val title: String,
    val category: String, // "Penghijauan", "Edukasi Pemilahan", "Lomba & Reward", "Aksi Bersih", "Pelatihan TPS 3R"
    val organizer: String, // "PT VALE IGP Pomalaa", "TPS 3R Pomalaa", "Departemen Environment Vale"
    val startDate: String,
    val endDate: String,
    val time: String = "08:00 - 12:00 WITA",
    val location: String,
    val description: String,
    val targetParticipants: Int = 100,
    val registeredCount: Int = 0,
    val rewardPoints: Int = 50,
    val status: String = "Sedang Berlangsung", // "Akan Datang", "Sedang Berlangsung", "Selesai"
    val contactPerson: String = "Tim Environment PT Vale / TPS 3R (0812-4455-6677)",
    val registeredUserIds: String = "" // comma-separated user IDs
)

data class WasteSummary(
    val wasteType: String,
    val totalKg: Double,
    val transactionCount: Int,
    val percentage: Double = 0.0,
    val colorHex: String = "#198754"
)

data class DailyChartData(
    val dayLabel: String,
    val totalKg: Double
)

data class LaikaSyncPayload(
    val version: Int = 1,
    val appName: String = "LAIKA SAMPAH",
    val exportedAt: String = "",
    val exportedBy: String = "",
    val users: List<User> = emptyList(),
    val deposits: List<WasteDeposit> = emptyList(),
    val wasteTypes: List<WasteType> = emptyList(),
    val announcements: List<Announcement> = emptyList(),
    val documentation: List<DocumentationItem> = emptyList(),
    val campaigns: List<Campaign> = emptyList()
)
