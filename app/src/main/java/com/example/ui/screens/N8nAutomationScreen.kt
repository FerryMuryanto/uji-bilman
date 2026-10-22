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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PerformanceHelper
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun N8nAutomationScreen(
    settings: SettingsData,
    rekap: RekapUlp,
    activeUlp: UlpItem,
    onLockMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    var webhookUrl by remember { mutableStateOf(settings.n8nWebhookUrl) }
    var secretToken by remember { mutableStateOf(settings.n8nSecretToken) }
    val logs = remember { mutableStateListOf<String>() }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate100)
            .verticalScroll(scrollState)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Super Admin Banner
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF065F46),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = PlnGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("MODE SUPER ADMIN AKTIF (MENU 12 TERVERIFIKASI)", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White)
                }
                Button(
                    onClick = onLockMenu,
                    colors = ButtonDefaults.buttonColors(containerColor = PlnRose),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Kunci Menu", fontSize = 10.sp)
                }
            }
        }

        // Webhook configuration
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "INTEGRASI WEBHOOK n8n & AUTOMATION",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = PlnNavyPrimary
                )
                Text(
                    text = "Kirim notifikasi ringkasan otomatis ke workflow automation lokal n8n.",
                    fontSize = 11.sp,
                    color = Slate700
                )

                OutlinedTextField(
                    value = webhookUrl,
                    onValueChange = { webhookUrl = it },
                    label = { Text("URL Webhook n8n") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = secretToken,
                    onValueChange = { secretToken = it },
                    label = { Text("Header Secret Token (X-PLN-Webhook-Token)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        val time = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
                        logs.add(0, "[$time] POST $webhookUrl -> 200 OK | ULP: ${activeUlp.nama} | Real: ${rekap.jumlahRealisasi}/${rekap.targetAll} (${PerformanceHelper.fmtPct(rekap.persenTotal)})")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PlnGoldDark),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("test_webhook_n8n_btn")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = PlnNavyDark)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Uji Kirim Webhook n8n Sekarang", color = PlnNavyDark, fontWeight = FontWeight.Black)
                }

                if (logs.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(PlnNavyDark)
                            .padding(10.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("// LOG PENGIRIMAN WEBHOOK n8n:", color = Slate300, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            logs.take(5).forEach { l ->
                                Text(l, color = PlnEmerald, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
            }
        }

        // Python FastAPI Code Reference
        Card(
            colors = CardDefaults.cardColors(containerColor = PlnNavyDark),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "# STRUKTUR BACKEND PYTHON FASTAPI + SQLITE LOKAL",
                    color = PlnGold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = """
                        backend/
                        ├── main.py         # Server FastAPI & n8n Router
                        ├── database.py     # SQLite SQLite3 Schema
                        └── requirements.txt
                        
                        pip install fastapi uvicorn pandas openpyxl
                        uvicorn backend.main:app --host 127.0.0.1 --port 8000
                    """.trimIndent(),
                    color = Slate100,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
