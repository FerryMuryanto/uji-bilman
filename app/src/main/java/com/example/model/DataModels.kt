package com.example.model

data class UlpItem(
    val id: String,
    val nama: String,
    val kodePrefix: String,
    val jmlPetugas: Int,
    val koordinator: String,
    val picName: String = "",
    val picPhone: String = "",
    val picJabatan: String = "Supervisor Pelayanan Pelanggan & Billman"
)

data class UserCredential(
    val username: String,
    val password: String,
    val role: String,
    val name: String,
    val ulpId: String,
    val canSwitchUlp: Boolean
)

data class PeriodMetric(
    val p2dp: Int,
    val prev: Int,
    val kor: Int
)

data class CustomMetricVal(
    val target: Int,
    val real: Int
)

data class Officer(
    val id: Int,
    val no: Int,
    val kodePetugas: String,
    val petugas: String,
    val wilayah: String,
    val jumlahPelgn: Int,
    val targetAll: Int,
    val p2dpTarget: Int,
    val p2dpReal: Int,
    val prevTarget: Int,
    val prevReal: Int,
    val korTarget: Int,
    val korReal: Int,
    val pagi: PeriodMetric,
    val siang: PeriodMetric,
    val sore: PeriodMetric,
    val customData: Map<String, CustomMetricVal> = emptyMap()
)

data class Thresholds(
    val hijauTua: Int = 100,
    val hijauMuda: Int = 80,
    val kuning: Int = 60,
    val oranye: Int = 40
)

data class SettingsData(
    val bulan: String = "OKTOBER",
    val tahun: String = "2026",
    val waktuUpdate: String = "22/10/2026 10.55",
    val periodeAktif: String = "Pagi",
    val formulaMode: String = "proporsional", // "proporsional" or "bobot"
    val bobotP2DP: Int = 40,
    val bobotPrev: Int = 35,
    val bobotKor: Int = 25,
    val targetAllMode: String = "sum_komponen",
    val thresholds: Thresholds = Thresholds(),
    val n8nWebhookUrl: String = "http://localhost:5678/webhook/pln-billman-p2dp-tegal-timur",
    val n8nSecretToken: String = "PLN-ES-TEGALTIMUR-2026",
    val autoTriggerN8n: Boolean = true
)

data class HistorySnapshot(
    val id: String,
    val tanggal: String,
    val jam: String,
    val periode: String,
    val namaFile: String,
    val jumlahPetugas: Int,
    val totalRealisasi: Int,
    val persenTotal: Double,
    val deltaRealisasi: String,
    val user: String
)

data class CustomFeature(
    val id: String,
    val namaFitur: String,
    val kategori: String,
    val targetMenu: String,
    val fieldBaru: String,
    val rumusLogika: String,
    val defaultTarget: Int = 15,
    val showInMainTable: Boolean = true,
    val includeInTotalUlp: Boolean = false,
    val createdAt: String = "21/10/2026 11.00"
)

data class CustomComputedMetric(
    val target: Int,
    val real: Int,
    val persen: Double
)

data class ProcessedOfficer(
    val officer: Officer,
    val ranking: Int,
    val activeP2dpReal: Int,
    val activePrevReal: Int,
    val activeKorReal: Int,
    val p2dpPersen: Double,
    val prevPersen: Double,
    val korPersen: Double,
    val jumlahRealisasi: Int,
    val finalTargetAll: Int,
    val persenTotal: Double,
    val deltaPagiKeSiang: Int,
    val deltaSiangKeSore: Int,
    val customMetrics: Map<String, CustomComputedMetric> = emptyMap()
)

data class CustomTotalMetric(
    val target: Int,
    val real: Int,
    val persen: Double,
    val capai100: Int
)

data class RekapUlp(
    val jumlahPelgn: Int,
    val targetAll: Int,
    val p2dpTarget: Int,
    val p2dpReal: Int,
    val p2dpPersen: Double,
    val prevTarget: Int,
    val prevReal: Int,
    val prevPersen: Double,
    val korTarget: Int,
    val korReal: Int,
    val korPersen: Double,
    val jumlahRealisasi: Int,
    val persenTotal: Double,
    val pagiTotal: Int,
    val siangTotal: Int,
    val soreTotal: Int,
    val count100: Int,
    val countZero: Int,
    val countBelumTarget: Int,
    val customTotals: Map<String, CustomTotalMetric> = emptyMap()
)

data class ChatMessage(
    val id: String,
    val sender: String, // "user" or "model"
    val text: String,
    val timestamp: String = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
)
