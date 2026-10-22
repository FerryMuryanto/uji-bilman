package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PerformanceHelper
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun ExportPresentationScreen(
    rekap: RekapUlp,
    processedOfficers: List<ProcessedOfficer>,
    activeUlp: UlpItem,
    settings: SettingsData,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val top5 = processedOfficers.sortedBy { it.ranking }.take(5)
    val bottom5 = processedOfficers.sortedByDescending { it.ranking }.take(5)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate100)
            .verticalScroll(scrollState)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Actions Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "PUSAT EKSPOR LAPORAN & PRESENTASI 16:9",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = PlnNavyPrimary
                )
                Text(
                    text = "Format laporan siap bagikan untuk manajemen dan grup koordinasi WhatsApp.",
                    fontSize = 11.sp,
                    color = Slate700
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val shareText = """
                                *LAPORAN HARIAN BILLMAN P2DP — ${activeUlp.nama}*
                                *AREA UP3 TEGAL — PT PLN ELECTRICITY SERVICES*
                                Tanggal: ${settings.waktuUpdate} WIB (${settings.periodeAktif})

                                📊 *REKAPITULASI KINERJA:*
                                • Total Pelanggan: ${PerformanceHelper.fmtNum(rekap.jumlahPelgn)}
                                • P2DP: ${rekap.p2dpReal}/${rekap.p2dpTarget} (${PerformanceHelper.fmtPct(rekap.p2dpPersen)})
                                • Preventif: ${rekap.prevReal}/${rekap.prevTarget} (${PerformanceHelper.fmtPct(rekap.prevPersen)})
                                • Korektif: ${rekap.korReal}/${rekap.korTarget} (${PerformanceHelper.fmtPct(rekap.korPersen)})
                                • TOTAL CAPAIAN: ${PerformanceHelper.fmtPct(rekap.persenTotal)} (${rekap.jumlahRealisasi}/${rekap.targetAll})

                                🏆 *TOP 3 PETUGAS:*
                                1. ${top5.getOrNull(0)?.officer?.petugas ?: "-"} (${PerformanceHelper.fmtPct(top5.getOrNull(0)?.persenTotal ?: 0.0)})
                                2. ${top5.getOrNull(1)?.officer?.petugas ?: "-"} (${PerformanceHelper.fmtPct(top5.getOrNull(1)?.persenTotal ?: 0.0)})
                                3. ${top5.getOrNull(2)?.officer?.petugas ?: "-"} (${PerformanceHelper.fmtPct(top5.getOrNull(2)?.persenTotal ?: 0.0)})
                            """.trimIndent()

                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Bagikan Laporan Billman")
                            context.startActivity(shareIntent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PlnEmerald),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("share_whatsapp_report_btn")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Bagikan Teks Laporan", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 16:9 Presentation Bento Preview Card
        Card(
            colors = CardDefaults.cardColors(containerColor = PlnNavyPrimary),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header of the slide
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(PlnGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("PLN", fontWeight = FontWeight.Black, fontSize = 14.sp, color = PlnNavyDark)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("PT PLN ELECTRICITY SERVICES", color = PlnGold, fontSize = 9.sp, fontWeight = FontWeight.Black)
                            Text(activeUlp.nama, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            "UPDATE: ${settings.waktuUpdate} WIB",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Bento Hero Block
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E293B),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "BILLMAN PERFORMANCE MONITORING P2DP",
                            color = PlnGold,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Monitoring Terpadu P2DP, Preventif & Korektif New ITO — 46 Petugas Billman",
                            color = Slate300,
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("TOTAL PENCAPAIAN ULP", color = Slate300, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = PerformanceHelper.fmtPct(rekap.persenTotal),
                                    color = PlnGold,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text("${rekap.jumlahRealisasi} dari ${rekap.targetAll} Target", color = Color.White, fontSize = 10.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("TOTAL PELANGGAN", color = Slate300, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = PerformanceHelper.fmtNum(rekap.jumlahPelgn),
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text("${rekap.count100} Petugas Capai 100%", color = PlnEmerald, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Bento 3 KPI Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BentoPill("P2DP", PerformanceHelper.fmtPct(rekap.p2dpPersen), "${rekap.p2dpReal}/${rekap.p2dpTarget}", Modifier.weight(1f))
                    BentoPill("PREVENTIF", PerformanceHelper.fmtPct(rekap.prevPersen), "${rekap.prevReal}/${rekap.prevTarget}", Modifier.weight(1f))
                    BentoPill("KOREKTIF", PerformanceHelper.fmtPct(rekap.korPersen), "${rekap.korReal}/${rekap.korTarget}", Modifier.weight(1f))
                }

                // Motivational Quote Footer
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White.copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "“Disiplin melakukan update, konsisten menyelesaikan pekerjaan, dan bergerak bersama mencapai target. Setiap realisasi hari ini (${rekap.jumlahRealisasi} pekerjaan selesai) adalah langkah nyata menuju kinerja ULP Tegal Timur yang unggul!”",
                        fontStyle = FontStyle.Italic,
                        color = Color.White,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BentoPill(title: String, percent: String, realTarget: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF1E293B),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(title, color = Slate300, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text(percent, color = PlnGold, fontSize = 14.sp, fontWeight = FontWeight.Black)
            Text(realTarget, color = Color.White, fontSize = 9.sp)
        }
    }
}
