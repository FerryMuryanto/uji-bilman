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
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun DatabaseFeatureBuilderScreen(
    customFeatures: List<CustomFeature>,
    onAddFeature: (CustomFeature) -> Unit,
    onRemoveFeature: (String) -> Unit,
    onToggleShowInTable: (String, Boolean) -> Unit,
    onToggleIncludeInTotal: (String, Boolean) -> Unit,
    onNavigateToCustomModule: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var namaFitur by remember { mutableStateOf("") }
    var kategori by remember { mutableStateOf("Monitoring & Tabel") }
    var targetMenu by remember { mutableStateOf("Tabel Referensi Utama") }
    var fieldBaru by remember { mutableStateOf("") }
    var rumusLogika by remember { mutableStateOf("") }
    var defaultTarget by remember { mutableStateOf("15") }
    var showInMainTable by remember { mutableStateOf(true) }
    var includeInTotalUlp by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate100)
            .verticalScroll(scrollState)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // DB Status Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFDCFCE7))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("DATABASE REALTIME AKTIF", fontWeight = FontWeight.Black, fontSize = 10.sp, color = Color(0xFF166534))
                    }
                }
                Text(
                    text = "Lokasi Penyimpanan & Sinkronisasi Multi-Device",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = PlnNavyPrimary
                )
                Text(
                    text = "Status: Cloud Firestore Realtime Sync + Local SQLite Offline Engine. Data otomatis terhubung antar handphone, tablet, dan laptop.",
                    fontSize = 11.sp,
                    color = Slate700
                )
            }
        }

        // New Feature Generator Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "GENERATOR MODUL KUSTOM (MENU #${14 + customFeatures.size})",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = PlnNavyPrimary
                )
                Text(
                    text = "Setiap fitur baru otomatis muncul di menu samping mulai dari nomor 14, 15, dst. dan sinkron dengan 46 Petugas.",
                    fontSize = 11.sp,
                    color = Slate700
                )

                OutlinedTextField(
                    value = namaFitur,
                    onValueChange = { namaFitur = it },
                    label = { Text("Nama Fitur / Modul Baru") },
                    placeholder = { Text("Contoh: Monitoring Pemutusan Rampung") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = fieldBaru,
                        onValueChange = { fieldBaru = it },
                        label = { Text("Nama Kolom DB") },
                        placeholder = { Text("target_rampung, real_rampung") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = defaultTarget,
                        onValueChange = { defaultTarget = it },
                        label = { Text("Target Default") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = rumusLogika,
                    onValueChange = { rumusLogika = it },
                    label = { Text("Rumus Logika") },
                    placeholder = { Text("persen = (real / target) * 100") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = showInMainTable,
                        onCheckedChange = { showInMainTable = it }
                    )
                    Text("Tampilkan sebagai kolom di Tabel Referensi Utama", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = includeInTotalUlp,
                        onCheckedChange = { includeInTotalUlp = it }
                    )
                    Text("Ikutkan ke perhitungan TARGET ALL & TOTAL KINERJA ULP", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        if (namaFitur.isNotBlank()) {
                            val newFeat = CustomFeature(
                                id = "FEAT-${(100..999).random()}",
                                namaFitur = namaFitur.trim(),
                                kategori = kategori,
                                targetMenu = targetMenu,
                                fieldBaru = fieldBaru.ifBlank { "target_baru, real_baru" },
                                rumusLogika = rumusLogika.ifBlank { "persen = (real / target) * 100" },
                                defaultTarget = defaultTarget.toIntOrNull() ?: 15,
                                showInMainTable = showInMainTable,
                                includeInTotalUlp = includeInTotalUlp
                            )
                            onAddFeature(newFeat)
                            namaFitur = ""
                            fieldBaru = ""
                            rumusLogika = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PlnNavyPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("create_custom_feature_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Simpan & Tambahkan ke Menu #${14 + customFeatures.size}", fontWeight = FontWeight.Bold)
                }
            }
        }

        // List of Active Features
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "DAFTAR MODUL AKTIF (${customFeatures.size})",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = PlnNavyPrimary
                )

                customFeatures.forEachIndexed { idx, feat ->
                    val menuNo = 14 + idx
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Slate50,
                        border = BorderStroke(0.5.dp, Slate200),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(PlnGold)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("MENU $menuNo", fontSize = 9.sp, fontWeight = FontWeight.Black, color = PlnNavyDark)
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(feat.namaFitur, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Row {
                                    IconButton(onClick = { onNavigateToCustomModule(feat.id) }) {
                                        Icon(Icons.Default.OpenInNew, contentDescription = "Buka", tint = Color(0xFF4F46E5), modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(onClick = { onRemoveFeature(feat.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = PlnRose, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }

                            Text("Field: ${feat.fieldBaru} • Default Target: ${feat.defaultTarget}", fontSize = 10.sp, color = Slate700)
                            Text("Rumus: ${feat.rumusLogika}", fontSize = 10.sp, color = PlnNavyPrimary)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = feat.showInMainTable,
                                        onCheckedChange = { onToggleShowInTable(feat.id, it) }
                                    )
                                    Text("Kolom Tabel Utama", fontSize = 10.sp)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = feat.includeInTotalUlp,
                                        onCheckedChange = { onToggleIncludeInTotal(feat.id, it) }
                                    )
                                    Text("Hitung Total ULP", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
