package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PerformanceHelper
import com.example.model.Thresholds
import com.example.ui.theme.*

@Composable
fun PerformanceBadge(
    pct: Double,
    real: Int,
    thresholds: Thresholds,
    modifier: Modifier = Modifier,
    showPercentOnly: Boolean = false,
    textPrefix: String = ""
) {
    val style = PerformanceHelper.getStyle(pct, real, thresholds)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(style.backgroundColor)
            .padding(horizontal = 6.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        val label = if (showPercentOnly) {
            PerformanceHelper.fmtPct(pct)
        } else if (textPrefix.isNotEmpty()) {
            "$textPrefix ${PerformanceHelper.fmtPct(pct)}"
        } else {
            "$real (${PerformanceHelper.fmtPct(pct)})"
        }
        Text(
            text = label,
            color = style.textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    badgeColor: Color = Slate100,
    valueColor: Color = Slate900
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )
                if (icon != null) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(badgeColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = valueColor, modifier = Modifier.size(14.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = valueColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = Color(0xFF475569)
            )
        }
    }
}

@Composable
fun LegendBar(
    thresholds: Thresholds,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = "LEGENDA INDIKATOR KINERJA:",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = Slate900,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                LegendChip("≥${thresholds.hijauTua}% (Tercapai)", PerformanceHelper.GreenDark, Color.White, Modifier.weight(1f))
                LegendChip("${thresholds.hijauMuda}%-${thresholds.hijauTua-1}%", PerformanceHelper.GreenLight, Slate900, Modifier.weight(1f))
                LegendChip("${thresholds.kuning}%-${thresholds.hijauMuda-1}%", PerformanceHelper.Yellow, Slate900, Modifier.weight(1f))
                LegendChip("${thresholds.oranye}%-${thresholds.kuning-1}%", PerformanceHelper.Orange, Slate900, Modifier.weight(1f))
                LegendChip("<${thresholds.oranye}% / Nol", PerformanceHelper.Red, Color.White, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun LegendChip(label: String, bg: Color, fg: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .padding(vertical = 4.dp, horizontal = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = fg,
            maxLines = 1
        )
    }
}

@Composable
fun SecurityAuthDialog(
    isOpen: Boolean,
    title: String,
    prompt: String,
    onDismiss: () -> Unit,
    onVerify: (String) -> Boolean
) {
    if (!isOpen) return

    var inputCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = PlnNavyPrimary)
                Spacer(Modifier.width(8.dp))
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = prompt, fontSize = 12.sp, color = Slate700)
                OutlinedTextField(
                    value = inputCode,
                    onValueChange = {
                        inputCode = it
                        errorMessage = ""
                    },
                    label = { Text("Kode / Sandi Otorisasi") },
                    singleLine = true,
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle password visibility"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("security_input_code")
                )
                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = PlnRose, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val ok = onVerify(inputCode)
                    if (!ok) {
                        errorMessage = "Kode otorisasi tidak valid! Akses ditolak."
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PlnNavyPrimary),
                modifier = Modifier.testTag("security_confirm_btn")
            ) {
                Text("Verifikasi & Buka")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
