package com.example.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.model.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DataTransferHelper {

    /**
     * Serialize LaikaSyncPayload into a clean JSON string
     */
    fun serializePayloadToJson(payload: LaikaSyncPayload): String {
        val root = JSONObject()
        root.put("version", payload.version)
        root.put("appName", payload.appName)
        root.put("exportedAt", payload.exportedAt)
        root.put("exportedBy", payload.exportedBy)

        // Users
        val usersArr = JSONArray()
        payload.users.forEach { u ->
            val uObj = JSONObject()
            uObj.put("id", u.id)
            uObj.put("name", u.name)
            uObj.put("username", u.username)
            uObj.put("phone", u.phone)
            uObj.put("password", u.password)
            uObj.put("role", u.role)
            uObj.put("department", u.department)
            uObj.put("address", u.address)
            uObj.put("photo", u.photo)
            uObj.put("status", u.status)
            uObj.put("createdAt", u.createdAt)
            usersArr.put(uObj)
        }
        root.put("users", usersArr)

        // Deposits
        val depositsArr = JSONArray()
        payload.deposits.forEach { d ->
            val dObj = JSONObject()
            dObj.put("id", d.id)
            dObj.put("userId", d.userId)
            dObj.put("userName", d.userName)
            dObj.put("userDepartment", d.userDepartment)
            dObj.put("date", d.date)
            dObj.put("time", d.time)
            dObj.put("location", d.location)
            dObj.put("wasteType", d.wasteType)
            dObj.put("weight", d.weight)
            dObj.put("photo", d.photo)
            dObj.put("notes", d.notes)
            dObj.put("status", d.status)
            dObj.put("verifiedBy", d.verifiedBy)
            dObj.put("verifiedAt", d.verifiedAt)
            dObj.put("rejectionReason", d.rejectionReason)
            dObj.put("createdAt", d.createdAt)
            depositsArr.put(dObj)
        }
        root.put("deposits", depositsArr)

        // Waste Types
        val typesArr = JSONArray()
        payload.wasteTypes.forEach { wt ->
            val wtObj = JSONObject()
            wtObj.put("id", wt.id)
            wtObj.put("name", wt.name)
            wtObj.put("code", wt.code)
            wtObj.put("category", wt.category)
            wtObj.put("colorHex", wt.colorHex)
            wtObj.put("iconName", wt.iconName)
            wtObj.put("isActive", wt.isActive)
            wtObj.put("description", wt.description)
            typesArr.put(wtObj)
        }
        root.put("wasteTypes", typesArr)

        // Announcements
        val ancArr = JSONArray()
        payload.announcements.forEach { a ->
            val aObj = JSONObject()
            aObj.put("id", a.id)
            aObj.put("title", a.title)
            aObj.put("content", a.content)
            aObj.put("date", a.date)
            aObj.put("category", a.category)
            aObj.put("status", a.status)
            aObj.put("author", a.author)
            ancArr.put(aObj)
        }
        root.put("announcements", ancArr)

        // Documentations
        val docArr = JSONArray()
        payload.documentation.forEach { doc ->
            val docObj = JSONObject()
            docObj.put("id", doc.id)
            docObj.put("title", doc.title)
            docObj.put("date", doc.date)
            docObj.put("location", doc.location)
            docObj.put("category", doc.category)
            docObj.put("description", doc.description)
            docObj.put("photo", doc.photo)
            docObj.put("author", doc.author)
            docArr.put(docObj)
        }
        root.put("documentation", docArr)

        // Campaigns
        val campArr = JSONArray()
        payload.campaigns.forEach { c ->
            val cObj = JSONObject()
            cObj.put("id", c.id)
            cObj.put("title", c.title)
            cObj.put("category", c.category)
            cObj.put("organizer", c.organizer)
            cObj.put("startDate", c.startDate)
            cObj.put("endDate", c.endDate)
            cObj.put("time", c.time)
            cObj.put("location", c.location)
            cObj.put("description", c.description)
            cObj.put("targetParticipants", c.targetParticipants)
            cObj.put("registeredCount", c.registeredCount)
            cObj.put("rewardPoints", c.rewardPoints)
            cObj.put("status", c.status)
            cObj.put("contactPerson", c.contactPerson)
            cObj.put("registeredUserIds", c.registeredUserIds)
            campArr.put(cObj)
        }
        root.put("campaigns", campArr)

        return root.toString(2)
    }

    /**
     * Parse JSON string back into LaikaSyncPayload
     */
    fun parseJsonToPayload(jsonStr: String): LaikaSyncPayload? {
        return try {
            val root = JSONObject(jsonStr)
            val version = root.optInt("version", 1)
            val appName = root.optString("appName", "LAIKA SAMPAH")
            val exportedAt = root.optString("exportedAt", "")
            val exportedBy = root.optString("exportedBy", "")

            val users = mutableListOf<User>()
            val usersArr = root.optJSONArray("users")
            if (usersArr != null) {
                for (i in 0 until usersArr.length()) {
                    val o = usersArr.getJSONObject(i)
                    users.add(
                        User(
                            id = o.optString("id", "u_${System.currentTimeMillis()}_$i"),
                            name = o.optString("name", "Pengguna"),
                            username = o.optString("username", "user_$i"),
                            phone = o.optString("phone", ""),
                            password = o.optString("password", "123456"),
                            role = o.optString("role", "Penyetor"),
                            department = o.optString("department", "Umum"),
                            address = o.optString("address", ""),
                            photo = o.optString("photo", ""),
                            status = o.optString("status", "Aktif"),
                            createdAt = o.optString("createdAt", "2026-01-01")
                        )
                    )
                }
            }

            val deposits = mutableListOf<WasteDeposit>()
            val depositsArr = root.optJSONArray("deposits")
            if (depositsArr != null) {
                for (i in 0 until depositsArr.length()) {
                    val o = depositsArr.getJSONObject(i)
                    deposits.add(
                        WasteDeposit(
                            id = o.optString("id", "dep_${System.currentTimeMillis()}_$i"),
                            userId = o.optString("userId", ""),
                            userName = o.optString("userName", ""),
                            userDepartment = o.optString("userDepartment", ""),
                            date = o.optString("date", "2026-08-19"),
                            time = o.optString("time", "12:00"),
                            location = o.optString("location", "TPS 3R Pomalaa"),
                            wasteType = o.optString("wasteType", "Organik"),
                            weight = o.optDouble("weight", 0.0),
                            photo = o.optString("photo", ""),
                            notes = o.optString("notes", ""),
                            status = o.optString("status", "Pending"),
                            verifiedBy = o.optString("verifiedBy", ""),
                            verifiedAt = o.optString("verifiedAt", ""),
                            rejectionReason = o.optString("rejectionReason", ""),
                            createdAt = o.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                }
            }

            val wasteTypes = mutableListOf<WasteType>()
            val typesArr = root.optJSONArray("wasteTypes")
            if (typesArr != null) {
                for (i in 0 until typesArr.length()) {
                    val o = typesArr.getJSONObject(i)
                    wasteTypes.add(
                        WasteType(
                            id = o.optString("id", "wt_${i + 1}"),
                            name = o.optString("name", "Sampah"),
                            code = o.optString("code", "SMP"),
                            category = o.optString("category", "Umum"),
                            colorHex = o.optString("colorHex", "#198754"),
                            iconName = o.optString("iconName", "Category"),
                            isActive = o.optBoolean("isActive", true),
                            description = o.optString("description", "")
                        )
                    )
                }
            }

            val announcements = mutableListOf<Announcement>()
            val ancArr = root.optJSONArray("announcements")
            if (ancArr != null) {
                for (i in 0 until ancArr.length()) {
                    val o = ancArr.getJSONObject(i)
                    announcements.add(
                        Announcement(
                            id = o.optString("id", "anc_${i + 1}"),
                            title = o.optString("title", "Pengumuman"),
                            content = o.optString("content", ""),
                            date = o.optString("date", "2026-08-19"),
                            category = o.optString("category", "Informasi"),
                            status = o.optString("status", "Aktif"),
                            author = o.optString("author", "Admin")
                        )
                    )
                }
            }

            val docs = mutableListOf<DocumentationItem>()
            val docArr = root.optJSONArray("documentation")
            if (docArr != null) {
                for (i in 0 until docArr.length()) {
                    val o = docArr.getJSONObject(i)
                    docs.add(
                        DocumentationItem(
                            id = o.optString("id", "doc_${i + 1}"),
                            title = o.optString("title", "Dokumentasi"),
                            date = o.optString("date", "2026-08-19"),
                            location = o.optString("location", "TPS 3R"),
                            category = o.optString("category", "TPS 3R"),
                            description = o.optString("description", ""),
                            photo = o.optString("photo", ""),
                            author = o.optString("author", "Admin")
                        )
                    )
                }
            }

            val campaigns = mutableListOf<Campaign>()
            val campArr = root.optJSONArray("campaigns")
            if (campArr != null) {
                for (i in 0 until campArr.length()) {
                    val o = campArr.getJSONObject(i)
                    campaigns.add(
                        Campaign(
                            id = o.optString("id", "camp_${i + 1}"),
                            title = o.optString("title", "Kegiatan"),
                            category = o.optString("category", "Lingkungan"),
                            organizer = o.optString("organizer", "PT VALE IGP Pomalaa"),
                            startDate = o.optString("startDate", "2026-08-19"),
                            endDate = o.optString("endDate", "2026-08-31"),
                            time = o.optString("time", "08:00 - 16:00 WITA"),
                            location = o.optString("location", "Pomalaa"),
                            description = o.optString("description", ""),
                            targetParticipants = o.optInt("targetParticipants", 100),
                            registeredCount = o.optInt("registeredCount", 0),
                            rewardPoints = o.optInt("rewardPoints", 50),
                            status = o.optString("status", "Sedang Berlangsung"),
                            contactPerson = o.optString("contactPerson", ""),
                            registeredUserIds = o.optString("registeredUserIds", "")
                        )
                    )
                }
            }

            LaikaSyncPayload(
                version = version,
                appName = appName,
                exportedAt = exportedAt,
                exportedBy = exportedBy,
                users = users,
                deposits = deposits,
                wasteTypes = wasteTypes,
                announcements = announcements,
                documentation = docs,
                campaigns = campaigns
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Export payload to a JSON file and open the system Share Sheet (WhatsApp, Bluetooth, Nearby Share / Quick Share, Email, etc.)
     */
    fun exportAndShareDataFile(context: Context, payload: LaikaSyncPayload): Boolean {
        return try {
            val jsonString = serializePayloadToJson(payload)
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val exportDir = File(context.cacheDir, "exports")
            if (!exportDir.exists()) exportDir.mkdirs()

            val file = File(exportDir, "laika_sampah_backup_$timeStamp.json")
            FileOutputStream(file).use { fos ->
                fos.write(jsonString.toByteArray(Charsets.UTF_8))
            }

            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Cadangan Data LAIKA SAMPAH ($timeStamp)")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "📦 *CADANGAN DATA LAIKA SAMPAH TPS 3R PT VALE*\n" +
                    "📅 Waktu Ekspor: ${payload.exportedAt}\n" +
                    "👤 Operator: ${payload.exportedBy}\n" +
                    "📊 Isi Data: ${payload.deposits.size} Setoran, ${payload.users.size} Pengguna, ${payload.wasteTypes.size} Jenis Sampah\n\n" +
                    "Kirimkan file ini ke HP lain melalui Bluetooth, Quick Share, atau WhatsApp, lalu buka menu 'Berbagi Antar HP > Impor Data' di aplikasi LAIKA SAMPAH penerima."
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Kirim Cadangan Data LAIKA SAMPAH"))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Gagal membagikan data: ${e.message}", Toast.LENGTH_SHORT).show()
            false
        }
    }

    /**
     * Share the Application installation link, APK sharing instructions, or invitation to another phone
     */
    fun shareAppToOtherPhone(context: Context) {
        try {
            // First check if current APK file can be directly shared
            val appInfo = context.applicationInfo
            val apkFile = File(appInfo.publicSourceDir)

            val shareIntent = Intent(Intent.ACTION_SEND)
            
            if (apkFile.exists() && apkFile.length() > 0) {
                try {
                    val apkUri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        apkFile
                    )
                    shareIntent.type = "application/vnd.android.package-archive"
                    shareIntent.putExtra(Intent.EXTRA_STREAM, apkUri)
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                } catch (_: Exception) {
                    shareIntent.type = "text/plain"
                }
            } else {
                shareIntent.type = "text/plain"
            }

            val shareMessage = 
                "🌿 *UNDUH & GABUNG APLIKASI LAIKA SAMPAH*\n" +
                "Sistem Pengelolaan Bank Sampah & TPS 3R PT VALE IGP Pomalaa\n\n" +
                "Mari bersama mewujudkan operasional zero waste dan ekonomi sirkular di lingkungan PT Vale Indonesia Tbk.\n\n" +
                "Fitur Utama:\n" +
                "• Pencatatan & Verifikasi Setoran Sampah Terpadu\n" +
                "• Scan QR Code Penyetor & TPS 3R\n" +
                "• Transfer & Sinkronisasi Data Antar HP Tanpa Internet\n" +
                "• Biokonversi Maggot BSF & Komposting Organik\n" +
                "• Ekspor Laporan Excel & CSV\n\n" +
                "Hubungi Tim Environment & TPS 3R PT Vale IGP Pomalaa untuk informasi akun & pendaftaran."

            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Aplikasi LAIKA SAMPAH PT VALE IGP Pomalaa")

            context.startActivity(Intent.createChooser(shareIntent, "Bagikan Aplikasi LAIKA SAMPAH ke HP Lain"))
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Gagal membagikan aplikasi: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Share formatted Deposit Receipt text (Struk Setoran) via WhatsApp, SMS, or other apps
     */
    fun shareDepositReceipt(context: Context, deposit: WasteDeposit) {
        try {
            val formattedText = 
                "📄 *BUKTI PENYETORAN SAMPAH - LAIKA SAMPAH*\n" +
                "🏢 *TPS 3R PT VALE IGP POMALAA*\n" +
                "━━━━━━━━━━━━━━━━━━━━━━\n" +
                "🆔 *ID Transaksi:* #${deposit.id}\n" +
                "📅 *Tanggal:* ${deposit.date} - ${deposit.time} WITA\n" +
                "👤 *Penyetor:* ${deposit.userName}\n" +
                "🏢 *Unit/Dept:* ${deposit.userDepartment}\n" +
                "📍 *Lokasi:* ${deposit.location}\n" +
                "━━━━━━━━━━━━━━━━━━━━━━\n" +
                "♻️ *Jenis Sampah:* ${deposit.wasteType}\n" +
                "⚖️ *Berat:* ${String.format(Locale.US, "%.2f", deposit.weight)} Kg\n" +
                "📌 *Status:* ${deposit.status.uppercase()}\n" +
                (if (deposit.verifiedBy.isNotBlank()) "✅ *Verifikator:* ${deposit.verifiedBy}\n" else "") +
                (if (deposit.verifiedAt.isNotBlank()) "⏰ *Waktu Verifikasi:* ${deposit.verifiedAt}\n" else "") +
                (if (deposit.notes.isNotBlank()) "📝 *Catatan:* ${deposit.notes}\n" else "") +
                "━━━━━━━━━━━━━━━━━━━━━━\n" +
                "🌱 *Terima kasih telah berkontribusi menjaga kelestarian lingkungan dan mendukung program Zero Waste PT Vale!*"

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Bukti Setoran Sampah - ${deposit.userName} (${deposit.weight} Kg)")
                putExtra(Intent.EXTRA_TEXT, formattedText)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Bagikan Struk Setoran ke HP Lain"))
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Gagal membagikan struk: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Generate compact QR string for Peer-to-Peer Setoran Transfer
     * Format: LAIKA_DEP|id|userId|userName|dept|date|time|loc|type|weight|notes
     */
    fun encodeDepositToQr(deposit: WasteDeposit): String {
        return "LAIKA_DEP|" +
                "${deposit.id}|" +
                "${deposit.userId}|" +
                "${deposit.userName}|" +
                "${deposit.userDepartment}|" +
                "${deposit.date}|" +
                "${deposit.time}|" +
                "${deposit.location}|" +
                "${deposit.wasteType}|" +
                "${deposit.weight}|" +
                deposit.notes.replace("|", "/")
    }

    /**
     * Parse compact QR string back into a WasteDeposit
     */
    fun decodeDepositFromQr(qrData: String): WasteDeposit? {
        if (!qrData.startsWith("LAIKA_DEP|")) return null
        val parts = qrData.split("|")
        if (parts.size < 10) return null

        return try {
            WasteDeposit(
                id = parts.getOrNull(1)?.ifBlank { "dep_${System.currentTimeMillis()}" } ?: "dep_${System.currentTimeMillis()}",
                userId = parts.getOrNull(2) ?: "",
                userName = parts.getOrNull(3) ?: "Penyetor",
                userDepartment = parts.getOrNull(4) ?: "Umum",
                date = parts.getOrNull(5) ?: SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                time = parts.getOrNull(6) ?: SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                location = parts.getOrNull(7) ?: "TPS 3R Pomalaa",
                wasteType = parts.getOrNull(8) ?: "Organik",
                weight = parts.getOrNull(9)?.toDoubleOrNull() ?: 1.0,
                photo = "",
                notes = parts.getOrNull(10) ?: "",
                status = "Pending",
                createdAt = System.currentTimeMillis()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Generate compact QR string for User Digital ID Card
     * Format: LAIKA_USER|id|name|username|dept|phone
     */
    fun encodeUserCardToQr(user: User): String {
        return "LAIKA_USER|" +
                "${user.id}|" +
                "${user.name}|" +
                "${user.username}|" +
                "${user.department}|" +
                user.phone
    }

    /**
     * Decode User Digital ID Card from QR
     */
    fun decodeUserCardFromQr(qrData: String): Map<String, String>? {
        if (!qrData.startsWith("LAIKA_USER|")) return null
        val parts = qrData.split("|")
        if (parts.size < 5) return null

        return mapOf(
            "id" to (parts.getOrNull(1) ?: ""),
            "name" to (parts.getOrNull(2) ?: ""),
            "username" to (parts.getOrNull(3) ?: ""),
            "department" to (parts.getOrNull(4) ?: ""),
            "phone" to (parts.getOrNull(5) ?: "")
        )
    }
}
