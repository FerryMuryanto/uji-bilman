package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PerformanceHelper
import com.example.model.*
import com.example.ui.components.PerformanceBadge
import com.example.ui.components.StatCard
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    rekap: RekapUlp,
    processedOfficers: List<ProcessedOfficer>,
    activeUlp: UlpItem,
    allUlps: List<UlpItem>,
    currentUser: UserCredential?,
    customFeatures: List<CustomFeature>,
    onSelectUlp: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate100)
            .verticalScroll(scrollState)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Welcome Banner
        Card(
            colors = CardDefaults.cardColors(containerColor = PlnNavyPrimary),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "DASHBOARD MONITORING KINERJA",
                        color = PlnGold,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = activeUlp.nama,
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Area UP3 Tegal • PT PLN Electricity Services",
                        color = Slate300,
                        fontSize = 11.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("TOTAL CAPAIAN", color = PlnGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = PerformanceHelper.fmtPct(rekap.persenTotal),
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // 10 KPI Stat Cards in a responsive Grid
        Text(
            text = "10 INDIKATOR UTAMA KINERJA (KPI)",
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            color = Slate900
        )

        // Row 1
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard(
                title = "1. Total Petugas",
                value = processedOfficers.size.toString(),
                subtitle = "Billman Aktif",
                icon = Icons.Default.People,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "2. Total Pelanggan",
                value = PerformanceHelper.fmtNum(rekap.jumlahPelgn),
                subtitle = "Pelanggan RBM",
                icon = Icons.Default.Groups,
                valueColor = PlnPurple,
                modifier = Modifier.weight(1f)
            )
        }

        // Row 2: P2DP Target & Realisasi
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard(
                title = "3. Target P2DP",
                value = PerformanceHelper.fmtNum(rekap.p2dpTarget),
                subtitle = "Target All: ${PerformanceHelper.fmtNum(rekap.targetAll)}",
                icon = Icons.Default.Flag,
                valueColor = PlnOrange,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "4. Realisasi P2DP",
                value = PerformanceHelper.fmtNum(rekap.p2dpReal),
                subtitle = "Sisa: ${PerformanceHelper.fmtNum((rekap.p2dpTarget - rekap.p2dpReal).coerceAtLeast(0))}",
                icon = Icons.Default.CheckCircle,
                valueColor = PlnEmerald,
                modifier = Modifier.weight(1f)
            )
        }

        // Row 3: Persentase P2DP & Preventif
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard(
                title = "5. Persentase P2DP",
                value = PerformanceHelper.fmtPct(rekap.p2dpPersen),
                subtitle = "Real ÷ Target P2DP",
                icon = Icons.Default.Percent,
                valueColor = PlnNavyPrimary,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "6. Real Preventif",
                value = PerformanceHelper.fmtNum(rekap.prevReal),
                subtitle = "Target: ${rekap.prevTarget}",
                icon = Icons.Default.Build,
                valueColor = PlnCyan,
                modifier = Modifier.weight(1f)
            )
        }

        // Row 4: Preventif % & Korektif Real
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard(
                title = "7. Persen Preventif",
                value = PerformanceHelper.fmtPct(rekap.prevPersen),
                subtitle = "Preventif New ITO",
                icon = Icons.Default.TrendingUp,
                valueColor = PlnCyan,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "8. Real Korektif",
                value = PerformanceHelper.fmtNum(rekap.korReal),
                subtitle = "Target: ${rekap.korTarget}",
                icon = Icons.Default.AssignmentTurnedIn,
                valueColor = PlnPurple,
                modifier = Modifier.weight(1f)
            )
        }

        // Row 5: Korektif % & Total Kinerja
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard(
                title = "9. Persen Korektif",
                value = PerformanceHelper.fmtPct(rekap.korPersen),
                subtitle = "Korektif New ITO",
                icon = Icons.Default.Speed,
                valueColor = PlnPurple,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "10. Capaian ULP",
                value = PerformanceHelper.fmtPct(rekap.persenTotal),
                subtitle = "${rekap.jumlahRealisasi} / ${rekap.targetAll} Selesai",
                icon = Icons.Default.EmojiEvents,
                valueColor = Color(0xFFC2410C),
                modifier = Modifier.weight(1f)
            )
        }

        // Progress bar visualizer for Pagi, Siang, Sore
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "PROGRES REALISASI KUMULATIF HARIAN",
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = PlnNavyPrimary
                )
                ProgressIndicatorRow("Pagi (09.00 WIB)", rekap.pagiTotal, rekap.targetAll, PlnCyan)
                ProgressIndicatorRow("Siang (14.15 WIB)", rekap.siangTotal, rekap.targetAll, PlnGold)
                ProgressIndicatorRow("Sore (20.33 WIB)", rekap.soreTotal, rekap.targetAll, PlnEmerald)
            }
        }

        // Top 5 Officers Leaderboard
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "TOP 5 PETUGAS TERBAIK",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        color = PlnNavyPrimary
                    )
                    Text(
                        text = "${rekap.count100} Petugas Capai 100%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PlnEmerald
                    )
                }

                val top5 = remember(processedOfficers) {
                    processedOfficers.sortedBy { it.ranking }.take(5)
                }

                top5.forEachIndexed { i, p ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (i == 0) Color(0xFFFEF9C3) else Slate50)
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(if (i == 0) PlnGold else Slate300),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("#${p.ranking}", fontSize = 10.sp, fontWeight = FontWeight.Black)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(p.officer.petugas, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("${p.jumlahRealisasi} / ${p.finalTargetAll} Pekerjaan Selesai", fontSize = 10.sp, color = Slate700)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(PlnEmerald)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                PerformanceHelper.fmtPct(p.persenTotal),
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // 10 ULP Overview List
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "UNIT LAYANAN PELANGGAN (10 ULP SE-UP3 TEGAL)",
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = PlnNavyPrimary
                )
                Text(
                    text = if (currentUser?.canSwitchUlp == true)
                        "Super Admin dapat mengetuk ULP untuk berpindah ruangan."
                    else
                        "Akun Anda dikhususkan untuk unit ${activeUlp.nama}.",
                    fontSize = 11.sp,
                    color = Slate700
                )

                allUlps.forEachIndexed { idx, ulp ->
                    val isSelected = ulp.id == activeUlp.id
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color(0xFFEEF2FF) else Slate50)
                            .clickable(enabled = currentUser?.canSwitchUlp == true) {
                                onSelectUlp(ulp.id)
                            }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${idx + 1}.", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Slate700)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(ulp.nama, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (isSelected) PlnNavyPrimary else Slate900)
                                Text("${ulp.jmlPetugas} Petugas • ${ulp.kodePrefix}", fontSize = 10.sp, color = Slate700)
                            }
                        }
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(PlnNavyPrimary)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text("AKTIF", color = PlnGold, fontSize = 9.sp, fontWeight = FontWeight.Black)
                            }
                        } else if (currentUser?.canSwitchUlp == true) {
                            Text("Buka", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PlnCyan)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgressIndicatorRow(label: String, current: Int, target: Int, color: Color) {
    val fraction = if (target > 0) (current.toFloat() / target.toFloat()).coerceIn(0f, 1f) else 0f
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Slate800)
            Text(
                "$current / $target (${(fraction * 100).toInt()}%)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = Slate200
        )
    }
}
