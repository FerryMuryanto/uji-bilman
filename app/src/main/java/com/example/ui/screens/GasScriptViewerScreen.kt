package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun GasScriptViewerScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf("code") } // "code", "setup", "index"

    val codeGsSnippet = remember {
        """
/**
 * CODE.GS — BACKEND GOOGLE APPS SCRIPT WEB APP & REST API
 * BILLMAN PERFORMANCE MONITORING P2DP — UP3 TEGAL
 */
function doGet(e) {
  var action = (e && e.parameter && e.parameter.action) ? e.parameter.action : "";
  if (action === "getOfficers") {
    var ulpId = e.parameter.ulpId || "TEGAL_TIMUR";
    return jsonResponse({ status: "success", ulpId: ulpId, data: getOfficersByUlp(ulpId) });
  }
  if (action === "getPicContacts") {
    return jsonResponse({ status: "success", data: getPicContactsData() });
  }
  if (action === "getRekap") {
    var ulpId = e.parameter.ulpId || "TEGAL_TIMUR";
    return jsonResponse({ status: "success", ulpId: ulpId, data: calculateRekapUlp(ulpId) });
  }

  var template = HtmlService.createTemplateFromFile("index");
  template.initialUlp = (e && e.parameter && e.parameter.ulp) ? e.parameter.ulp : "TEGAL_TIMUR";
  return template.evaluate()
    .setTitle("PLN BILLMAN MONITORING P2DP — UP3 TEGAL")
    .addMetaTag("viewport", "width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no")
    .setXFrameOptionsMode(HtmlService.XFrameOptionsMode.ALLOWALL);
}

function getOfficersByUlp(ulpId) {
  var ss = SpreadsheetApp.getActiveSpreadsheet();
  var sheet = ss.getSheetByName("Master_Petugas");
  if (!sheet) return [];
  var data = sheet.getDataRange().getValues();
  var officers = [];
  for (var i = 1; i < data.length; i++) {
    var row = data[i];
    if (!ulpId || row[3] === ulpId) {
      officers.push({
        no: row[0], kodePetugas: row[1], petugas: row[2],
        ulpId: row[3], ulpNama: row[4], wilayah: row[5],
        jumlahPelgn: Number(row[6]) || 0, targetAll: Number(row[7]) || 0,
        p2dpTarget: Number(row[8]) || 0, p2dpReal: Number(row[9]) || 0,
        prevTarget: Number(row[10]) || 0, prevReal: Number(row[11]) || 0,
        korTarget: Number(row[12]) || 0, korReal: Number(row[13]) || 0
      });
    }
  }
  return officers;
}

function jsonResponse(obj) {
  return ContentService.createTextOutput(JSON.stringify(obj))
    .setMimeType(ContentService.MimeType.JSON);
}
        """.trimIndent()
    }

    val setupGsSnippet = remember {
        """
/**
 * SETUP.GS — SETUP & INISIALISASI DATABASE SPREADSHEET
 * PT PLN (PERSERO) ELECTRICITY SERVICES — UP3 TEGAL
 */
function initialSetupSpreadsheet() {
  var ss = SpreadsheetApp.getActiveSpreadsheet();
  setupMasterPetugasSheet(ss);
  setupKontakPicSheet(ss);
  setupMonitoringHarianSheet(ss);
  setupRiwayatSnapshotSheet(ss);
  setupConfigKpiSheet(ss);
  SpreadsheetApp.getUi().alert("Setup Berhasil", "Seluruh sheet database & data 10 ULP telah dibuat.", SpreadsheetApp.getUi().ButtonSet.OK);
}

function setupKontakPicSheet(ss) {
  var sheet = getOrCreateSheet(ss, "Kontak_PIC");
  sheet.clear();
  var headers = ["NO", "ID_ULP", "NAMA_ULP", "NAMA_PIC", "JABATAN", "NO_WHATSAPP", "LINK_WHATSAPP"];
  sheet.appendRow(headers);
  var picList = [
    [1, "UP3_TEGAL", "KANTOR INDUK UP3 TEGAL", "Bpk. Ir. Joko Susanto, M.M.", "Manager Transaksi Energi & Koordinator P2DP", "628122981800", "=HYPERLINK(\"https://wa.me/628122981800\",\"Chat WhatsApp\")"],
    [2, "TEGAL_TIMUR", "ULP TEGAL TIMUR", "Bpk. Rahmat Hidayat, S.T.", "Supervisor Pelayanan Pelanggan & Billman", "628122981801", "=HYPERLINK(\"https://wa.me/628122981801\",\"Chat WhatsApp\")"],
    [3, "TEGAL_KOTA", "ULP TEGAL KOTA", "Ibu Sri Wahyuni, S.T.", "Supervisor Pelayanan Pelanggan & Billman", "628122981802", "=HYPERLINK(\"https://wa.me/628122981802\",\"Chat WhatsApp\")"],
    [4, "SLAWI", "ULP SLAWI", "Bpk. Agus Prasetyo, M.T.", "Supervisor Pelayanan Pelanggan & Billman", "628122981803", "=HYPERLINK(\"https://wa.me/628122981803\",\"Chat WhatsApp\")"],
    [5, "RANDUDONGKAL", "ULP RANDUDONGKAL", "Bpk. Hendro Siswanto", "Supervisor Pelayanan Pelanggan & Billman", "628122981804", "=HYPERLINK(\"https://wa.me/628122981804\",\"Chat WhatsApp\")"],
    [6, "JATIBARANG", "ULP JATIBARANG", "Ibu Dewi Lestari", "Supervisor Pelayanan Pelanggan & Billman", "628122981805", "=HYPERLINK(\"https://wa.me/628122981805\",\"Chat WhatsApp\")"],
    [7, "COMAL", "ULP COMAL", "Bpk. Fajar Ramadhan", "Supervisor Pelayanan Pelanggan & Billman", "628122981806", "=HYPERLINK(\"https://wa.me/628122981806\",\"Chat WhatsApp\")"],
    [8, "BREBES", "ULP BREBES", "Bpk. Tri Wibowo, S.T.", "Supervisor Pelayanan Pelanggan & Billman", "628122981807", "=HYPERLINK(\"https://wa.me/628122981807\",\"Chat WhatsApp\")"],
    [9, "PEMALANG", "ULP PEMALANG", "Bpk. Danang Kusuma", "Supervisor Pelayanan Pelanggan & Billman", "628122981808", "=HYPERLINK(\"https://wa.me/628122981808\",\"Chat WhatsApp\")"],
    [10, "BUMIAYU", "ULP BUMIAYU", "Bpk. Arif Firmansyah", "Supervisor Pelayanan Pelanggan & Billman", "628122981809", "=HYPERLINK(\"https://wa.me/628122981809\",\"Chat WhatsApp\")"],
    [11, "BALAPULANG", "ULP BALAPULANG", "Bpk. Sigit Purnomo", "Supervisor Pelayanan Pelanggan & Billman", "628122981810", "=HYPERLINK(\"https://wa.me/628122981810\",\"Chat WhatsApp\")"]
  ];
  sheet.getRange(2, 1, picList.length, headers.length).setValues(picList);
}
        """.trimIndent()
    }

    val indexHtmlSnippet = remember {
        """
<!DOCTYPE html>
<html lang="id">
<head>
  <meta charset="UTF-8">
  <title>BILLMAN MONITORING P2DP — UP3 TEGAL</title>
  <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-slate-100 text-slate-900">
  <!-- Header with WhatsApp PIC Direct Button -->
  <header class="bg-indigo-950 text-white p-4 flex justify-between items-center">
    <div class="flex items-center gap-3">
      <div class="w-10 h-10 rounded-xl bg-amber-400 text-slate-950 font-black flex items-center justify-center">PLN</div>
      <h1 class="text-base font-bold">BILLMAN MONITORING P2DP UP3 TEGAL</h1>
    </div>
    <a href="https://wa.me/628122981801?text=Halo%20PIC%20ULP" target="_blank" class="px-3 py-2 bg-emerald-600 text-white font-bold rounded-lg text-xs">
      Chat WhatsApp PIC
    </a>
  </header>
  <!-- Main Spreadsheet Table & WhatsApp Directory -->
  <main class="max-w-7xl mx-auto p-4">
    <!-- File index.html lengkap tersedia di folder root project -->
  </main>
</body>
</html>
        """.trimIndent()
    }

    fun copyCode(code: String, title: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(title, code)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$title berhasil disalin ke clipboard!", Toast.LENGTH_SHORT).show()
    }

    val currentContent = when (selectedTab) {
        "code" -> codeGsSnippet
        "setup" -> setupGsSnippet
        else -> indexHtmlSnippet
    }

    val currentTitle = when (selectedTab) {
        "code" -> "Code.gs"
        "setup" -> "Setup.gs"
        else -> "index.html"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate100)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Banner
        Card(
            colors = CardDefaults.cardColors(containerColor = PlnNavyPrimary),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "KODE GOOGLE APPS SCRIPT & WEB DEPLOYMENT",
                    color = PlnGold,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Code.gs • Setup.gs • index.html",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                )
                Text(
                    text = "Salin kode berikut untuk diterapkan langsung pada script.google.com sebagai web app.",
                    color = Slate300,
                    fontSize = 11.sp
                )
            }
        }

        // Tab Selector & Copy Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FilterChip(
                    selected = selectedTab == "code",
                    onClick = { selectedTab = "code" },
                    label = { Text("Code.gs") }
                )
                FilterChip(
                    selected = selectedTab == "setup",
                    onClick = { selectedTab = "setup" },
                    label = { Text("Setup.gs") }
                )
                FilterChip(
                    selected = selectedTab == "index",
                    onClick = { selectedTab = "index" },
                    label = { Text("index.html") }
                )
            }

            Button(
                onClick = { copyCode(currentContent, currentTitle) },
                colors = ButtonDefaults.buttonColors(containerColor = PlnEmerald),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("copy_gas_code_btn")
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Salin Kode", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Code Viewer
        Card(
            colors = CardDefaults.cardColors(containerColor = PlnNavyDark),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            val vScroll = rememberScrollState()
            val hScroll = rememberScrollState()

            Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                Text(
                    text = currentContent,
                    color = Color(0xFFF1F5F9),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier
                        .verticalScroll(vScroll)
                        .horizontalScroll(hScroll)
                )
            }
        }
    }
}
