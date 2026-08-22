package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MenuSetting
import com.example.data.model.User
import com.example.ui.theme.EcoOrange
import com.example.ui.theme.EcoRed
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.LightMint

@Composable
fun LaikaSidebarContent(
    currentUser: User?,
    currentRoute: String,
    menuSettings: List<MenuSetting>,
    pendingVerificationCount: Int,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Surface(
        modifier = modifier
            .fillMaxHeight()
            .width(280.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Sidebar Header with Official Logos
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Laika3DLogo(size = 46.dp)
                    PtValeLogo(height = 28.dp)
                }

                Column {
                    Text(
                        text = "LAIKA SAMPAH",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "PT VALE IGP POMALAA",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldGreen
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // User Info Pill
            if (currentUser != null) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (currentUser.role == "Admin") EcoOrange else EmeraldGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentUser.name.take(1).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentUser.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Text(
                                text = "${currentUser.role} • ${currentUser.department}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Menus List
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val isUserAdmin = currentUser?.role == "Admin"

                // Filter menu items by role and whether they are enabled
                val visibleMenus = menuSettings
                    .filter { it.isEnabled }
                    .filter { menu ->
                        when (menu.role) {
                            "Admin" -> isUserAdmin
                            "Penyetor" -> !isUserAdmin
                            else -> true
                        }
                    }
                    .sortedBy { it.orderIndex }

                // If menu settings is empty (e.g. before seed), fallback to defaults
                val finalMenuList = if (visibleMenus.isNotEmpty()) {
                    visibleMenus
                } else {
                    if (isUserAdmin) {
                        listOf(
                            MenuSetting("m1", "Dashboard", "dashboard", "Dashboard", "Admin", true, 1),
                            MenuSetting("m2", "Data Pengguna", "admin_users", "People", "Admin", true, 2),
                            MenuSetting("m3", "Verifikasi Setoran", "admin_verifikasi", "FactCheck", "Admin", true, 3),
                            MenuSetting("m4", "Data Sampah", "admin_sampah", "Category", "Admin", true, 4),
                            MenuSetting("m5", "Dokumentasi", "dokumentasi", "PhotoLibrary", "Admin", true, 5),
                            MenuSetting("m6", "Laporan", "admin_laporan", "Assessment", "Admin", true, 6),
                            MenuSetting("m7", "Rekap Sampah", "admin_rekap", "TableChart", "Admin", true, 7),
                            MenuSetting("m8", "Statistik", "statistik", "BarChart", "Admin", true, 8),
                            MenuSetting("m9", "Pengumuman", "admin_pengumuman", "Campaign", "Admin", true, 9),
                            MenuSetting("m10", "Modul TPS 3R", "tps_modules", "Recycle", "Admin", true, 10),
                            MenuSetting("m11", "Manajemen Menu", "admin_menu", "MenuBook", "Admin", true, 11),
                            MenuSetting("m12", "Manajemen Fitur", "admin_fitur", "ToggleOn", "Admin", true, 12),
                            MenuSetting("m13", "Notifikasi", "notifikasi", "Notifications", "Admin", true, 13),
                            MenuSetting("m_share", "Berbagi Antar HP", "share_hub", "Share", "Admin", true, 14),
                            MenuSetting("m14", "Profil", "profil", "Person", "Admin", true, 15),
                            MenuSetting("m15", "Pengaturan", "pengaturan", "Settings", "Admin", true, 16)
                        )
                    } else {
                        listOf(
                            MenuSetting("m1", "Dashboard", "dashboard", "Dashboard", "Penyetor", true, 1),
                            MenuSetting("m2", "Setor Sampah", "setor_sampah", "AddCircle", "Penyetor", true, 2),
                            MenuSetting("m3", "Scan QR", "scan_qr", "QrCodeScanner", "Penyetor", true, 3),
                            MenuSetting("m4", "Riwayat Setoran", "riwayat", "History", "Penyetor", true, 4),
                            MenuSetting("m5", "Statistik", "statistik", "BarChart", "Penyetor", true, 5),
                            MenuSetting("m6", "Dokumentasi", "dokumentasi", "PhotoLibrary", "Penyetor", true, 6),
                            MenuSetting("m7", "Modul TPS 3R", "tps_modules", "Recycle", "Penyetor", true, 7),
                            MenuSetting("m8", "Notifikasi", "notifikasi", "Notifications", "Penyetor", true, 8),
                            MenuSetting("m_share", "Berbagi Antar HP", "share_hub", "Share", "Penyetor", true, 9),
                            MenuSetting("m9", "Profil", "profil", "Person", "Penyetor", true, 10),
                            MenuSetting("m10", "Pengaturan", "pengaturan", "Settings", "Penyetor", true, 11)
                        )
                    }
                }

                finalMenuList.forEach { menu ->
                    val isSelected = currentRoute == menu.route
                    val icon = getIconForName(menu.iconName)
                    val isVerifikasiMenu = menu.route == "admin_verifikasi"

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onNavigate(menu.route) }
                            .testTag("nav_menu_${menu.route}"),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = menu.name,
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )

                            Text(
                                text = menu.name,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )

                            if (isVerifikasiMenu && pendingVerificationCount > 0) {
                                Surface(
                                    color = EcoOrange,
                                    shape = CircleShape
                                ) {
                                    Text(
                                        text = pendingVerificationCount.toString(),
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Logout Button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onLogout() }
                    .testTag("sidebar_logout_button"),
                color = Color(0xFFF8D7DA).copy(alpha = 0.6f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Logout",
                        tint = EcoRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Keluar (Logout)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = EcoRed
                    )
                }
            }
        }
    }
}

fun getIconForName(iconName: String): ImageVector {
    return when (iconName.lowercase()) {
        "dashboard" -> Icons.Default.Dashboard
        "addcircle", "setor" -> Icons.Default.AddCircle
        "qrcodescanner", "qr" -> Icons.Default.QrCodeScanner
        "history", "riwayat" -> Icons.Default.History
        "barchart", "chart", "statistik" -> Icons.Default.BarChart
        "photolibrary", "dokumentasi", "image" -> Icons.Default.PhotoLibrary
        "notifications", "notif" -> Icons.Default.Notifications
        "person", "profil", "user" -> Icons.Default.Person
        "settings", "pengaturan" -> Icons.Default.Settings
        "people", "users" -> Icons.Default.People
        "factcheck", "verifikasi" -> Icons.Default.FactCheck
        "category", "sampah" -> Icons.Default.Category
        "assessment", "laporan" -> Icons.Default.Assessment
        "tablechart", "rekap" -> Icons.Default.TableChart
        "campaign", "pengumuman", "campaigns" -> Icons.Default.Campaign
        "forum", "chat", "diskusi" -> Icons.Default.Forum
        "share", "share_hub", "berbagi" -> Icons.Default.Share
        "event", "kegiatan" -> Icons.Default.Event
        "menubook", "menu" -> Icons.Default.MenuBook
        "toggleon", "fitur" -> Icons.Default.ToggleOn
        "recycle", "tps" -> Icons.Default.Eco
        else -> Icons.AutoMirrored.Filled.List
    }
}
