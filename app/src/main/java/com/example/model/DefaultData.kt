package com.example.model

object DefaultData {
    const val SUPER_ADMIN_CODE = "SUPER-PLN-2026"
    const val EDIT_OFFICER_CODE = "EDIT-BILLMAN-2026"

    val KOORDINATOR_UP3 = UlpItem(
        id = "UP3_TEGAL",
        nama = "KANTOR UP3 TEGAL (INDUK)",
        kodePrefix = "UP3-TGL",
        jmlPetugas = 460,
        koordinator = "Manager Bagian Transaksi Energi & P2DP UP3 Tegal",
        picName = "Bpk. Ir. Joko Susanto, M.M.",
        picPhone = "628122981800",
        picJabatan = "Manager Bagian Transaksi Energi & Koordinator P2DP UP3 Tegal"
    )

    val DAFTAR_ULP = listOf(
        UlpItem(
            id = "TEGAL_TIMUR",
            nama = "ULP TEGAL TIMUR",
            kodePrefix = "BM-TGT",
            jmlPetugas = 46,
            koordinator = "Supervisor Billman Tegal Timur",
            picName = "Bpk. Rahmat Hidayat, S.T.",
            picPhone = "628122981801",
            picJabatan = "Supervisor Pelayanan Pelanggan & Billman Tegal Timur"
        ),
        UlpItem(
            id = "TEGAL_KOTA",
            nama = "ULP TEGAL KOTA",
            kodePrefix = "BM-TGK",
            jmlPetugas = 46,
            koordinator = "Supervisor Billman Tegal Kota",
            picName = "Ibu Sri Wahyuni, S.T.",
            picPhone = "628122981802",
            picJabatan = "Supervisor Pelayanan Pelanggan & Billman Tegal Kota"
        ),
        UlpItem(
            id = "SLAWI",
            nama = "ULP SLAWI",
            kodePrefix = "BM-SLW",
            jmlPetugas = 46,
            koordinator = "Supervisor Billman Slawi",
            picName = "Bpk. Agus Prasetyo, M.T.",
            picPhone = "628122981803",
            picJabatan = "Supervisor Pelayanan Pelanggan & Billman Slawi"
        ),
        UlpItem(
            id = "RANDUDONGKAL",
            nama = "ULP RANDUDONGKAL",
            kodePrefix = "BM-RDD",
            jmlPetugas = 46,
            koordinator = "Supervisor Billman Randudongkal",
            picName = "Bpk. Hendro Siswanto",
            picPhone = "628122981804",
            picJabatan = "Supervisor Pelayanan Pelanggan & Billman Randudongkal"
        ),
        UlpItem(
            id = "JATIBARANG",
            nama = "ULP JATIBARANG",
            kodePrefix = "BM-JTB",
            jmlPetugas = 46,
            koordinator = "Supervisor Billman Jatibarang",
            picName = "Ibu Dewi Lestari",
            picPhone = "628122981805",
            picJabatan = "Supervisor Pelayanan Pelanggan & Billman Jatibarang"
        ),
        UlpItem(
            id = "COMAL",
            nama = "ULP COMAL",
            kodePrefix = "BM-CML",
            jmlPetugas = 46,
            koordinator = "Supervisor Billman Comal",
            picName = "Bpk. Fajar Ramadhan",
            picPhone = "628122981806",
            picJabatan = "Supervisor Pelayanan Pelanggan & Billman Comal"
        ),
        UlpItem(
            id = "BREBES",
            nama = "ULP BREBES",
            kodePrefix = "BM-BRB",
            jmlPetugas = 46,
            koordinator = "Supervisor Billman Brebes",
            picName = "Bpk. Tri Wibowo, S.T.",
            picPhone = "628122981807",
            picJabatan = "Supervisor Pelayanan Pelanggan & Billman Brebes"
        ),
        UlpItem(
            id = "PEMALANG",
            nama = "ULP PEMALANG",
            kodePrefix = "BM-PML",
            jmlPetugas = 46,
            koordinator = "Supervisor Billman Pemalang",
            picName = "Bpk. Danang Kusuma",
            picPhone = "628122981808",
            picJabatan = "Supervisor Pelayanan Pelanggan & Billman Pemalang"
        ),
        UlpItem(
            id = "BUMIAYU",
            nama = "ULP BUMIAYU",
            kodePrefix = "BM-BMY",
            jmlPetugas = 46,
            koordinator = "Supervisor Billman Bumiayu",
            picName = "Bpk. Arif Firmansyah",
            picPhone = "628122981809",
            picJabatan = "Supervisor Pelayanan Pelanggan & Billman Bumiayu"
        ),
        UlpItem(
            id = "BALAPULANG",
            nama = "ULP BALAPULANG",
            kodePrefix = "BM-BLP",
            jmlPetugas = 46,
            koordinator = "Supervisor Billman Balapulang",
            picName = "Bpk. Sigit Purnomo",
            picPhone = "628122981810",
            picJabatan = "Supervisor Pelayanan Pelanggan & Billman Balapulang"
        )
    )

