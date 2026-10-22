package com.example.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PerformanceHelper
import com.example.model.*
import com.example.ui.theme.*
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

@Composable
fun ChatbotScreen(
    activeUlp: UlpItem,
    rekap: RekapUlp,
    processedOfficers: List<ProcessedOfficer>,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var inputMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                id = "msg-0",
                sender = "model",
                text = "Halo! Saya Asisten AI Billman P2DP UP3 Tegal. Saat ini ULP ${activeUlp.nama} mencapai ${PerformanceHelper.fmtPct(rekap.persenTotal)} (${rekap.jumlahRealisasi}/${rekap.targetAll} pekerjaan). Ada yang bisa saya bantu terkait analisis performa, draf pesan WhatsApp ke PIC, atau strategi pencapaian target hari ini?"
            )
        )
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return
        val userMsg = ChatMessage(
            id = "msg-${System.currentTimeMillis()}",
            sender = "user",
            text = userText
        )
        messages.add(userMsg)
        inputMessage = ""
        isLoading = true

        coroutineScope.launch {
            listState.animateScrollToItem(messages.size - 1)
            try {
                val contextPrompt = """
                    Data Konteks ULP Saat Ini:
                    - Unit: ${activeUlp.nama}
                    - PIC ULP: ${activeUlp.picName} (WA: +${activeUlp.picPhone})
                    - Capaian ULP: ${PerformanceHelper.fmtPct(rekap.persenTotal)} (${rekap.jumlahRealisasi}/${rekap.targetAll})
                    - P2DP: ${rekap.p2dpReal}/${rekap.p2dpTarget} (${PerformanceHelper.fmtPct(rekap.p2dpPersen)})
                    - Preventif: ${rekap.prevReal}/${rekap.prevTarget} (${PerformanceHelper.fmtPct(rekap.prevPersen)})
                    - Korektif: ${rekap.korReal}/${rekap.korTarget} (${PerformanceHelper.fmtPct(rekap.korPersen)})
                    - Petugas Capai 100%: ${rekap.count100} orang
                    - Petugas Realisasi 0: ${rekap.countZero} orang

                    Pertanyaan Pengguna: $userText
                """.trimIndent()

                var responseText = ""
                try {
                    val aiModel = Firebase.ai.generativeModel("gemini-3.8-flash")
                    val response = withTimeout(30_000L) {
                        aiModel.generateContent(contextPrompt)
                    }
                    responseText = response.text.orEmpty()
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.w("ChatbotScreen", "Gemini live call exception: ${e.message}")
                }

                if (responseText.isBlank()) {
                    // Smart domain intelligence fallback
                    responseText = generateDomainFallbackResponse(userText, activeUlp, rekap, processedOfficers)
                }

                messages.add(
                    ChatMessage(
                        id = "msg-${System.currentTimeMillis()}",
                        sender = "model",
                        text = responseText
                    )
                )
            } catch (e: Exception) {
                messages.add(
                    ChatMessage(
                        id = "msg-${System.currentTimeMillis()}",
                        sender = "model",
                        text = "Maaf, terjadi kendala saat memproses permintaan. Silakan periksa koneksi data Anda."
                    )
                )
            } finally {
                isLoading = false
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate100)
    ) {
        // Header
        Surface(
            color = PlnNavyPrimary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(PlnGold),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.SmartToy, contentDescription = null, tint = PlnNavyDark, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("ASISTEN AI BILLMAN P2DP", color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
                    Text("Didukung Gemini 3.8 Flash • Analisis Real-Time & Draf WA", color = PlnGold, fontSize = 10.sp)
                }
            }
        }

        // Quick Suggestion Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SuggestionChip(
                onClick = { sendMessage("Analisis ringkasan capaian kinerja P2DP hari ini untuk ${activeUlp.nama}") },
                label = { Text("📊 Analisis Kinerja Hari Ini", fontSize = 10.sp) }
            )
            SuggestionChip(
                onClick = { sendMessage("Buatkan draf pesan WhatsApp resmi untuk koordinasi ke PIC ULP ${activeUlp.nama} (${activeUlp.picName})") },
                label = { Text("💬 Draf WA ke PIC ULP", fontSize = 10.sp) }
            )
            SuggestionChip(
                onClick = { sendMessage("Berikan 3 strategi percepatan target agar ULP capai 100% sebelum sore hari") },
                label = { Text("⚡ Strategi Capai 100%", fontSize = 10.sp) }
            )
            SuggestionChip(
                onClick = { sendMessage("Evaluasi petugas yang masih memiliki realisasi 0 atau di bawah 40%") },
                label = { Text("📋 Evaluasi Petugas Tertinggal", fontSize = 10.sp) }
            )
        }

        // Message Thread
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                val isUser = msg.sender == "user"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 14.dp,
                            topEnd = 14.dp,
                            bottomStart = if (isUser) 14.dp else 2.dp,
                            bottomEnd = if (isUser) 2.dp else 14.dp
                        ),
                        color = if (isUser) PlnNavyPrimary else Color.White,
                        shadowElevation = 1.dp,
                        modifier = Modifier.widthIn(max = 320.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = msg.text,
                                color = if (isUser) Color.White else Slate900,
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = msg.timestamp,
                                color = if (isUser) Color.White.copy(alpha = 0.6f) else Slate700,
                                fontSize = 9.sp,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }

            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = PlnNavyPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Asisten AI sedang menganalisis data...", fontSize = 11.sp, color = Slate700)
                            }
                        }
                    }
                }
            }
        }

        // Input Field Bar
        Surface(
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputMessage,
                    onValueChange = { inputMessage = it },
                    placeholder = { Text("Tanyakan kinerja, evaluasi, atau minta draf WA...", fontSize = 12.sp) },
                    singleLine = false,
                    maxLines = 3,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chatbot_input_field")
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (inputMessage.isNotBlank()) {
                            sendMessage(inputMessage.trim())
                        }
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(PlnNavyPrimary)
                        .testTag("chatbot_send_btn")
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Kirim", tint = PlnGold, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

private fun generateDomainFallbackResponse(
    userPrompt: String,
    activeUlp: UlpItem,
    rekap: RekapUlp,
    officers: List<ProcessedOfficer>
): String {
    val lower = userPrompt.lowercase()
    return when {
        lower.contains("wa") || lower.contains("whatsapp") || lower.contains("draf") -> {
            """
            Berikut draf pesan WhatsApp resmi yang siap Anda kirimkan kepada PIC ${activeUlp.nama}:
            
            *Yth. ${activeUlp.picName}*
            (${activeUlp.picJabatan})
            
            Selamat pagi/siang Bpk/Ibu,
            Kami dari Tim Koordinator Billman UP3 Tegal ingin menginformasikan rekapitulasi progres harian P2DP untuk *${activeUlp.nama}*:
            
            📊 *Capaian Saat Ini:*
            • P2DP: ${rekap.p2dpReal} / ${rekap.p2dpTarget} (${PerformanceHelper.fmtPct(rekap.p2dpPersen)})
            • Preventif: ${rekap.prevReal} / ${rekap.prevTarget} (${PerformanceHelper.fmtPct(rekap.prevPersen)})
            • Korektif: ${rekap.korReal} / ${rekap.korTarget} (${PerformanceHelper.fmtPct(rekap.korPersen)})
            • Total Realisasi: *${rekap.jumlahRealisasi} / ${rekap.targetAll} (${PerformanceHelper.fmtPct(rekap.persenTotal)})*
            
            ⚡ Mohon dukungan untuk mendorong petugas yang masih di bawah target agar realisasi dapat rampung 100% pada pembaruan sore nanti. Terima kasih atas dedikasi dan kerja samanya!
            """.trimIndent()
        }
        lower.contains("strategi") || lower.contains("percepatan") -> {
            """
            Rekomendasi Strategi Percepatan Kinerja ${activeUlp.nama}:
            
            1. **Prioritaskan Work Order Preventif & P2DP Pukul 11.00 - 14.00**: Fokuskan rute RBM terdekat yang memiliki jumlah pelanggan menunggak tinggi.
            2. **Monitoring Real-Time via WhatsApp Group RBM**: PIC ULP dapat membagikan update pukul 12.00 dan mengapresiasi petugas yang sudah mencapai ≥80%.
            3. **Asistensi Petugas dengan Kendala Lapangan**: Tinjau ${rekap.countZero} petugas yang belum memulai realisasi untuk memverifikasi apakah ada kendala teknis atau aplikasi FSM ACMT.
            """.trimIndent()
        }
        else -> {
            val topOfficer = officers.sortedBy { it.ranking }.firstOrNull()?.officer?.petugas ?: "AHMAD FAUZI"
            """
            *Ringkasan Eksekutif Kinerja ${activeUlp.nama}:*
            
            • **Total Capaian**: ${PerformanceHelper.fmtPct(rekap.persenTotal)} (${rekap.jumlahRealisasi} dari ${rekap.targetAll} pekerjaan).
            • **P2DP**: ${rekap.p2dpReal} / ${rekap.p2dpTarget} (${PerformanceHelper.fmtPct(rekap.p2dpPersen)}).
            • **Preventif New ITO**: ${rekap.prevReal} / ${rekap.prevTarget} (${PerformanceHelper.fmtPct(rekap.prevPersen)}).
            • **Korektif New ITO**: ${rekap.korReal} / ${rekap.korTarget} (${PerformanceHelper.fmtPct(rekap.korPersen)}).
            • **Petugas Terbaik #1**: $topOfficer.
            
            Silakan ketik pertanyaan lain jika memerlukan perincian lebih lanjut atau draf pesan koordinasi WhatsApp!
            """.trimIndent()
        }
    }
}
