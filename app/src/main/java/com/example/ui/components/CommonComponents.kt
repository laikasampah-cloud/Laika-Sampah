package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaikaTopHeader(
    currentUser: User?,
    currentRoute: String = "dashboard",
    unreadNotifCount: Int,
    onMenuClick: () -> Unit,
    onBackClick: () -> Unit = {},
    onNotifClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var showProfileMenu by remember { mutableStateOf(false) }
    val isDashboard = currentRoute == "dashboard"

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Navigation Icon: Back to dashboard when on inner screen, or Menu when on dashboard
                if (!isDashboard) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("back_to_dashboard_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali ke Dashboard",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier.testTag("menu_toggle_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.clickable { onBackClick() }
                ) {
                    Laika3DLogo(size = 38.dp)

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "LAIKA",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "SAMPAH",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = EmeraldGreen,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Text(
                            text = if (!isDashboard) "← Kembali ke Dashboard" else "PT VALE IGP POMALAA",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!isDashboard) EmeraldGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            // Right side: Notif, User Badge, Profile Dropdown
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = onNotifClick,
                    modifier = Modifier.testTag("notification_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadNotifCount > 0) {
                                Badge(
                                    containerColor = EcoRed,
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        text = if (unreadNotifCount > 9) "9+" else unreadNotifCount.toString(),
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifikasi",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                if (currentUser != null) {
                    Box {
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { showProfileMenu = true }
                                .testTag("profile_dropdown_trigger"),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (currentUser.role == "Admin") EcoOlive else EmeraldGreen
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = currentUser.name.take(1).uppercase(),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }

                                Column(modifier = Modifier.widthIn(max = 110.dp)) {
                                    Text(
                                        text = currentUser.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = currentUser.role,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (currentUser.role == "Admin") EcoOrange else EmeraldGreen
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showProfileMenu,
                            onDismissRequest = { showProfileMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Profil Saya") },
                                onClick = {
                                    showProfileMenu = false
                                    onProfileClick()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = EmeraldGreen)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Berbagi ke HP Lain") },
                                onClick = {
                                    showProfileMenu = false
                                    onSettingsClick() // can navigate via settings or directly
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Share, contentDescription = null, tint = ForestGreenPrimary)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Pengaturan") },
                                onClick = {
                                    showProfileMenu = false
                                    onSettingsClick()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Settings, contentDescription = null, tint = TextSecondary)
                                }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Keluar (Logout)", color = EcoRed) },
                                onClick = {
                                    showProfileMenu = false
                                    onLogoutClick()
                                },
                                leadingIcon = {
                                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = EcoRed)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LaikaFooter(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "♻️ LAIKA SAMPAH",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "•",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "PT VALE IGP POMALAA",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = EmeraldGreen
                )
            }
            Text(
                text = "Digital Waste Management System • TPS 3R Integrated Platform",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Text(
                text = "© 2026 LAIKA SAMPAH • \"Kelola Sampah, Jaga Lingkungan\"",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Text(
                    text = value,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (subtitle.isNotBlank()) {
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = accentColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (bg, fg, icon) = when (status.lowercase()) {
        "diverifikasi" -> Triple(LightMint, ForestGreenPrimary, Icons.Default.CheckCircle)
        "ditolak" -> Triple(Color(0xFFF8D7DA), EcoRed, Icons.Default.Cancel)
        else -> Triple(Color(0xFFFFF3CD), Color(0xFF664D03), Icons.Default.Schedule)
    }

    Surface(
        color = bg,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = fg,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = status,
                color = fg,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ToastOverlay(
    message: String?,
    onDismiss: () -> Unit
) {
    LaunchedEffect(message) {
        if (message != null) {
            delay(3200)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = message != null,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        if (message != null) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = ForestGreenPrimary,
                shadowElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = LightMint
                    )
                    Text(
                        text = message,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleBarChart(
    data: List<Pair<String, Double>>,
    modifier: Modifier = Modifier,
    barColor: Color = EmeraldGreen
) {
    val maxVal = (data.maxOfOrNull { it.second } ?: 1.0).coerceAtLeast(1.0)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            data.forEach { item ->
                val ratio = (item.second / maxVal).toFloat().coerceIn(0.05f, 1f)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${item.second.toInt()}k",
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .fillMaxHeight(ratio)
                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(barColor, barColor.copy(alpha = 0.6f))
                                )
                            )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = item.first,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun WastePieDonutChart(
    breakdown: List<Triple<String, Double, Color>>,
    modifier: Modifier = Modifier
) {
    val total = breakdown.sumOf { it.second }.coerceAtLeast(0.001)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier.size(110.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(100.dp)) {
                var startAngle = -90f
                val strokeWidth = 24.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2
                val centerOffset = Offset(size.width / 2, size.height / 2)

                breakdown.forEach { item ->
                    val sweepAngle = ((item.second / total) * 360f).toFloat()
                    if (sweepAngle > 0.5f) {
                        drawArc(
                            color = item.third,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle - 2f,
                            useCenter = false,
                            topLeft = Offset(centerOffset.x - radius, centerOffset.y - radius),
                            size = Size(radius * 2, radius * 2),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }
                    startAngle += sweepAngle
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = String.format(Locale.US, "%.0f", total),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Kg Total",
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            breakdown.take(5).forEach { item ->
                val pct = (item.second / total) * 100
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(item.third)
                        )
                        Text(
                            text = item.first,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = "${String.format(Locale.US, "%.1f", item.second)} Kg (${pct.toInt()}%)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
