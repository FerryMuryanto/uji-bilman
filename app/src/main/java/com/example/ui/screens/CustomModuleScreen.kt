package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
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
import com.example.ui.components.PerformanceBadge
import com.example.ui.theme.*

@Composable
fun CustomModuleScreen(
    feature: CustomFeature,
    menuNumber: Int,
    processedOfficers: List<ProcessedOfficer>,
    rekap: RekapUlp,
    settings: SettingsData,
    activeUlp: UlpItem,
    onUpdateOfficerMetric: (Int, String, Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val ct = rekap.customTotals[feature.id] ?: CustomTotalMetric(0, 0, 0.0, 0)
    var editingOfficerId by remember { mutableStateOf<Int?>(null) }
    var editTarget by remember { mutableStateOf("") }
    var editReal by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate100)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Banner Header
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F766E)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(PlnGold)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("MENU #$menuNumber (MODUL BARU)", fontWeight = FontWeight.Black, fontSize = 10.sp, color = PlnNavyDark)
                    }
                    Text(activeUlp.nama, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Text(
                    text = feature.namaFitur,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
                Text(
                    text = "Field: ${feature.fieldBaru} • Rumus: ${feature.rumusLogika}",
                    color = Color(0xFFCCFBF1),
                    fontSize = 11.sp
                )
            }
        }

        // Stats row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("TOTAL TARGET", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate700)
                    Text(PerformanceHelper.fmtNum(ct.target), fontSize = 18.sp, fontWeight = FontWeight.Black, color = Slate900)
                }
            }
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("TOTAL REALISASI", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate700)
                    Text(PerformanceHelper.fmtNum(ct.real), fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F766E))
                }
            }
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDFA)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("CAPAIAN ULP", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F766E))
                    Text(PerformanceHelper.fmtPct(ct.persen), fontSize = 18.sp, fontWeight = FontWeight.Black, color = PlnEmerald)
                }
            }
        }

        // Table of Officers for this module
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
                Row(modifier = Modifier.background(Color(0xFF0D9488))) {
                    Box(modifier = Modifier.width(45.dp).height(34.dp), contentAlignment = Alignment.Center) {
                        Text("NO", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.White)
                    }
                    Box(modifier = Modifier.width(170.dp).height(34.dp), contentAlignment = Alignment.Center) {
                        Text("PETUGAS", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.White)
                    }
                    Box(modifier = Modifier.width(85.dp).height(34.dp), contentAlignment = Alignment.Center) {
                        Text("TARGET", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.White)
                    }
                    Box(modifier = Modifier.width(85.dp).height(34.dp), contentAlignment = Alignment.Center) {
                        Text("REALISASI", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.White)
                    }
                    Box(modifier = Modifier.width(95.dp).height(34.dp), contentAlignment = Alignment.Center) {
                        Text("PERSENTASE", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.White)
                    }
                    Box(modifier = Modifier.width(90.dp).height(34.dp), contentAlignment = Alignment.Center) {
                        Text("AKSI", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.White)
                    }
                }

                processedOfficers.forEachIndexed { idx, p ->
                    val cm = p.customMetrics[feature.id]
                    val cTarget = cm?.target ?: feature.defaultTarget
                    val cReal = cm?.real ?: 0
                    val cPct = cm?.persen ?: 0.0

                    val rowBg = if (idx % 2 == 0) Color.White else Color(0xFFF8FAFC)
                    Row(
                        modifier = Modifier
                            .background(rowBg)
                            .border(0.5.dp, Slate200),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.width(45.dp).height(34.dp), contentAlignment = Alignment.Center) {
                            Text(p.officer.no.toString(), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Box(modifier = Modifier.width(170.dp).height(34.dp).padding(horizontal = 6.dp), contentAlignment = Alignment.CenterStart) {
                            Text(p.officer.petugas, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        }
                        Box(modifier = Modifier.width(85.dp).height(34.dp), contentAlignment = Alignment.Center) {
                            Text(cTarget.toString(), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Box(modifier = Modifier.width(85.dp).height(34.dp), contentAlignment = Alignment.Center) {
                            Text(cReal.toString(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F766E))
                        }
                        Box(modifier = Modifier.width(95.dp).height(34.dp), contentAlignment = Alignment.Center) {
                            PerformanceBadge(pct = cPct, real = cReal, thresholds = settings.thresholds, showPercentOnly = true)
                        }
                        Box(modifier = Modifier.width(90.dp).height(34.dp), contentAlignment = Alignment.Center) {
                            Button(
                                onClick = {
                                    editingOfficerId = p.officer.id
                                    editTarget = cTarget.toString()
                                    editReal = cReal.toString()
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Slate100),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.height(26.dp)
                            ) {
                                Text("Ubah", fontSize = 10.sp, color = PlnNavyPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit Modal
    if (editingOfficerId != null) {
        val targetOfficer = processedOfficers.find { it.officer.id == editingOfficerId }
        AlertDialog(
            onDismissRequest = { editingOfficerId = null },
            title = { Text("Ubah Data: ${targetOfficer?.officer?.petugas}", fontWeight = FontWeight.Bold, fontSize = 14.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Modul: ${feature.namaFitur}", fontSize = 12.sp, color = Slate700)
                    OutlinedTextField(
                        value = editTarget,
                        onValueChange = { editTarget = it },
                        label = { Text("Target Modul") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editReal,
                        onValueChange = { editReal = it },
                        label = { Text("Realisasi Modul") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val t = editTarget.toIntOrNull() ?: feature.defaultTarget
                        val r = editReal.toIntOrNull() ?: 0
                        onUpdateOfficerMetric(editingOfficerId!!, feature.id, t, r)
                        editingOfficerId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PlnNavyPrimary)
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingOfficerId = null }) {
                    Text("Batal")
                }
            }
        )
    }
}