    val USERS = listOf(
        UserCredential("superadmin", "super2026", "Super Admin UP3 Tegal (Akses 10 ULP)", "Koordinator P2DP UP3 Tegal", "TEGAL_TIMUR", true),
        UserCredential("admin", "pln123", "Admin Billman ULP Tegal Timur", "Supervisor ULP Tegal Timur", "TEGAL_TIMUR", false),
        UserCredential("tegaltimur", "pln123", "Admin Billman ULP Tegal Timur", "Admin ULP Tegal Timur", "TEGAL_TIMUR", false),
        UserCredential("tegalkota", "pln123", "Admin Billman ULP Tegal Kota", "Admin ULP Tegal Kota", "TEGAL_KOTA", false),
        UserCredential("slawi", "pln123", "Admin Billman ULP Slawi", "Admin ULP Slawi", "SLAWI", false),
        UserCredential("randudongkal", "pln123", "Admin Billman ULP Randudongkal", "Admin ULP Randudongkal", "RANDUDONGKAL", false),
        UserCredential("jatibarang", "pln123", "Admin Billman ULP Jatibarang", "Admin ULP Jatibarang", "JATIBARANG", false),
        UserCredential("comal", "pln123", "Admin Billman ULP Comal", "Admin ULP Comal", "COMAL", false),
        UserCredential("brebes", "pln123", "Admin Billman ULP Brebes", "Admin ULP Brebes", "BREBES", false),
        UserCredential("pemalang", "pln123", "Admin Billman ULP Pemalang", "Admin ULP Pemalang", "PEMALANG", false),
        UserCredential("bumiayu", "pln123", "Admin Billman ULP Bumiayu", "Admin ULP Bumiayu", "BUMIAYU", false),
        UserCredential("balapulang", "pln123", "Admin Billman ULP Balapulang", "Admin ULP Balapulang", "BALAPULANG", false)
    )

    val OFFICER_NAMES = listOf(
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
    )

