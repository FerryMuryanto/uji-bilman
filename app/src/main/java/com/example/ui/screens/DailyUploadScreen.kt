package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PerformanceHelper
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun DailyUploadScreen(
    activeUlp: UlpItem,
    processedOfficers: List<ProcessedOfficer>,
    settings: SettingsData,
    currentUser: UserCredential?,
    onSaveSnapshot: (HistorySnapshot) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPeriode by remember { mutableStateOf("Pagi") }
    var updateTimestamp by remember { mutableStateOf(settings.waktuUpdate) }
    var uploadMode by remember { mutableStateOf("kumulatif") }
    var isSimulatingFile by remember { mutableStateOf(false) }
    var simulatedFileName by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate100)
            .verticalScroll(scrollState)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Guidance Box
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, Color(0xFFFCD34D)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(PlnGold)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "PANDUAN & FORMAT ACUAN UPLOAD",
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            color = PlnNavyDark
                        )
                    }
                }
                Text(
                    text = "Upload Update Kinerja Harian — ${activeUlp.nama}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = PlnNavyPrimary
                )
                Text(
                    text = "Petugas mengunggah file laporan harian hasil download dari FSM ACMT atau berkas kerja harian. Sistem secara otomatis mencocokkan kode petugas, menghitung capaian target, dan mencatat riwayat snapshot update.",
                    fontSize = 11.sp,
                    color = Slate700
                )
            }
        }

        // Settings and Parameters Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "PARAMETER UPDATE HARIAN",
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = PlnNavyPrimary
                )

                // Periode selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Pagi", "Siang", "Sore").forEach { p ->
                        val isSelected = selectedPeriode == p
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) PlnNavyPrimary else Slate100,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedPeriode = p }
                        ) {
                            Box(modifier = Modifier.padding(10.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = p,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isSelected) PlnGold else Slate900
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = updateTimestamp,
                    onValueChange = { updateTimestamp = it },
                    label = { Text("Tanggal & Jam Laporan (WIB)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            isSimulatingFile = true
                            simulatedFileName = "FSM_ACMT_${activeUlp.id}_22102026.xlsx"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("simulate_excel_btn")
                    ) {
                        Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Simulasi File Excel", fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            isSimulatingFile = true
                            simulatedFileName = "ACMT_EXPORT_${activeUlp.id}_22102026.pdf"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7E22CE)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("simulate_pdf_btn")
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Simulasi File PDF", fontSize = 11.sp)
                    }
                }
            }
        }

        // Preview Section if simulated
        if (isSimulatingFile) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF0FDF4),
                        border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "File Terbaca: $simulatedFileName",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF14532D)
                                )
                                Text(
                                    text = "46 Petugas Cocok • 0 Duplikasi Target",
                                    fontSize = 11.sp,
                                    color = Color(0xFF15803D)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(PlnEmerald)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("VALID", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            }
                        }
                    }

                    Text(
                        text = "Ringkasan Pembacaan Contoh Data (${processedOfficers.size} Petugas):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )

                    // Preview of first 3 rows
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        processedOfficers.take(4).forEach { p ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Slate50)
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(p.officer.petugas, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    Text("Kode: ${p.officer.kodePetugas} • Pelanggan: ${p.officer.jumlahPelgn}", fontSize = 10.sp, color = Slate700)
                                }
                                Text(
                                    text = "P2DP: ${p.activeP2dpReal}/${p.officer.p2dpTarget} • Prev: ${p.activePrevReal}/${p.officer.prevTarget}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PlnNavyPrimary
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                val newSnapshot = HistorySnapshot(
                                    id = "SNAP-${System.currentTimeMillis().toString().takeLast(6)}",
                                    tanggal = updateTimestamp.split(" ").getOrNull(0) ?: "22/10/2026",
                                    jam = updateTimestamp.split(" ").getOrNull(1) ?: "10.55",
                                    periode = selectedPeriode,
                                    namaFile = simulatedFileName,
                                    jumlahPetugas = processedOfficers.size,
                                    totalRealisasi = processedOfficers.sumOf { it.jumlahRealisasi },
                                    persenTotal = PerformanceHelper.calcPct(
                                        processedOfficers.sumOf { it.jumlahRealisasi },
                                        processedOfficers.sumOf { it.finalTargetAll }
                                    ),
                                    deltaRealisasi = "+${(100..300).random()}",
                                    user = currentUser?.name ?: "Admin Billman"
                                )
                                onSaveSnapshot(newSnapshot)
                                isSimulatingFile = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PlnEmerald),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("apply_upload_snapshot_btn")
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Simpan Snapshot Update Harian", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
