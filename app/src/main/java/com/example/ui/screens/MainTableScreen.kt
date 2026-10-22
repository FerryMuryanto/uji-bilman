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
import com.example.ui.components.LegendBar
import com.example.ui.components.PerformanceBadge
import com.example.ui.theme.*

@Composable
fun MainTableScreen(
    processedOfficers: List<ProcessedOfficer>,
    rekapUlp: RekapUlp,
    settings: SettingsData,
    activeUlp: UlpItem,
    selectedPeriod: String,
    customFeatures: List<CustomFeature>,
    onSaveAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val mainTableFeatures = remember(customFeatures) {
        customFeatures.filter { it.showInMainTable }
    }

    val filteredOfficers = remember(processedOfficers, searchQuery) {
        if (searchQuery.isBlank()) processedOfficers
        else processedOfficers.filter {
            it.officer.petugas.contains(searchQuery, ignoreCase = true) ||
                    it.officer.kodePetugas.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate100)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Search and Legend Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Cari nama petugas atau kode...", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("main_table_search_input")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onSaveAll,
                        colors = ButtonDefaults.buttonColors(containerColor = PlnEmerald),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("save_all_main_btn")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Simpan All", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                LegendBar(thresholds = settings.thresholds)
            }
        }

        // Title and Meta Banner
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "MONITORING P2DP, PREVENTIF & KOREKTIF",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = PlnNavyPrimary
                    )
                    Text(
                        text = "BILLMAN ${activeUlp.nama} • ${settings.bulan} ${settings.tahun}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Slate700
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(PlnGold)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "UPDATE: ${settings.waktuUpdate} WIB (${selectedPeriod.uppercase()})",
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp,
                        color = PlnNavyDark
                    )
                }
            }
        }

        // Spreadsheet Table with Horizontal & Vertical Scroll
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            val horizontalScrollState = rememberScrollState()
            val verticalScrollState = rememberScrollState()

            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(verticalScrollState)
                        .horizontalScroll(horizontalScrollState)
                ) {
                    // Two-Tier Header
                    TableHeaderRow(mainTableFeatures)

                    // Data Rows
                    filteredOfficers.forEachIndexed { index, proc ->
                        TableDataRow(
                            item = proc,
                            index = index,
                            thresholds = settings.thresholds,
                            mainTableFeatures = mainTableFeatures
                        )
                    }

                    // Summary Total Row
                    TableSummaryRow(
                        rekap = rekapUlp,
                        activeUlp = activeUlp,
                        mainTableFeatures = mainTableFeatures
                    )
                }
            }
        }
    }
}

@Composable
private fun TableHeaderRow(mainTableFeatures: List<CustomFeature>) {
    Column {
        // Tier 1 Header
        Row(modifier = Modifier.background(PlnNavyPrimary)) {
            HeaderCell("NO", 45.dp, PlnNavyPrimary, Color.White)
            HeaderCell("PETUGAS", 170.dp, PlnNavyPrimary, Color.White)
            HeaderCell("PELANGGAN", 85.dp, PlnNavyPrimary, Color.White)
            HeaderCell("TARGET ALL", 85.dp, PlnOrange, Color.White)
            HeaderCell("P2DP", 210.dp, PlnGold, PlnNavyDark)
            HeaderCell("PREVENTIF NEW ITO", 210.dp, PlnGold, PlnNavyDark)
            HeaderCell("KOREKTIF NEW ITO", 210.dp, PlnGold, PlnNavyDark)
            mainTableFeatures.forEach { f ->
                HeaderCell(f.namaFitur.uppercase(), 210.dp, Color(0xFF0D9488), Color.White)
            }
            HeaderCell("TOTAL KINERJA", 160.dp, Color(0xFF1D4ED8), Color.White)
        }
        // Tier 2 Subheader
        Row(modifier = Modifier.background(Color(0xFFFEF08A))) {
            HeaderSubCell("", 45.dp)
            HeaderSubCell("", 170.dp)
            HeaderSubCell("", 85.dp)
            HeaderSubCell("", 85.dp)
            // P2DP sub
            HeaderSubCell("TARGET", 65.dp)
            HeaderSubCell("REAL", 65.dp)
            HeaderSubCell("PERSEN", 80.dp)
            // Prev sub
            HeaderSubCell("TARGET", 65.dp)
            HeaderSubCell("REAL", 65.dp)
            HeaderSubCell("PERSEN", 80.dp)
            // Kor sub
            HeaderSubCell("TARGET", 65.dp)
            HeaderSubCell("REAL", 65.dp)
            HeaderSubCell("PERSEN", 80.dp)
            // Custom modules sub
            mainTableFeatures.forEach { _ ->
                HeaderSubCell("TARGET", 65.dp, Color(0xFF99F6E4))
                HeaderSubCell("REAL", 65.dp, Color(0xFF99F6E4))
                HeaderSubCell("PERSEN", 80.dp, Color(0xFF99F6E4))
            }
            // Total sub
            HeaderSubCell("JUMLAH", 75.dp, Color(0xFF3B82F6), Color.White)
            HeaderSubCell("PERSEN", 85.dp, Color(0xFF3B82F6), Color.White)
        }
    }
}

