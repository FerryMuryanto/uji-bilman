package com.example.data

import androidx.compose.ui.graphics.Color
import com.example.model.Thresholds

data class PerformanceStyle(
    val backgroundColor: Color,
    val textColor: Color,
    val label: String
)

object PerformanceHelper {
    val GreenDark = Color(0xFF16A34A)
    val GreenLight = Color(0xFF86EFAC)
    val Yellow = Color(0xFFFDE047)
    val Orange = Color(0xFFFB923C)
    val Red = Color(0xFFEF4444)
    val DarkText = Color(0xFF0F172A)
    val LightText = Color(0xFFFFFFFF)

    fun getStyle(pct: Double, real: Int, thresholds: Thresholds): PerformanceStyle {
        if (real == 0 || pct <= 0.0) {
            return PerformanceStyle(Red, LightText, "Sangat Rendah / Nol (0%)")
        }
        if (pct >= thresholds.hijauTua) {
            return PerformanceStyle(GreenDark, LightText, "Tercapai / Tinggi (≥100%)")
        }
        if (pct >= thresholds.hijauMuda) {
            return PerformanceStyle(GreenLight, DarkText, "Baik (80% - 99%)")
        }
        if (pct >= thresholds.kuning) {
            return PerformanceStyle(Yellow, DarkText, "Sedang (60% - 79%)")
        }
        if (pct >= thresholds.oranye) {
            return PerformanceStyle(Orange, DarkText, "Rendah (40% - 59%)")
        }
        return PerformanceStyle(Red, LightText, "Sangat Rendah (<40%)")
    }

    fun calcPct(real: Int, target: Int): Double {
        if (target <= 0) return 0.0
        return (real.toDouble() / target.toDouble()) * 100.0
    }

    fun fmtPct(value: Double): String {
        return String.format(java.util.Locale.GERMANY, "%.1f%%", value)
    }

    fun fmtNum(value: Int): String {
        return java.text.NumberFormat.getNumberInstance(java.util.Locale.GERMANY).format(value)
    }
}
