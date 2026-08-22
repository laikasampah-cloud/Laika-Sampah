package com.example.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.data.model.WasteDeposit
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object ReportExporter {

    enum class ReportPeriod(val label: String, val filenamePrefix: String) {
        HARIAN("Harian (Daily)", "Laporan_Harian_TPS3R"),
        MINGGUAN("Mingguan (Weekly)", "Laporan_Mingguan_TPS3R"),
        BULANAN("Bulanan (Monthly)", "Laporan_Bulanan_TPS3R"),
        TAHUNAN("Tahunan (Yearly)", "Laporan_Tahunan_TPS3R"),
        SEMUA("Semua Periode (All Data)", "Laporan_Master_TPS3R")
    }

    /**
     * Export report data formatted for Microsoft Excel (.csv with UTF-8 BOM for immediate perfect rendering)
     */
    fun exportToExcelCsv(
        context: Context,
        period: ReportPeriod,
        periodDetail: String,
        deposits: List<WasteDeposit>,
        operatorName: String = "Admin TPS 3R Vale"
    ): File? {
        return try {
            val reportsDir = File(context.cacheDir, "reports")
            if (!reportsDir.exists()) reportsDir.mkdirs()

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val file = File(reportsDir, "${period.filenamePrefix}_${timestamp}.csv")

            val nowFormatted = SimpleDateFormat("dd MMMM yyyy HH:mm:ss", Locale("id", "ID")).format(Date())
            val validDeposits = deposits.filter { it.status != "Ditolak" }

            val totalWeight = validDeposits.sumOf { it.weight }
            val totalTransactions = validDeposits.size
            val avgWeight = if (totalTransactions > 0) totalWeight / totalTransactions else 0.0
            val co2Reduced = totalWeight * 2.15 // kg CO2e saved

            val categoryGroups = validDeposits.groupBy { it.wasteType }
                .map { (type, list) ->
                    val sum = list.sumOf { it.weight }
                    val pct = if (totalWeight > 0) (sum / totalWeight) * 100.0 else 0.0
                    Triple(type, list.size, sum) to pct
                }.sortedByDescending { it.first.third }

            val deptGroups = validDeposits.groupBy { it.userDepartment.ifBlank { "Umum / Kontraktor" } }
                .map { (dept, list) ->
                    val sum = list.sumOf { it.weight }
                    val pct = if (totalWeight > 0) (sum / totalWeight) * 100.0 else 0.0
                    Triple(dept, list.size, sum) to pct
                }.sortedByDescending { it.first.third }

            val sb = StringBuilder()
            // UTF-8 BOM so Excel opens with proper character encoding immediately
            sb.append("\uFEFF")

            // Company & System Header
            sb.append("PT VALE INDONESIA Tbk - INDONESIA GROWTH PROJECT (IGP) POMALAA\n")
            sb.append("SISTEM MANAJEMEN SAMPAH & SIRKULAR EKONOMI - TPS 3R \"LAIKA SAMPAH\"\n")
            sb.append("LAPORAN REKAPITULASI PENGELOLAAN SAMPAH TERPADU\n\n")

            // Meta Information
            sb.append("Jenis Laporan;${period.label}\n")
            sb.append("Periode Data;${periodDetail}\n")
            sb.append("Waktu Dibuat;${nowFormatted}\n")
            sb.append("Petugas / Penanggung Jawab;${operatorName}\n")
            sb.append("Status Data;Terverifikasi TPS 3R\n\n")

            // Key Metrics Summary
            sb.append("=== RINGKASAN EKSEKUTIF (KEY PERFORMANCE INDICATORS) ===\n")
            sb.append("Indikator;Nilai;Satuan;Keterangan\n")
            sb.append("Total Akumulasi Sampah Terkelola;${String.format(Locale.US, "%.2f", totalWeight)};Kg;Berat bersih terpilah\n")
            sb.append("Total Transaksi Setoran;${totalTransactions};Transaksi;Setoran terverifikasi\n")
            sb.append("Rata-rata Berat per Setoran;${String.format(Locale.US, "%.2f", avgWeight)};Kg / Transaksi;Efisiensi setoran\n")
            sb.append("Estimasi Emisi Karbon Terhindar;${String.format(Locale.US, "%.2f", co2Reduced)};Kg CO2e;Berdasarkan standar daur ulang 3R\n\n")

            // Breakdown by Waste Category
            sb.append("=== RINCIAN BERDASARKAN KATEGORI & JENIS SAMPAH ===\n")
            sb.append("No;Jenis / Kategori Sampah;Frekuensi Transaksi;Total Berat (Kg);Persentase (%)\n")
            categoryGroups.forEachIndexed { index, (triple, pct) ->
                sb.append("${index + 1};${triple.first};${triple.second};${String.format(Locale.US, "%.2f", triple.third)};${String.format(Locale.US, "%.2f", pct)}%\n")
            }
            sb.append("TOTAL;;${totalTransactions};${String.format(Locale.US, "%.2f", totalWeight)};100.00%\n\n")

            // Breakdown by Department
            sb.append("=== KONTRIBUSI BERDASARKAN DEPARTEMEN / UNIT KERJA ===\n")
            sb.append("No;Departemen / Unit;Jumlah Setoran;Total Berat (Kg);Persentase Kontribusi (%)\n")
            deptGroups.forEachIndexed { index, (triple, pct) ->
                sb.append("${index + 1};${triple.first};${triple.second};${String.format(Locale.US, "%.2f", triple.third)};${String.format(Locale.US, "%.2f", pct)}%\n")
            }
            sb.append("TOTAL;;${totalTransactions};${String.format(Locale.US, "%.2f", totalWeight)};100.00%\n\n")

            // Detailed Ledger of Deposits
            sb.append("=== BUKU BESAR / RIWAYAT TRANSAKSI RINCI ===\n")
            sb.append("No;ID Setoran;Tanggal;Waktu;Nama Penyetor;Departemen / Unit;Lokasi Titik TPS;Jenis Sampah;Berat (Kg);Status Verifikasi;Catatan\n")
            validDeposits.sortedByDescending { "${it.date} ${it.time}" }.forEachIndexed { index, item ->
                val cleanNotes = item.notes.replace(";", ",").replace("\n", " ")
                sb.append("${index + 1};${item.id};${item.date};${item.time};${item.userName};${item.userDepartment};${item.location};${item.wasteType};${String.format(Locale.US, "%.2f", item.weight)};${item.status};${cleanNotes}\n")
            }

            FileOutputStream(file).use { out ->
                out.write(sb.toString().toByteArray(Charsets.UTF_8))
            }

            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Share exported file via Android Intent (Excel, Sheets, WhatsApp, Drive, etc.)
     */
    fun shareReportFile(context: Context, file: File, title: String = "Laporan Pengelolaan Sampah TPS 3R") {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/comma-separated-values"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, "Berikut terlampir $title dari aplikasi LAIKA SAMPAH PT Vale IGP Pomalaa.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, "Buka atau Bagikan Laporan Excel")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
