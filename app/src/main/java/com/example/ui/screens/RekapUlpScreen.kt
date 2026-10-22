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
fun RekapUlpScreen(
    rekap: RekapUlp,
    activeUlp: UlpItem,
    settings: SettingsData,
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
        Card(
            colors = CardDefaults.cardColors(containerColor = PlnNavyPrimary),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "REKAPITULASI RESMI TOTAL ULP",
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
                    text = "Update Terakhir: ${settings.waktuUpdate} WIB",
                    color = Slate300,
                    fontSize = 11.sp
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "RINCIAN PERHITUNGAN TARGET & REALISASI ULP",
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = PlnNavyPrimary
                )

                RincianRow("1. Pelanggan Dikelola", PerformanceHelper.fmtNum(rekap.jumlahPelgn), "100%", "SUM Pelanggan RBM")
                Divider(color = Slate200)
                RincianRow("2. P2DP", "${PerformanceHelper.fmtNum(rekap.p2dpReal)} / ${PerformanceHelper.fmtNum(rekap.p2dpTarget)}", PerformanceHelper.fmtPct(rekap.p2dpPersen), "Real ÷ Target × 100%")
                Divider(color = Slate200)
                RincianRow("3. Preventif New ITO", "${PerformanceHelper.fmtNum(rekap.prevReal)} / ${PerformanceHelper.fmtNum(rekap.prevTarget)}", PerformanceHelper.fmtPct(rekap.prevPersen), "Real ÷ Target × 100%")
                Divider(color = Slate200)
                RincianRow("4. Korektif New ITO", "${PerformanceHelper.fmtNum(rekap.korReal)} / ${PerformanceHelper.fmtNum(rekap.korTarget)}", PerformanceHelper.fmtPct(rekap.korPersen), "Real ÷ Target × 100%")
                Divider(color = Slate200)

                // Total Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF97316))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("TOTAL CAPAIAN ULP", fontWeight = FontWeight.Black, fontSize = 12.sp, color = Color.White)
                        Text("${rekap.jumlahRealisasi} dari ${rekap.targetAll} Target", fontSize = 11.sp, color = Color.White)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(PlnGold)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            PerformanceHelper.fmtPct(rekap.persenTotal),
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = PlnNavyDark
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RincianRow(label: String, values: String, percent: String, formula: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate900)
            Text("Rumus: $formula", fontSize = 10.sp, color = Slate700)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(values, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PlnNavyPrimary)
            Text(percent, fontWeight = FontWeight.Black, fontSize = 12.sp, color = PlnEmerald)
        }
    }
}
