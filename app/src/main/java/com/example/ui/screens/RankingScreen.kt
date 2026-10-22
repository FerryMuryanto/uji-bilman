package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.ui.theme.*

@Composable
fun RankingScreen(
    processedOfficers: List<ProcessedOfficer>,
    settings: SettingsData,
    activeUlp: UlpItem,
    modifier: Modifier = Modifier
) {
    var sortBy by remember { mutableStateOf("persenTotal") }
    var filterBy by remember { mutableStateOf("all") }

    val sortedList = remember(processedOfficers, sortBy, filterBy) {
        var list = when (filterBy) {
            "top10" -> processedOfficers.sortedBy { it.ranking }.take(10)
            "bottom10" -> processedOfficers.sortedByDescending { it.ranking }.take(10)
            "achieve100" -> processedOfficers.filter { it.persenTotal >= 100.0 }
            "belowTarget" -> processedOfficers.filter { it.persenTotal < 100.0 }
            "zeroReal" -> processedOfficers.filter { it.jumlahRealisasi == 0 }
            else -> processedOfficers
        }

        when (sortBy) {
            "persenTotal" -> list.sortedByDescending { it.persenTotal }
            "p2dpReal" -> list.sortedByDescending { it.activeP2dpReal }
            "prevPersen" -> list.sortedByDescending { it.prevPersen }
            "korPersen" -> list.sortedByDescending { it.korPersen }
            "jumlahReal" -> list.sortedByDescending { it.jumlahRealisasi }
            else -> list.sortedBy { it.ranking }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate100)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Controls Header
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "PERINGKAT & EVALUASI KINERJA PETUGAS",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = PlnNavyPrimary
                )
                Text(
                    text = "Evaluasi performa harian Billman ${activeUlp.nama} (${sortedList.size} petugas ditampilkan)",
                    fontSize = 11.sp,
                    color = Slate700
                )

                // Filter buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = filterBy == "all",
                        onClick = { filterBy = "all" },
                        label = { Text("Semua (${processedOfficers.size})") }
                    )
                    FilterChip(
                        selected = filterBy == "top10",
                        onClick = { filterBy = "top10" },
                        label = { Text("Top 10") }
                    )
                    FilterChip(
                        selected = filterBy == "bottom10",
                        onClick = { filterBy = "bottom10" },
                        label = { Text("Bottom 10") }
                    )
                    FilterChip(
                        selected = filterBy == "achieve100",
                        onClick = { filterBy = "achieve100" },
                        label = { Text("≥100%") }
                    )
                    FilterChip(
                        selected = filterBy == "belowTarget",
                        onClick = { filterBy = "belowTarget" },
                        label = { Text("<100%") }
                    )
                    FilterChip(
                        selected = filterBy == "zeroReal",
                        onClick = { filterBy = "zeroReal" },
                        label = { Text("Realisasi Nol") }
                    )
                }
            }
        }

        // Leaderboard List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(sortedList, key = { it.officer.id }) { p ->
                RankingCard(item = p, thresholds = settings.thresholds)
            }
        }
    }
}

@Composable
private fun RankingCard(item: ProcessedOfficer, thresholds: Thresholds) {
    val o = item.officer
    val badgeBg = when (item.ranking) {
        1 -> PlnGold
        2 -> Color(0xFFE2E8F0)
        3 -> Color(0xFFFDBA74)
        else -> Slate200
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(badgeBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "#${item.ranking}",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        color = PlnNavyDark
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = o.petugas,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Slate900
                    )
                    Text(
                        text = "No. Urut: ${o.no} • ${o.kodePetugas} • Pelanggan: ${o.jumlahPelgn}",
                        fontSize = 10.sp,
                        color = Slate700
                    )
                    Text(
                        text = "P2DP: ${item.activeP2dpReal}/${o.p2dpTarget} • Prev: ${item.activePrevReal}/${o.prevTarget} • Kor: ${item.activeKorReal}/${o.korTarget}",
                        fontSize = 10.sp,
                        color = PlnNavyPrimary
                    )
                }
            }
            PerformanceBadge(
                pct = item.persenTotal,
                real = item.jumlahRealisasi,
                thresholds = thresholds,
                showPercentOnly = true
            )
        }
    }
}
