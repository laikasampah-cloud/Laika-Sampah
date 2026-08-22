package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WasteDeposit
import com.example.ui.theme.*

@Composable
fun DepositDetailDialog(
    deposit: WasteDeposit?,
    onDismiss: () -> Unit
) {
    if (deposit == null) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Receipt, contentDescription = null, tint = ForestGreenPrimary)
                Text("Detail Tiket Setoran Sampah", fontWeight = FontWeight.Black, fontSize = 16.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Status Header Banner
                Surface(
                    color = when (deposit.status) {
                        "Diverifikasi" -> LightMint
                        "Pending" -> EcoOrange.copy(alpha = 0.15f)
                        else -> Color(0xFFF8D7DA)
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("STATUS VERIFIKASI", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                            Text(
                                text = deposit.status.uppercase(),
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = when (deposit.status) {
                                    "Diverifikasi" -> ForestGreenPrimary
                                    "Pending" -> EcoOrange
                                    else -> EcoRed
                                }
                            )
                        }
                        Text(
                            text = "${deposit.weight} Kg",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = ForestGreenPrimary
                        )
                    }
                }

                DetailRow("ID Transaksi", deposit.id)
                DetailRow("Nama Penyetor", deposit.userName)
                DetailRow("Unit / Departemen", deposit.userDepartment)
                DetailRow("Jenis Sampah", deposit.wasteType)
                DetailRow("Lokasi Penyetoran", deposit.location)
                DetailRow("Waktu Pencatatan", "${deposit.date} ${deposit.time} WITA")

                if (deposit.notes.isNotBlank()) {
                    DetailRow("Catatan", deposit.notes)
                }

                if (deposit.status == "Ditolak" && deposit.rejectionReason.isNotBlank()) {
                    Surface(
                        color = Color(0xFFF8D7DA),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("Alasan Penolakan Admin:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = EcoRed)
                            Text(deposit.rejectionReason, fontSize = 12.sp, color = EcoRed)
                        }
                    }
                }

                HorizontalDivider()
                Text(
                    text = "PT VALE IGP POMALAA • TPS 3R TERPADU",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        },
        confirmButton = {
            val context = androidx.compose.ui.platform.LocalContext.current
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        com.example.utils.DataTransferHelper.shareDepositReceipt(context, deposit)
                    },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp), tint = EmeraldGreen)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Bagikan Struk", fontSize = 12.sp, color = EmeraldGreen, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Tutup", color = Color.White)
                }
            }
        }
    )
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 11.sp, color = TextSecondary)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}
