/**
 * ============================================================================
 * SETUP.GS — SETUP & INISIALISASI SPREADSHEET DATABASE BILLMAN P2DP
 * PT PLN (PERSERO) ELECTRICITY SERVICES — AREA UP3 TEGAL
 * ============================================================================
 */

function initialSetupSpreadsheet() {
  var ss = SpreadsheetApp.getActiveSpreadsheet();
  if (!ss) {
    Logger.log("Error: Silakan buat Apps Script yang terikat (container-bound) ke Google Sheets.");
    return;
  }

  setupMasterPetugasSheet(ss);
  setupKontakPicSheet(ss);
  setupMonitoringHarianSheet(ss);
  setupRiwayatSnapshotSheet(ss);
  setupConfigKpiSheet(ss);

  SpreadsheetApp.getUi().alert(
    "Setup Database Berhasil!",
    "Seluruh sheet (Master_Petugas, Kontak_PIC, Monitoring_Harian, Riwayat_Snapshot, Config_KPI) dan data awal 10 ULP telah dibuat & diformat.",
    SpreadsheetApp.getUi().ButtonSet.OK
  );
}

function setupMasterPetugasSheet(ss) {
  var sheet = getOrCreateSheet(ss, "Master_Petugas");
  sheet.clear();

  var headers = [
    "NO", "KODE_PETUGAS", "NAMA_PETUGAS", "ULP_ID", "NAMA_ULP", 
    "WILAYAH_RBM", "JML_PELANGGAN", "TARGET_ALL", "TARGET_P2DP", "REALISASI_P2DP",
    "TARGET_PREVENTIF", "REALISASI_PREVENTIF", "TARGET_KOREKTIF", "REALISASI_KOREKTIF"
  ];
  sheet.appendRow(headers);

  var headerRange = sheet.getRange(1, 1, 1, headers.length);
  headerRange.setBackground("#1E1B4B")
             .setFontColor("#FFFFFF")
             .setFontWeight("bold")
             .setHorizontalAlignment("center");

  var officerNames = [
    "AHMAD FAUZI", "BUDI SANTOSO", "CAHYO NUGROHO", "DEDI KURNIAWAN", "EKO PRASETYO",
    "FAJAR HIDAYAT", "GILANG RAMADHAN", "HENDRA WIJAYA", "INDRA LESMANA", "JOKO SUSILO",
    "KHAIRUL ANAM", "LUKMAN HAKIM", "MUHAMMAD RIZKI", "NANANG KOSIM", "OKI SETIAWAN",
    "PRATAMA PUTRA", "QOMARUDIN", "RENDI SAPUTRA", "SLAMET RIYADI", "TAUFIK HIDAYAT",
    "USMAN ALI", "VICKY PRADANA", "WAHYU HIDAYAT", "YUDI PRATAMA", "ZAINAL ABIDIN",
    "AGUS SALIM", "BAMBANG IRAWAN", "CANDRA WIJAYA", "DANI SETIAWAN", "EDI PURWANTO",
    "FIRMANSYAH", "GUNAWAN", "HARIYANTO", "IRFAN MAULANA", "JUNAEDI",
    "KUSNADI", "M. ARIFIN", "NUR HIDAYAT", "PANJI KUSUMA", "RAHMAT HIDAYAT",
    "SAMSUL ARIFIN", "TEGUH SANTOSO", "UJANG SUPRIYADI", "WAWAN SETIAWAN", "YOGI PRATAMA",
    "ZULKIFLI"
  ];

  var ulpList = [
    { id: "TEGAL_TIMUR", nama: "ULP TEGAL TIMUR", prefix: "BM-TGT" },
    { id: "TEGAL_KOTA", nama: "ULP TEGAL KOTA", prefix: "BM-TGK" },
    { id: "SLAWI", nama: "ULP SLAWI", prefix: "BM-SLW" },
    { id: "RANDUDONGKAL", nama: "ULP RANDUDONGKAL", prefix: "BM-RDD" },
    { id: "JATIBARANG", nama: "ULP JATIBARANG", prefix: "BM-JTB" },
    { id: "COMAL", nama: "ULP COMAL", prefix: "BM-CML" },
    { id: "BREBES", nama: "ULP BREBES", prefix: "BM-BRB" },
    { id: "PEMALANG", nama: "ULP PEMALANG", prefix: "BM-PML" },
    { id: "BUMIAYU", nama: "ULP BUMIAYU", prefix: "BM-BMY" },
    { id: "BALAPULANG", nama: "ULP BALAPULANG", prefix: "BM-BLP" }
  ];

  var rows = [];
  var globalNo = 1;

  ulpList.forEach(function(u, uIdx) {
    officerNames.forEach(function(name, idx) {
      var seed = ((idx + uIdx * 7) * 17 + 13) % 100;
      var pelgn = 1150 + (((idx + uIdx * 7) * 29) % 450);
      var p2dpT = 25 + ((idx + uIdx) % 3) * 5;
      var prevT = 20 + ((idx + uIdx) % 2) * 5;
      var korT = 10 + ((idx + uIdx) % 2) * 5;
      var targetAll = p2dpT + prevT + korT;

      var perfFactor = 0.82;
      if (idx % 9 === 0) perfFactor = 1.05;
      else if (idx === 41 || idx === 44) perfFactor = 0.0;
      else if (idx % 7 === 0) perfFactor = 0.45;

      var p2dpR = Math.round(p2dpT * perfFactor);
      var prevR = Math.round(prevT * (perfFactor + 0.03));
      var korR = Math.round(korT * (perfFactor - 0.02));

      rows.push([
        globalNo++,
        u.prefix + "-" + ("000" + (idx + 1)).slice(-3),
        name,
        u.id,
        u.nama,
        "RBM " + u.nama.replace("ULP ", "") + " " + ((idx % 6) + 1),
        pelgn,
        targetAll,
        p2dpT,
        p2dpR,
        prevT,
        prevR,
        korT,
        korR
      ]);
    });
  });

  if (rows.length > 0) {
    sheet.getRange(2, 1, rows.length, headers.length).setValues(rows);
  }
  sheet.setFrozenRows(1);
  sheet.autoResizeColumns(1, headers.length);
}