    fun generateOfficers(ulpId: String): List<Officer> {
        val ulpMeta = DAFTAR_ULP.find { it.id == ulpId } ?: DAFTAR_ULP.first()
        val ulpIdx = DAFTAR_ULP.indexOfFirst { it.id == ulpId }.coerceAtLeast(0)
        val seedOffset = ulpIdx * 7

        return OFFICER_NAMES.mapIndexed { idx, name ->
            val seed = ((idx + seedOffset) * 17 + 13) % 100
            val pelgn = 1150 + (((idx + seedOffset) * 29) % 450)
            val p2dpTarget = 25 + ((idx + seedOffset) % 3) * 5
            val prevTarget = 20 + ((idx + seedOffset) % 2) * 5
            val korTarget = 10 + ((idx + seedOffset) % 2) * 5
            val targetAll = p2dpTarget + prevTarget + korTarget

            var perfFactor = 0.85
            when {
                (idx + seedOffset) % 9 == 0 -> perfFactor = 1.05
                idx == 41 || idx == 44 -> perfFactor = 0.0
                (idx + seedOffset) % 7 == 0 -> perfFactor = 0.42
                (idx + seedOffset) % 5 == 0 -> perfFactor = 0.68
                else -> perfFactor = 0.78 + (seed / 450.0)
            }

            val p2dpReal = if (perfFactor == 0.0) 0 else ((p2dpTarget * perfFactor).toInt()).coerceIn(0, p2dpTarget + 4)
            val prevReal = if (perfFactor == 0.0) 0 else ((prevTarget * (perfFactor + 0.04)).toInt()).coerceIn(0, prevTarget + 3)
            val korReal = if (perfFactor == 0.0) 0 else ((korTarget * (perfFactor - 0.03)).toInt()).coerceIn(0, korTarget + 2)

            val p2dpPagi = (p2dpReal * 0.55).toInt().coerceAtLeast(0)
            val prevPagi = (prevReal * 0.55).toInt().coerceAtLeast(0)
            val korPagi = (korReal * 0.55).toInt().coerceAtLeast(0)

            val p2dpSiang = (p2dpReal * 0.82).toInt().coerceAtLeast(p2dpPagi)
            val prevSiang = (prevReal * 0.82).toInt().coerceAtLeast(prevPagi)
            val korSiang = (korReal * 0.82).toInt().coerceAtLeast(korPagi)

            Officer(
                id = idx + 1,
                no = idx + 1,
                kodePetugas = "${ulpMeta.kodePrefix}-${(idx + 1).toString().padStart(3, '0')}",
                petugas = name,
                wilayah = "RBM ${ulpMeta.nama.replace("ULP ", "")} ${(idx % 6) + 1}",
                jumlahPelgn = pelgn,
                targetAll = targetAll,
                p2dpTarget = p2dpTarget,
                p2dpReal = p2dpReal,
                prevTarget = prevTarget,
                prevReal = prevReal,
                korTarget = korTarget,
                korReal = korReal,
                pagi = PeriodMetric(p2dpPagi, prevPagi, korPagi),
                siang = PeriodMetric(p2dpSiang, prevSiang, korSiang),
                sore = PeriodMetric(p2dpReal, prevReal, korReal)
            )
        }
    }

    val INITIAL_HISTORY = listOf(
        HistorySnapshot(
            id = "SNAP-20261022-0830",
            tanggal = "22/10/2026",
            jam = "08.30",
            periode = "Pagi",
            namaFile = "FSM_ACMT_TEGAL_TIMUR_22102026_PAGI.xlsx",
            jumlahPetugas = 46,
            totalRealisasi = 1184,
            persenTotal = 45.28,
            deltaRealisasi = "+142",
            user = "Admin Billman Tegal Timur"
        ),
        HistorySnapshot(
            id = "SNAP-20261022-1055",
            tanggal = "22/10/2026",
            jam = "10.55",
            periode = "Pagi",
            namaFile = "FSM_ACMT_TEGAL_TIMUR_22102026_UPDATE.xlsx",
            jumlahPetugas = 46,
            totalRealisasi = 2145,
            persenTotal = 82.03,
            deltaRealisasi = "+961",
            user = "Admin Billman Tegal Timur"
        )
    )

    val INITIAL_FEATURES = listOf(
        CustomFeature(
            id = "FEAT-001",
            namaFitur = "Monitoring Pemutusan Rampung",
            kategori = "Monitoring & Tabel",
            targetMenu = "Tabel Referensi Utama",
            fieldBaru = "target_rampung, real_rampung",
            rumusLogika = "persen_rampung = (real_rampung / target_rampung) * 100",
            showInMainTable = true,
            includeInTotalUlp = false,
            defaultTarget = 15
        ),
        CustomFeature(
            id = "FEAT-002",
            namaFitur = "Validasi Foto Meter Geotagging",
            kategori = "Monitoring Lapangan",
            targetMenu = "4. Monitoring P2DP",
            fieldBaru = "foto_geotag_url, koordinat_gps",
            rumusLogika = "IF(foto_geotag_url != NULL && akurasi_gps <= 15m, 'VALID', 'PERLU VERIFIKASI')",
            showInMainTable = false,
            includeInTotalUlp = false,
            defaultTarget = 25
        ),
        CustomFeature(
            id = "FEAT-003",
            namaFitur = "Alert Otomatis Petugas Realisasi 0 Pukul 14.00",
            kategori = "Notifikasi & Webhook",
            targetMenu = "12. Integrasi n8n",
            fieldBaru = "status_alert_siang",
            rumusLogika = "FILTER(petugas WHERE jumlahRealisasi == 0 AND jam >= 14.00) -> POST Webhook n8n",
            showInMainTable = false,
            includeInTotalUlp = false,
            defaultTarget = 10
        )
    )
}
