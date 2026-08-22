package com.example.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import java.util.concurrent.atomic.AtomicInteger

object NotificationHelper {

    const val CHANNEL_DEPOSITS = "channel_laika_deposits"
    const val CHANNEL_CAMPAIGNS = "channel_laika_campaigns"
    const val CHANNEL_CHATS = "channel_laika_chats"
    const val CHANNEL_GENERAL = "channel_laika_general"

    private val notificationIdCounter = AtomicInteger(100)

    fun initChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

            // Channel 1: Penyetoran Sampah (Deposit)
            val depositChannel = NotificationChannel(
                CHANNEL_DEPOSITS,
                "Penyetoran Sampah Terpadu",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi status penyetoran, timbangan, dan verifikasi sampah TPS 3R"
                enableLights(true)
                lightColor = Color.parseColor("#006C47")
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 200, 300)
                setShowBadge(true)
            }

            // Channel 2: Campaign & Kegiatan Lingkungan
            val campaignChannel = NotificationChannel(
                CHANNEL_CAMPAIGNS,
                "Info Campaign & Kegiatan",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Pengumuman kegiatan lingkungan, aksi bersih, dan campaign PT Vale"
                enableLights(true)
                lightColor = Color.parseColor("#2E7D32")
                enableVibration(true)
                setShowBadge(true)
            }

            // Channel 3: Obrolan / Chat Penyetor & Admin
            val chatChannel = NotificationChannel(
                CHANNEL_CHATS,
                "Obrolan Penyetor & Admin",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi pesan langsung antara penyetor sampah dan admin TPS 3R"
                enableLights(true)
                lightColor = Color.parseColor("#0288D1")
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 200, 150, 200)
                setShowBadge(true)
            }

            // Channel 4: Informasi Umum
            val generalChannel = NotificationChannel(
                CHANNEL_GENERAL,
                "Informasi Sistem Laika Sampah",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Pemberitahuan sistem, tips pemilahan, dan poin reward"
                enableLights(true)
                lightColor = Color.parseColor("#006C47")
            }

            notificationManager.createNotificationChannels(
                listOf(depositChannel, campaignChannel, chatChannel, generalChannel)
            )
        }
    }

    private fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun createPendingIntent(context: Context, route: String, id: Int): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("target_route", route)
        }
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        return PendingIntent.getActivity(context, id, intent, flags)
    }

    /**
     * Notifikasi Penyetoran Sampah (Dikirim ke Penyetor / Admin saat ada setoran baru atau verifikasi)
     */
    fun showDepositNotification(
        context: Context,
        title: String,
        message: String,
        targetRoute: String = "riwayat"
    ) {
        initChannels(context)
        if (!hasNotificationPermission(context)) return

        val id = notificationIdCounter.incrementAndGet()
        val pendingIntent = createPendingIntent(context, targetRoute, id)

        val builder = NotificationCompat.Builder(context, CHANNEL_DEPOSITS)
            .setSmallIcon(R.drawable.ic_stat_laika)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setColor(Color.parseColor("#006C47"))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setCategory(NotificationCompat.CATEGORY_STATUS)

        try {
            NotificationManagerCompat.from(context).notify(id, builder.build())
        } catch (_: SecurityException) {
            // Permission not granted on runtime
        }
    }

    /**
     * Notifikasi Info Campaign & Kegiatan Lingkungan PT Vale
     */
    fun showCampaignNotification(
        context: Context,
        title: String,
        message: String,
        targetRoute: String = "campaigns"
    ) {
        initChannels(context)
        if (!hasNotificationPermission(context)) return

        val id = notificationIdCounter.incrementAndGet()
        val pendingIntent = createPendingIntent(context, targetRoute, id)

        val builder = NotificationCompat.Builder(context, CHANNEL_CAMPAIGNS)
            .setSmallIcon(R.drawable.ic_stat_laika)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setColor(Color.parseColor("#2E7D32"))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setCategory(NotificationCompat.CATEGORY_EVENT)

        try {
            NotificationManagerCompat.from(context).notify(id, builder.build())
        } catch (_: SecurityException) {
            // Permission not granted on runtime
        }
    }

    /**
     * Notifikasi Obrolan / Chat Langsung antara Admin dan Penyetor
     */
    fun showChatNotification(
        context: Context,
        senderName: String,
        message: String,
        targetRoute: String = "chat"
    ) {
        initChannels(context)
        if (!hasNotificationPermission(context)) return

        val id = notificationIdCounter.incrementAndGet()
        val pendingIntent = createPendingIntent(context, targetRoute, id)

        val builder = NotificationCompat.Builder(context, CHANNEL_CHATS)
            .setSmallIcon(R.drawable.ic_stat_laika)
            .setContentTitle("💬 Pesan dari $senderName")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setColor(Color.parseColor("#0288D1"))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)

        try {
            NotificationManagerCompat.from(context).notify(id, builder.build())
        } catch (_: SecurityException) {
            // Permission not granted on runtime
        }
    }

    /**
     * Notifikasi Umum / Sistem
     */
    fun showGeneralNotification(
        context: Context,
        title: String,
        message: String,
        targetRoute: String = "dashboard"
    ) {
        initChannels(context)
        if (!hasNotificationPermission(context)) return

        val id = notificationIdCounter.incrementAndGet()
        val pendingIntent = createPendingIntent(context, targetRoute, id)

        val builder = NotificationCompat.Builder(context, CHANNEL_GENERAL)
            .setSmallIcon(R.drawable.ic_stat_laika)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setColor(Color.parseColor("#006C47"))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        try {
            NotificationManagerCompat.from(context).notify(id, builder.build())
        } catch (_: SecurityException) {
            // Permission not granted on runtime
        }
    }
}