@Composable
private fun HeaderCell(title: String, width: androidx.compose.ui.unit.Dp, bg: Color, textCol: Color) {
    Box(
        modifier = Modifier
            .width(width)
            .height(34.dp)
            .background(bg)
            .border(0.5.dp, Color(0xFF000000).copy(alpha = 0.3f))
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title, fontWeight = FontWeight.Black, fontSize = 10.sp, color = textCol, maxLines = 1)
    }
}

@Composable
private fun HeaderSubCell(title: String, width: androidx.compose.ui.unit.Dp, bg: Color = Color(0xFFFEF08A), textCol: Color = PlnNavyDark) {
    Box(
        modifier = Modifier
            .width(width)
            .height(28.dp)
            .background(bg)
            .border(0.5.dp, Color(0xFF000000).copy(alpha = 0.2f))
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 9.sp, color = textCol)
    }
}

@Composable
private fun TableDataRow(
    item: ProcessedOfficer,
    index: Int,
    thresholds: Thresholds,
    mainTableFeatures: List<CustomFeature>
) {
    val rowBg = if (index % 2 == 0) Color.White else Color(0xFFF8FAFC)
    val o = item.officer

    Row(
        modifier = Modifier
            .background(rowBg)
            .border(0.5.dp, Slate200)
    ) {
        DataCell(o.no.toString(), 45.dp, isBold = true)
        DataCell(o.petugas, 170.dp, isBold = true, alignment = Alignment.CenterStart)
        DataCell(PerformanceHelper.fmtNum(o.jumlahPelgn), 85.dp)
        DataCell(item.finalTargetAll.toString(), 85.dp, isBold = true, bg = Color(0xFFFFF7ED))

        // P2DP
        DataCell(o.p2dpTarget.toString(), 65.dp)
        BadgeCell(item.p2dpPersen, item.activeP2dpReal, thresholds, 65.dp, isRealOnly = true)
        BadgeCell(item.p2dpPersen, item.activeP2dpReal, thresholds, 80.dp, isPercentOnly = true)

        // Preventif
        DataCell(o.prevTarget.toString(), 65.dp)
        BadgeCell(item.prevPersen, item.activePrevReal, thresholds, 65.dp, isRealOnly = true)
        BadgeCell(item.prevPersen, item.activePrevReal, thresholds, 80.dp, isPercentOnly = true)

        // Korektif
        DataCell(o.korTarget.toString(), 65.dp)
        BadgeCell(item.korPersen, item.activeKorReal, thresholds, 65.dp, isRealOnly = true)
        BadgeCell(item.korPersen, item.activeKorReal, thresholds, 80.dp, isPercentOnly = true)

        // Custom features
        mainTableFeatures.forEach { f ->
            val cm = item.customMetrics[f.id]
            val cTarget = cm?.target ?: 0
            val cReal = cm?.real ?: 0
            val cPct = cm?.persen ?: 0.0
            DataCell(cTarget.toString(), 65.dp, bg = Color(0xFFF0FDFA))
            BadgeCell(cPct, cReal, thresholds, 65.dp, isRealOnly = true)
            BadgeCell(cPct, cReal, thresholds, 80.dp, isPercentOnly = true)
        }

        // Total
        DataCell(item.jumlahRealisasi.toString(), 75.dp, isBold = true, bg = Color(0xFFEFF6FF))
        BadgeCell(item.persenTotal, item.jumlahRealisasi, thresholds, 85.dp, isPercentOnly = true)
    }
}

