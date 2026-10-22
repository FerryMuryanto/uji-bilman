package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PerformanceHelper
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun HistoryScreen(
    historyList: List<HistorySnapshot>,
    processedOfficers: List<ProcessedOfficer>,
    activeUlp: UlpItem,
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
        // Snapshots list card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "LOG RIWAYAT SNAPSHOT UPDATE HARIAN",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = PlnNavyPrimary
                )
                Text(
                    text = "Catatan pembaruan berkala dari FSM ACMT untuk ${activeUlp.nama}",
                    fontSize = 11.sp,
                    color = Slate700
                )

                historyList.reversed().forEach { snap ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Slate50)
                            .border(0.5.dp, Slate200)
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(snap.jam + " WIB", fontWeight = FontWeight.Black, fontSize = 12.sp, color = PlnNavyPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFFEF08A))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(snap.periode, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = PlnNavyDark)
                                }
                            }
                            Text("${snap.tanggal} • ${snap.namaFile}", fontSize = 10.sp, color = Slate700)
                            Text("User: ${snap.user} • ${snap.jumlahPetugas} Petugas", fontSize = 10.sp, color = Slate700)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = PerformanceHelper.fmtPct(snap.persenTotal),
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = PlnEmerald
                            )
                            Text(
                                text = "${snap.totalRealisasi} Real (${snap.deltaRealisasi})",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1D4ED8)
                            )
                        }
                    }
                }
            }
        }

        // Progression from Pagi -> Siang -> Sore
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "KENAIKAN REALISASI HARIAN (PAGI → SIANG → SORE)",
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = PlnNavyPrimary
                )

                val hScroll = rememberScrollState()
                Column(modifier = Modifier.horizontalScroll(hScroll)) {
                    Row(modifier = Modifier.background(PlnNavyPrimary)) {
                        HistoryHeaderCell("No", 45.dp)
                        HistoryHeaderCell("Petugas", 160.dp)
                        HistoryHeaderCell("Pagi", 65.dp)
                        HistoryHeaderCell("Siang", 65.dp)
                        HistoryHeaderCell("Delta P->S", 80.dp)
                        HistoryHeaderCell("Sore", 65.dp)
                        HistoryHeaderCell("Delta S->S", 80.dp)
                        HistoryHeaderCell("Capaian", 80.dp)
                    }

                    processedOfficers.forEachIndexed { i, p ->
                        val o = p.officer
                        val totPagi = o.pagi.p2dp + o.pagi.prev + o.pagi.kor
                        val totSiang = o.siang.p2dp + o.siang.prev + o.siang.kor
                        val totSore = o.p2dpReal + o.prevReal + o.korReal

                        val rowBg = if (i % 2 == 0) Color.White else Color(0xFFF8FAFC)
                        Row(modifier = Modifier.background(rowBg).border(0.5.dp, Slate200)) {
                            HistoryDataCell(o.no.toString(), 45.dp, isBold = true)
                            HistoryDataCell(o.petugas, 160.dp, isBold = true, alignment = Alignment.CenterStart)
                            HistoryDataCell(totPagi.toString(), 65.dp)
                            HistoryDataCell(totSiang.toString(), 65.dp)
                            HistoryDataCell("+${p.deltaPagiKeSiang}", 80.dp, textCol = PlnEmerald, isBold = true)
                            HistoryDataCell(totSore.toString(), 65.dp)
                            HistoryDataCell("+${p.deltaSiangKeSore}", 80.dp, textCol = PlnCyan, isBold = true)
                            HistoryDataCell(PerformanceHelper.fmtPct(p.persenTotal), 80.dp, isBold = true)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryHeaderCell(text: String, width: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier.width(width).height(32.dp).padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.White)
    }
}

@Composable
private fun HistoryDataCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    isBold: Boolean = false,
    textCol: Color = Slate900,
    alignment: Alignment = Alignment.Center
) {
    Box(
        modifier = Modifier.width(width).height(30.dp).padding(horizontal = 4.dp),
        contentAlignment = alignment
    ) {
        Text(text, fontSize = 10.sp, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal, color = textCol)
    }
}
