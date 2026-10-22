/**
 * ============================================================================
 * CODE.GS — BACKEND GOOGLE APPS SCRIPT WEB APP & REST API
 * BILLMAN PERFORMANCE MONITORING P2DP — UP3 TEGAL
 * ============================================================================
 */

function doGet(e) {
  var action = (e && e.parameter && e.parameter.action) ? e.parameter.action : "";

  // REST API JSON Endpoints
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

  // Default: Render HTML Web App (index.html)
  var template = HtmlService.createTemplateFromFile("index");
  template.initialUlp = (e && e.parameter && e.parameter.ulp) ? e.parameter.ulp : "TEGAL_TIMUR";

  return template.evaluate()
    .setTitle("PLN BILLMAN MONITORING P2DP — UP3 TEGAL")
    .addMetaTag("viewport", "width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no")
    .setXFrameOptionsMode(HtmlService.XFrameOptionsMode.ALLOWALL);
}

function doPost(e) {
  try {
    var body = e.postData.contents;
    var data = JSON.parse(body);
    var action = data.action || "";

    if (action === "updateProgress") {
      var result = saveDailyProgress(data);
      return jsonResponse({ status: "success", message: "Progress berhasil disimpan", result: result });
    }

    if (action === "updateOfficer") {
      var ok = updateOfficerData(data.officer);
      return jsonResponse({ status: ok ? "success" : "error", message: ok ? "Petugas diperbarui" : "Petugas tidak ditemukan" });
    }

    if (action === "triggerN8n") {
      var res = sendWebhookToN8n(data.payload);
      return jsonResponse({ status: "success", n8nResponse: res });
    }

    return jsonResponse({ status: "error", message: "Action tidak dikenal" });
  } catch (err) {
    return jsonResponse({ status: "error", error: err.toString() });
  }
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
        no: row[0],
        kodePetugas: row[1],
        petugas: row[2],
        ulpId: row[3],
        ulpNama: row[4],
        wilayah: row[5],
        jumlahPelgn: Number(row[6]) || 0,
        targetAll: Number(row[7]) || 0,
        p2dpTarget: Number(row[8]) || 0,
        p2dpReal: Number(row[9]) || 0,
        prevTarget: Number(row[10]) || 0,
        prevReal: Number(row[11]) || 0,
        korTarget: Number(row[12]) || 0,
        korReal: Number(row[13]) || 0
      });
    }
  }
  return officers;
}

function getPicContactsData() {
  var ss = SpreadsheetApp.getActiveSpreadsheet();
  var sheet = ss.getSheetByName("Kontak_PIC");
  if (!sheet) return [];

  var data = sheet.getDataRange().getValues();
  var contacts = [];

  for (var i = 1; i < data.length; i++) {
    var row = data[i];
    var phone = String(row[5]).replace(/[^0-9]/g, "");
    contacts.push({
      no: row[0],
      ulpId: row[1],
      namaUlp: row[2],
      picName: row[3],
      jabatan: row[4],
      whatsappNumber: phone,
      whatsappLink: "https://wa.me/" + phone + "?text=" + encodeURIComponent("Halo " + row[3] + ", koordinasi monitoring Billman P2DP " + row[2])
    });
  }
  return contacts;
}

function calculateRekapUlp(ulpId) {
  var officers = getOfficersByUlp(ulpId);
  var totals = {
    jumlahPelgn: 0,
    targetAll: 0,
    p2dpTarget: 0,
    p2dpReal: 0,
    prevTarget: 0,
    prevReal: 0,
    korTarget: 0,
    korReal: 0,
    jumlahRealisasi: 0
  };

  officers.forEach(function(o) {
    totals.jumlahPelgn += o.jumlahPelgn;
    totals.targetAll += o.targetAll;
    totals.p2dpTarget += o.p2dpTarget;
    totals.p2dpReal += o.p2dpReal;
    totals.prevTarget += o.prevTarget;
    totals.prevReal += o.prevReal;
    totals.korTarget += o.korTarget;
    totals.korReal += o.korReal;
    totals.jumlahRealisasi += (o.p2dpReal + o.prevReal + o.korReal);
  });

  totals.p2dpPersen = totals.p2dpTarget > 0 ? (totals.p2dpReal / totals.p2dpTarget) * 100 : 0;
  totals.prevPersen = totals.prevTarget > 0 ? (totals.prevReal / totals.prevTarget) * 100 : 0;
  totals.korPersen = totals.korTarget > 0 ? (totals.korReal / totals.korTarget) * 100 : 0;
  totals.persenTotal = totals.targetAll > 0 ? (totals.jumlahRealisasi / totals.targetAll) * 100 : 0;

  return totals;
}

function saveDailyProgress(data) {
  var ss = SpreadsheetApp.getActiveSpreadsheet();
  var sheet = ss.getSheetByName("Riwayat_Snapshot");
  if (!sheet) return false;

  var snapshotId = "SNAP-" + Utilities.formatDate(new Date(), "GMT+7", "yyyyMMdd-HHmm");
  var tanggal = Utilities.formatDate(new Date(), "GMT+7", "dd/MM/yyyy");
  var jam = Utilities.formatDate(new Date(), "GMT+7", "HH.mm");

  sheet.appendRow([
    snapshotId,
    tanggal,
    jam,
    data.periode || "Pagi",
    data.namaFile || "UPDATE_MANUAL.xlsx",
    data.namaUlp || "ULP TEGAL TIMUR",
    data.jumlahPetugas || 46,
    data.totalRealisasi || 0,
    (data.persenTotal ? data.persenTotal.toFixed(2) + "%" : "0%"),
    data.delta || "+0",
    data.operator || "Admin Billman"
  ]);

  return { snapshotId: snapshotId, tanggal: tanggal, jam: jam };
}

function sendWebhookToN8n(payload) {
  var ss = SpreadsheetApp.getActiveSpreadsheet();
  var configSheet = ss.getSheetByName("Config_KPI");
  var webhookUrl = "http://localhost:5678/webhook/pln-billman-p2dp-tegal-timur";
  var secretToken = "PLN-ES-TEGALTIMUR-2026";

  if (configSheet) {
    var values = configSheet.getDataRange().getValues();
    values.forEach(function(row) {
      if (row[0] === "WEBHOOK_N8N_URL") webhookUrl = row[1];
      if (row[0] === "WEBHOOK_SECRET_TOKEN") secretToken = row[1];
    });
  }

  var options = {
    method: "post",
    contentType: "application/json",
    headers: { "X-PLN-Webhook-Token": secretToken },
    payload: JSON.stringify(payload),
    muteHttpExceptions: true
  };

  try {
    var response = UrlFetchApp.fetch(webhookUrl, options);
    return response.getContentText();
  } catch (e) {
    return "Error webhook: " + e.toString();
  }
}

function jsonResponse(obj) {
  return ContentService.createTextOutput(JSON.stringify(obj))
    .setMimeType(ContentService.MimeType.JSON);
}

function include(filename) {
  return HtmlService.createHtmlOutputFromFile(filename).getContent();
}
