package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.ui.theme.*

enum class MonitoringDomain {
    P2DP,
    PREVENTIF,
    KOREKTIF
}

@Composable
fun MonitoringDomainScreen(
    domain: MonitoringDomain,
    processedOfficers: List<ProcessedOfficer>,
    rekap: RekapUlp,
    settings: SettingsData,
    activeUlp: UlpItem,
    modifier: Modifier = Modifier
) {
    val title = when (domain) {
        MonitoringDomain.P2DP -> "MONITORING KHUSUS P2DP"
        MonitoringDomain.PREVENTIF -> "MONITORING PREVENTIF NEW ITO"
        MonitoringDomain.KOREKTIF -> "MONITORING KOREKTIF NEW ITO"
    }

    val subtitle = when (domain) {
        MonitoringDomain.P2DP -> "Penertiban Pemakaian Tenaga Listrik • ${activeUlp.nama}"
        MonitoringDomain.PREVENTIF -> "Pemeliharaan & Pengecekan Preventif • ${activeUlp.nama}"
        MonitoringDomain.KOREKTIF -> "Perbaikan & Tindak Lanjut Gangguan • ${activeUlp.nama}"
    }

    val totalTarget = when (domain) {
        MonitoringDomain.P2DP -> rekap.p2dpTarget
        MonitoringDomain.PREVENTIF -> rekap.prevTarget
        MonitoringDomain.KOREKTIF -> rekap.korTarget
    }

    val totalReal = when (domain) {
        MonitoringDomain.P2DP -> rekap.p2dpReal
        MonitoringDomain.PREVENTIF -> rekap.prevReal
        MonitoringDomain.KOREKTIF -> rekap.korReal
    }

    val totalPct = when (domain) {
        MonitoringDomain.P2DP -> rekap.p2dpPersen
        MonitoringDomain.PREVENTIF -> rekap.prevPersen
        MonitoringDomain.KOREKTIF -> rekap.korPersen
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate100)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Summary Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = title, fontWeight = FontWeight.Black, fontSize = 14.sp, color = PlnNavyPrimary)
                    Text(text = subtitle, fontSize = 11.sp, color = Slate700)
                    Text(
                        text = "Target: ${PerformanceHelper.fmtNum(totalTarget)} • Real: ${PerformanceHelper.fmtNum(totalReal)} • Sisa: ${PerformanceHelper.fmtNum((totalTarget - totalReal).coerceAtLeast(0))}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF475569)
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PlnGold)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = PerformanceHelper.fmtPct(totalPct),
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = PlnNavyDark
                    )
                }
            }
        }

        // Table
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            val hScroll = rememberScrollState()
            val vScroll = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(vScroll)
                    .horizontalScroll(hScroll)
            ) {
                // Header
                Row(modifier = Modifier.background(PlnGold)) {
                    DomainHeaderCell("NO", 45.dp)
                    DomainHeaderCell("PETUGAS", 170.dp)
                    DomainHeaderCell("TARGET", 65.dp)
                    DomainHeaderCell("PAGI", 65.dp)
                    DomainHeaderCell("SIANG", 65.dp)
                    DomainHeaderCell("SORE / REAL", 80.dp)
                    DomainHeaderCell("PERSENTASE", 85.dp)
                    DomainHeaderCell("STATUS SELISIH", 120.dp)
                }

                processedOfficers.forEachIndexed { idx, p ->
                    val o = p.officer
                    val t = when (domain) {
                        MonitoringDomain.P2DP -> o.p2dpTarget
                        MonitoringDomain.PREVENTIF -> o.prevTarget
                        MonitoringDomain.KOREKTIF -> o.korTarget
                    }
                    val rPagi = when (domain) {
                        MonitoringDomain.P2DP -> o.pagi.p2dp
                        MonitoringDomain.PREVENTIF -> o.pagi.prev
                        MonitoringDomain.KOREKTIF -> o.pagi.kor
                    }
                    val rSiang = when (domain) {
                        MonitoringDomain.P2DP -> o.siang.p2dp
                        MonitoringDomain.PREVENTIF -> o.siang.prev
                        MonitoringDomain.KOREKTIF -> o.siang.kor
                    }
                    val rSore = when (domain) {
                        MonitoringDomain.P2DP -> p.activeP2dpReal
                        MonitoringDomain.PREVENTIF -> p.activePrevReal
                        MonitoringDomain.KOREKTIF -> p.activeKorReal
                    }
                    val pct = when (domain) {
                        MonitoringDomain.P2DP -> p.p2dpPersen
                        MonitoringDomain.PREVENTIF -> p.prevPersen
                        MonitoringDomain.KOREKTIF -> p.korPersen
                    }
                    val gap = rSore - t

                    val rowBg = if (idx % 2 == 0) Color.White else Color(0xFFF8FAFC)

                    Row(
                        modifier = Modifier
                            .background(rowBg)
                            .border(0.5.dp, Slate200)
                    ) {
                        DomainDataCell(o.no.toString(), 45.dp, isBold = true)
                        DomainDataCell(o.petugas, 170.dp, isBold = true, alignment = Alignment.CenterStart)
                        DomainDataCell(t.toString(), 65.dp, isBold = true)
                        DomainDataCell(rPagi.toString(), 65.dp)
                        DomainDataCell(rSiang.toString(), 65.dp)
                        DomainBadgeCell(pct, rSore, settings.thresholds, 80.dp, isRealOnly = true)
                        DomainBadgeCell(pct, rSore, settings.thresholds, 85.dp, isPercentOnly = true)
                        Box(
                            modifier = Modifier
                                .width(120.dp)
                                .height(32.dp)
                                .padding(horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (gap >= 0) "+$gap (Terpenuhi)" else "$gap (Kurang)",
                                color = if (gap >= 0) PlnEmerald else PlnRose,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DomainHeaderCell(text: String, width: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .width(width)
            .height(34.dp)
            .border(0.5.dp, Color.Black.copy(alpha = 0.2f))
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, fontWeight = FontWeight.Black, fontSize = 10.sp, color = PlnNavyDark)
    }
}

@Composable
private fun DomainDataCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    isBold: Boolean = false,
    alignment: Alignment = Alignment.Center
) {
    Box(
        modifier = Modifier
            .width(width)
            .height(32.dp)
            .border(0.5.dp, Slate200)
            .padding(horizontal = 4.dp),
        contentAlignment = alignment
    ) {
        Text(text = text, fontSize = 10.sp, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal, color = Slate900)
    }
}

@Composable
private fun DomainBadgeCell(
    pct: Double,
    real: Int,
    thresholds: Thresholds,
    width: androidx.compose.ui.unit.Dp,
    isPercentOnly: Boolean = false,
    isRealOnly: Boolean = false
) {
    val style = PerformanceHelper.getStyle(pct, real, thresholds)
    val text = if (isPercentOnly) PerformanceHelper.fmtPct(pct) else real.toString()
    Box(
        modifier = Modifier
            .width(width)
            .height(32.dp)
            .background(style.backgroundColor)
            .border(0.5.dp, Color.Black.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = style.textColor)
    }
}
