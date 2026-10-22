package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SettingsData
import com.example.model.Thresholds
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
    currentSettings: SettingsData,
    onSaveSettings: (SettingsData) -> Unit,
    modifier: Modifier = Modifier
) {
    var bulan by remember { mutableStateOf(currentSettings.bulan) }
    var tahun by remember { mutableStateOf(currentSettings.tahun) }
    var waktuUpdate by remember { mutableStateOf(currentSettings.waktuUpdate) }
    var formulaMode by remember { mutableStateOf(currentSettings.formulaMode) }
    var bobotP2DP by remember { mutableStateOf(currentSettings.bobotP2DP.toString()) }
    var bobotPrev by remember { mutableStateOf(currentSettings.bobotPrev.toString()) }
    var bobotKor by remember { mutableStateOf(currentSettings.bobotKor.toString()) }

    var thHijauTua by remember { mutableStateOf(currentSettings.thresholds.hijauTua.toString()) }
    var thHijauMuda by remember { mutableStateOf(currentSettings.thresholds.hijauMuda.toString()) }
    var thKuning by remember { mutableStateOf(currentSettings.thresholds.kuning.toString()) }
    var thOranye by remember { mutableStateOf(currentSettings.thresholds.oranye.toString()) }

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
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "PENGATURAN TARGET, RUMUS & AMBANG WARNA",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = PlnNavyPrimary
                )
                Text(
                    text = "Konfigurasi rumus evaluasi performa, bobot komponen, dan ambang batas warna.",
                    fontSize = 11.sp,
                    color = Slate700
                )

                // 1. Identitas Laporan
                Text("1. IDENTITAS LAPORAN", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = PlnNavyDark)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = bulan,
                        onValueChange = { bulan = it.uppercase() },
                        label = { Text("Bulan") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = tahun,
                        onValueChange = { tahun = it },
                        label = { Text("Tahun") },
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = waktuUpdate,
                    onValueChange = { waktuUpdate = it },
                    label = { Text("Timestamp Update Terakhir") },
                    modifier = Modifier.fillMaxWidth()
                )

                Divider(color = Slate200)

                // 2. Formula Mode
                Text("2. RUMUS PERSENTASE TOTAL KINERJA", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = PlnNavyDark)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = formulaMode == "proporsional",
                        onClick = { formulaMode = "proporsional" },
                        label = { Text("Proporsional (Real/Target)") }
                    )
                    FilterChip(
                        selected = formulaMode == "bobot",
                        onClick = { formulaMode = "bobot" },
                        label = { Text("Bobot Tertimbang") }
                    )
                }

                if (formulaMode == "bobot") {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = bobotP2DP,
                            onValueChange = { bobotP2DP = it },
                            label = { Text("P2DP (%)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = bobotPrev,
                            onValueChange = { bobotPrev = it },
                            label = { Text("Prev (%)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = bobotKor,
                            onValueChange = { bobotKor = it },
                            label = { Text("Kor (%)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Divider(color = Slate200)

                // 3. Thresholds
                Text("3. AMBANG BATAS INDIKATOR WARNA (%)", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = PlnNavyDark)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = thHijauTua,
                        onValueChange = { thHijauTua = it },
                        label = { Text("Hijau Tua (≥ %)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = thHijauMuda,
                        onValueChange = { thHijauMuda = it },
                        label = { Text("Hijau Muda (≥ %)") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = thKuning,
                        onValueChange = { thKuning = it },
                        label = { Text("Kuning (≥ %)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = thOranye,
                        onValueChange = { thOranye = it },
                        label = { Text("Oranye (≥ %)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Button(
                    onClick = {
                        val newSettings = currentSettings.copy(
                            bulan = bulan,
                            tahun = tahun,
                            waktuUpdate = waktuUpdate,
                            formulaMode = formulaMode,
                            bobotP2DP = bobotP2DP.toIntOrNull() ?: 40,
                            bobotPrev = bobotPrev.toIntOrNull() ?: 35,
                            bobotKor = bobotKor.toIntOrNull() ?: 25,
                            thresholds = Thresholds(
                                hijauTua = thHijauTua.toIntOrNull() ?: 100,
                                hijauMuda = thHijauMuda.toIntOrNull() ?: 80,
                                kuning = thKuning.toIntOrNull() ?: 60,
                                oranye = thOranye.toIntOrNull() ?: 40
                            )
                        )
                        onSaveSettings(newSettings)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PlnNavyPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_settings_btn")
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Simpan Konfigurasi KPI", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