function setupKontakPicSheet(ss) {
  var sheet = getOrCreateSheet(ss, "Kontak_PIC");
  sheet.clear();

  var headers = ["NO", "ID_ULP", "NAMA_ULP", "NAMA_PIC", "JABATAN", "NO_WHATSAPP", "LINK_WHATSAPP"];
  sheet.appendRow(headers);

  var headerRange = sheet.getRange(1, 1, 1, headers.length);
  headerRange.setBackground("#0F766E")
             .setFontColor("#FFFFFF")
             .setFontWeight("bold")
             .setHorizontalAlignment("center");

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
  sheet.setFrozenRows(1);
  sheet.autoResizeColumns(1, headers.length);
}

function setupMonitoringHarianSheet(ss) {
  var sheet = getOrCreateSheet(ss, "Monitoring_Harian");
  sheet.clear();

  var headers = [
    "NO", "KODE_PETUGAS", "NAMA_PETUGAS", "ULP", "JML_PELANGGAN",
    "TARGET_ALL", "TARGET_P2DP", "REAL_P2DP", "PERSEN_P2DP",
    "TARGET_PREV", "REAL_PREV", "PERSEN_PREV",
    "TARGET_KOR", "REAL_KOR", "PERSEN_KOR",
    "JUMLAH_REALISASI", "PERSEN_TOTAL"
  ];
  sheet.appendRow(headers);
  sheet.getRange(1, 1, 1, headers.length).setBackground("#F59E0B").setFontColor("#000000").setFontWeight("bold");
}

function setupRiwayatSnapshotSheet(ss) {
  var sheet = getOrCreateSheet(ss, "Riwayat_Snapshot");
  sheet.clear();
  var headers = ["SNAPSHOT_ID", "TANGGAL", "JAM_WIB", "PERIODE", "NAMA_FILE", "ULP", "JML_PETUGAS", "TOTAL_REAL", "PERSEN_TOTAL", "DELTA", "OPERATOR"];
  sheet.appendRow(headers);
  sheet.getRange(1, 1, 1, headers.length).setBackground("#3B82F6").setFontColor("#FFFFFF").setFontWeight("bold");
}

function setupConfigKpiSheet(ss) {
  var sheet = getOrCreateSheet(ss, "Config_KPI");
  sheet.clear();
  var configData = [
    ["PARAMETER", "NILAI", "KETERANGAN"],
    ["BULAN_LAPORAN", "OKTOBER", "Nama bulan aktif"],
    ["TAHUN_LAPORAN", "2026", "Tahun kalender"],
    ["WAKTU_UPDATE_TERAKHIR", "22/10/2026 10.55", "Timestamp update terakhir WIB"],
    ["FORMULA_MODE", "proporsional", "Pilihan: proporsional atau bobot"],
    ["BOBOT_P2DP", "40", "Persen bobot komponen P2DP (jika mode bobot)"],
    ["BOBOT_PREVENTIF", "35", "Persen bobot Preventif ITO"],
    ["BOBOT_KOREKTIF", "25", "Persen bobot Korektif ITO"],
    ["THRESHOLD_HIJAU_TUA", "100", "Batas Tercapai (>= 100%)"],
    ["THRESHOLD_HIJAU_MUDA", "80", "Batas Baik (80% - 99%)"],
    ["THRESHOLD_KUNING", "60", "Batas Sedang (60% - 79%)"],
    ["THRESHOLD_ORANYE", "40", "Batas Rendah (40% - 59%)"]
  ];
  sheet.getRange(1, 1, configData.length, 3).setValues(configData);
  sheet.getRange(1, 1, 1, 3).setBackground("#1E1B4B").setFontColor("#FFFFFF").setFontWeight("bold");
}

function getOrCreateSheet(ss, sheetName) {
  var sheet = ss.getSheetByName(sheetName);
  if (!sheet) {
    sheet = ss.insertSheet(sheetName);
  }
  return sheet;
}