@Composable
private fun TableSummaryRow(
    rekap: RekapUlp,
    activeUlp: UlpItem,
    mainTableFeatures: List<CustomFeature>
) {
    val summaryBg = Color(0xFFF97316)
    Row(
        modifier = Modifier
            .background(summaryBg)
            .border(1.dp, Color.Black)
    ) {
        Box(
            modifier = Modifier
                .width(215.dp)
                .height(36.dp)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(text = "TOTAL ${activeUlp.nama}", fontWeight = FontWeight.Black, fontSize = 11.sp, color = Color.White)
        }
        DataCell(PerformanceHelper.fmtNum(rekap.jumlahPelgn), 85.dp, isBold = true, textCol = Color.White)
        DataCell(PerformanceHelper.fmtNum(rekap.targetAll), 85.dp, isBold = true, textCol = Color.White)

        // P2DP
        DataCell(rekap.p2dpTarget.toString(), 65.dp, isBold = true, textCol = Color.White)
        DataCell(rekap.p2dpReal.toString(), 65.dp, isBold = true, textCol = Color.White)
        SummaryBadgeCell(rekap.p2dpPersen, 80.dp)

        // Prev
        DataCell(rekap.prevTarget.toString(), 65.dp, isBold = true, textCol = Color.White)
        DataCell(rekap.prevReal.toString(), 65.dp, isBold = true, textCol = Color.White)
        SummaryBadgeCell(rekap.prevPersen, 80.dp)

        // Kor
        DataCell(rekap.korTarget.toString(), 65.dp, isBold = true, textCol = Color.White)
        DataCell(rekap.korReal.toString(), 65.dp, isBold = true, textCol = Color.White)
        SummaryBadgeCell(rekap.korPersen, 80.dp)

        // Custom features
        mainTableFeatures.forEach { f ->
            val ct = rekap.customTotals[f.id]
            DataCell(ct?.target?.toString() ?: "0", 65.dp, isBold = true, textCol = Color.White)
            DataCell(ct?.real?.toString() ?: "0", 65.dp, isBold = true, textCol = Color.White)
            SummaryBadgeCell(ct?.persen ?: 0.0, 80.dp)
        }

        // Total
        DataCell(rekap.jumlahRealisasi.toString(), 75.dp, isBold = true, textCol = Color.White)
        SummaryBadgeCell(rekap.persenTotal, 85.dp)
    }
}

@Composable
private fun DataCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    isBold: Boolean = false,
    bg: Color = Color.Transparent,
    textCol: Color = Slate900,
    alignment: Alignment = Alignment.Center
) {
    Box(
        modifier = Modifier
            .width(width)
            .height(32.dp)
            .background(bg)
            .border(0.5.dp, Slate200)
            .padding(horizontal = 4.dp),
        contentAlignment = alignment
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = textCol,
            maxLines = 1
        )
    }
}

@Composable
private fun BadgeCell(
    pct: Double,
    real: Int,
    thresholds: Thresholds,
    width: androidx.compose.ui.unit.Dp,
    isPercentOnly: Boolean = false,
    isRealOnly: Boolean = false
) {
    val style = PerformanceHelper.getStyle(pct, real, thresholds)
    val text = when {
        isPercentOnly -> PerformanceHelper.fmtPct(pct)
        isRealOnly -> real.toString()
        else -> "$real (${PerformanceHelper.fmtPct(pct)})"
    }

    Box(
        modifier = Modifier
            .width(width)
            .height(32.dp)
            .background(style.backgroundColor)
            .border(0.5.dp, Color.Black.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = style.textColor
        )
    }
}

@Composable
private fun SummaryBadgeCell(pct: Double, width: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .width(width)
            .height(36.dp)
            .background(PlnGold)
            .border(0.5.dp, Color.Black.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = PerformanceHelper.fmtPct(pct),
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = PlnNavyDark
        )
    }
}
