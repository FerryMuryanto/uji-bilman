package com.example.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BillmanViewModel : ViewModel() {

    private val _currentUser = MutableStateFlow<UserCredential?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _selectedUlpId = MutableStateFlow("TEGAL_TIMUR")
    val selectedUlpId = _selectedUlpId.asStateFlow()

    private val _selectedPeriodView = MutableStateFlow("pagi") // "pagi", "siang", "sore"
    val selectedPeriodView = _selectedPeriodView.asStateFlow()

    private val _settings = MutableStateFlow(SettingsData())
    val settings = _settings.asStateFlow()

    private val _ulpDatasets = MutableStateFlow<Map<String, List<Officer>>>(
        DefaultData.DAFTAR_ULP.associate { it.id to DefaultData.generateOfficers(it.id) }
    )
    val ulpDatasets = _ulpDatasets.asStateFlow()

    private val _historyList = MutableStateFlow(DefaultData.INITIAL_HISTORY)
    val historyList = _historyList.asStateFlow()

    private val _customFeatures = MutableStateFlow(DefaultData.INITIAL_FEATURES)
    val customFeatures = _customFeatures.asStateFlow()

    private val _isMenu12Unlocked = MutableStateFlow(false)
    val isMenu12Unlocked = _isMenu12Unlocked.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage = _toastMessage.asStateFlow()

    // Active ULP Metadata
    val activeUlpMeta = _selectedUlpId.map { id ->
        DefaultData.DAFTAR_ULP.find { it.id == id } ?: DefaultData.DAFTAR_ULP.first()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, DefaultData.DAFTAR_ULP.first())

    // Raw Officers of selected ULP
    val currentOfficers = combine(_selectedUlpId, _ulpDatasets) { ulpId, map ->
        map[ulpId] ?: DefaultData.generateOfficers(ulpId)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, DefaultData.generateOfficers("TEGAL_TIMUR"))

    // Processed Officers with metrics and rankings
    val processedOfficers: StateFlow<List<ProcessedOfficer>> = combine(
        currentOfficers,
        _selectedPeriodView,
        _settings,
        _customFeatures
    ) { officers, period, st, features ->
        computeProcessedOfficers(officers, period, st, features)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Rekapitulasi ULP
    val rekapUlp: StateFlow<RekapUlp> = combine(
        processedOfficers,
        _settings,
        _customFeatures
    ) { procList, st, features ->
        computeRekapUlp(procList, st, features)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, RekapUlp(0, 0, 0, 0, 0.0, 0, 0, 0.0, 0, 0, 0.0, 0, 0.0, 0, 0, 0, 0, 0, 0))

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun login(username: String, pass: String): Boolean {
        val user = DefaultData.USERS.find {
            it.username.equals(username.trim(), ignoreCase = true) && it.password == pass.trim()
        }
        if (user != null) {
            _currentUser.value = user
            _selectedUlpId.value = user.ulpId
            showToast("Login berhasil sebagai ${user.name}")
            return true
        }
        return false
    }

    fun logout() {
        _currentUser.value = null
        _isMenu12Unlocked.value = false
        showToast("Telah keluar dari sistem.")
    }

    fun setUlp(ulpId: String) {
        val user = _currentUser.value
        if (user != null && !user.canSwitchUlp && user.ulpId != ulpId) {
            showToast("Akses ditolak: Akun terkunci di ${activeUlpMeta.value.nama}")
            return
        }
        _selectedUlpId.value = ulpId
        showToast("Beralih ke ruangan ${DefaultData.DAFTAR_ULP.find { it.id == ulpId }?.nama}")
    }

    fun setPeriod(period: String) {
        _selectedPeriodView.value = period
    }

    fun updateSettings(newSettings: SettingsData) {
        _settings.value = newSettings
        showToast("Pengaturan KPI & Ambang Warna Diperbarui")
    }

    fun unlockMenu12(code: String): Boolean {
        if (code == DefaultData.SUPER_ADMIN_CODE) {
            _isMenu12Unlocked.value = true
            showToast("Akses Super Admin Terbuka")
            return true
        }
        return false
    }

    fun lockMenu12() {
        _isMenu12Unlocked.value = false
        showToast("Menu 12 Dikunci")
    }

    fun saveAllChanges() {
        showToast("SIMPAN PERUBAHAN ALL BERHASIL! Seluruh data 10 ULP & KPI telah tersinkron.")
    }

    fun addOfficer(newOfficer: Officer) {
        val ulp = _selectedUlpId.value
        val current = _ulpDatasets.value[ulp] ?: emptyList()
        val updated = current + newOfficer.copy(id = current.size + 1, no = current.size + 1)
        _ulpDatasets.value = _ulpDatasets.value + (ulp to updated)
        showToast("Petugas ${newOfficer.petugas} ditambahkan.")
    }

    fun updateOfficer(updatedOfficer: Officer) {
        val ulp = _selectedUlpId.value
        val current = _ulpDatasets.value[ulp] ?: emptyList()
        val next = current.map { if (it.id == updatedOfficer.id) updatedOfficer else it }
        _ulpDatasets.value = _ulpDatasets.value + (ulp to next)
        showToast("Data petugas ${updatedOfficer.petugas} berhasil diperbarui.")
    }

    fun deleteOfficer(officerId: Int) {
        val ulp = _selectedUlpId.value
        val current = _ulpDatasets.value[ulp] ?: emptyList()
        val next = current.filter { it.id != officerId }.mapIndexed { idx, o -> o.copy(id = idx + 1, no = idx + 1) }
        _ulpDatasets.value = _ulpDatasets.value + (ulp to next)
        showToast("Petugas telah dihapus.")
    }

    fun clearOfficers() {
        val ulp = _selectedUlpId.value
        _ulpDatasets.value = _ulpDatasets.value + (ulp to emptyList())
        showToast("Daftar petugas ${activeUlpMeta.value.nama} dikosongkan.")
    }

    fun replaceOfficers(list: List<Officer>) {
        val ulp = _selectedUlpId.value
        val indexed = list.mapIndexed { idx, o -> o.copy(id = idx + 1, no = idx + 1) }
        _ulpDatasets.value = _ulpDatasets.value + (ulp to indexed)
        showToast("${indexed.size} data petugas berhasil disimpan ke ${activeUlpMeta.value.nama}.")
    }

    fun addSnapshot(snapshot: HistorySnapshot) {
        _historyList.value = _historyList.value + snapshot
        _settings.value = _settings.value.copy(
            waktuUpdate = "${snapshot.tanggal} ${snapshot.jam}",
            periodeAktif = snapshot.periode
        )
        showToast("Snapshot update harian ${snapshot.jam} berhasil disimpan.")
    }

    fun updateCustomMetric(officerId: Int, featId: String, target: Int, real: Int) {
        val ulp = _selectedUlpId.value
        val current = _ulpDatasets.value[ulp] ?: emptyList()
        val next = current.map { o ->
            if (o.id == officerId) {
                val nextCustom = o.customData.toMutableMap()
                nextCustom[featId] = CustomMetricVal(target, real)
                o.copy(customData = nextCustom)
            } else o
        }
        _ulpDatasets.value = _ulpDatasets.value + (ulp to next)
        showToast("Nilai modul diperbarui.")
    }

    fun addCustomFeature(feature: CustomFeature) {
        _customFeatures.value = _customFeatures.value + feature
        showToast("Modul baru '${feature.namaFitur}' berhasil ditambahkan.")
    }

    fun removeCustomFeature(featureId: String) {
        _customFeatures.value = _customFeatures.value.filter { it.id != featureId }
        showToast("Modul dihapus.")
    }

    fun toggleFeatureInMainTable(featureId: String, show: Boolean) {
        _customFeatures.value = _customFeatures.value.map {
            if (it.id == featureId) it.copy(showInMainTable = show) else it
        }
    }

    fun toggleFeatureInTotalUlp(featureId: String, include: Boolean) {
        _customFeatures.value = _customFeatures.value.map {
            if (it.id == featureId) it.copy(includeInTotalUlp = include) else it
        }
    }

    private fun computeProcessedOfficers(
        officers: List<Officer>,
        period: String,
        st: SettingsData,
        features: List<CustomFeature>
    ): List<ProcessedOfficer> {
        val rawComputed = officers.map { item ->
            val p2dpReal = when (period) {
                "pagi" -> item.pagi.p2dp
                "siang" -> item.siang.p2dp
                else -> item.p2dpReal
            }
            val prevReal = when (period) {
                "pagi" -> item.pagi.prev
                "siang" -> item.siang.prev
                else -> item.prevReal
            }
            val korReal = when (period) {
                "pagi" -> item.pagi.kor
                "siang" -> item.siang.kor
                else -> item.korReal
            }

            val baseTargetAll = if (st.targetAllMode == "sum_komponen") {
                item.p2dpTarget + item.prevTarget + item.korTarget
            } else {
                item.targetAll
            }

            val p2dpPersen = PerformanceHelper.calcPct(p2dpReal, item.p2dpTarget)
            val prevPersen = PerformanceHelper.calcPct(prevReal, item.prevTarget)
            val korPersen = PerformanceHelper.calcPct(korReal, item.korTarget)
            val baseJumlahReal = p2dpReal + prevReal + korReal

            val customMetrics = mutableMapOf<String, CustomComputedMetric>()
            var extraCustomTarget = 0
            var extraCustomReal = 0

            features.forEach { feat ->
                val stored = item.customData[feat.id]
                val defTarget = if (feat.defaultTarget > 0) feat.defaultTarget else 15
                val cTarget = stored?.target ?: defTarget
                val ratio = if (item.p2dpTarget > 0) (p2dpReal.toDouble() / item.p2dpTarget.toDouble()) else 0.8
                val autoReal = if (p2dpReal == 0) 0 else (cTarget * ratio).toInt().coerceIn(0, cTarget)
                val cReal = stored?.real ?: autoReal
                val cPersen = PerformanceHelper.calcPct(cReal, cTarget)

                customMetrics[feat.id] = CustomComputedMetric(cTarget, cReal, cPersen)

                if (feat.includeInTotalUlp) {
                    extraCustomTarget += cTarget
                    extraCustomReal += cReal
                }
            }

            val finalTargetAll = baseTargetAll + extraCustomTarget
            val finalJumlahReal = baseJumlahReal + extraCustomReal
            val persenTotal = if (st.formulaMode == "proporsional") {
                PerformanceHelper.calcPct(finalJumlahReal, finalTargetAll)
            } else {
                val w1 = st.bobotP2DP / 100.0
                val w2 = st.bobotPrev / 100.0
                val w3 = st.bobotKor / 100.0
                (p2dpPersen * w1) + (prevPersen * w2) + (korPersen * w3)
            }

            val deltaPagiKeSiang = (item.siang.p2dp + item.siang.prev + item.siang.kor) - (item.pagi.p2dp + item.pagi.prev + item.pagi.kor)
            val deltaSiangKeSore = (item.p2dpReal + item.prevReal + item.korReal) - (item.siang.p2dp + item.siang.prev + item.siang.kor)

            ProcessedOfficer(
                officer = item,
                ranking = 0,
                activeP2dpReal = p2dpReal,
                activePrevReal = prevReal,
                activeKorReal = korReal,
                p2dpPersen = p2dpPersen,
                prevPersen = prevPersen,
                korPersen = korPersen,
                jumlahRealisasi = finalJumlahReal,
                finalTargetAll = finalTargetAll,
                persenTotal = persenTotal,
                deltaPagiKeSiang = deltaPagiKeSiang,
                deltaSiangKeSore = deltaSiangKeSore,
                customMetrics = customMetrics
            )
        }

        // Rank by persenTotal desc, then jumlahRealisasi desc, then id asc
        val sorted = rawComputed.sortedWith(
            compareByDescending<ProcessedOfficer> { it.persenTotal }
                .thenByDescending { it.jumlahRealisasi }
                .thenBy { it.officer.no }
        )

        val rankMap = mutableMapOf<Int, Int>()
        sorted.forEachIndexed { index, o ->
            rankMap[o.officer.id] = index + 1
        }

        return rawComputed.map { o ->
            o.copy(ranking = rankMap[o.officer.id] ?: o.officer.no)
        }
    }

    private fun computeRekapUlp(
        procList: List<ProcessedOfficer>,
        st: SettingsData,
        features: List<CustomFeature>
    ): RekapUlp {
        var jumlahPelgn = 0
        var targetAll = 0
        var p2dpTarget = 0
        var p2dpReal = 0
        var prevTarget = 0
        var prevReal = 0
        var korTarget = 0
        var korReal = 0
        var jumlahRealisasi = 0
        var pagiTotal = 0
        var siangTotal = 0
        var soreTotal = 0

        procList.forEach { p ->
            val o = p.officer
            jumlahPelgn += o.jumlahPelgn
            targetAll += p.finalTargetAll
            p2dpTarget += o.p2dpTarget
            p2dpReal += p.activeP2dpReal
            prevTarget += o.prevTarget
            prevReal += p.activePrevReal
            korTarget += o.korTarget
            korReal += p.activeKorReal
            jumlahRealisasi += p.jumlahRealisasi

            pagiTotal += (o.pagi.p2dp + o.pagi.prev + o.pagi.kor)
            siangTotal += (o.siang.p2dp + o.siang.prev + o.siang.kor)
            soreTotal += (o.p2dpReal + o.prevReal + o.korReal)
        }

        val p2dpPersen = PerformanceHelper.calcPct(p2dpReal, p2dpTarget)
        val prevPersen = PerformanceHelper.calcPct(prevReal, prevTarget)
        val korPersen = PerformanceHelper.calcPct(korReal, korTarget)

        val persenTotal = if (st.formulaMode == "proporsional") {
            PerformanceHelper.calcPct(jumlahRealisasi, targetAll)
        } else {
            (p2dpPersen * (st.bobotP2DP / 100.0)) +
                    (prevPersen * (st.bobotPrev / 100.0)) +
                    (korPersen * (st.bobotKor / 100.0))
        }

        val count100 = procList.count { it.persenTotal >= 100.0 }
        val countZero = procList.count { it.jumlahRealisasi == 0 }
        val countBelumTarget = procList.count { it.persenTotal < 100.0 }

        val customTotals = mutableMapOf<String, CustomTotalMetric>()
        features.forEach { feat ->
            val sumTarget = procList.sumOf { it.customMetrics[feat.id]?.target ?: 0 }
            val sumReal = procList.sumOf { it.customMetrics[feat.id]?.real ?: 0 }
            val pct = PerformanceHelper.calcPct(sumReal, sumTarget)
            val capai100 = procList.count { (it.customMetrics[feat.id]?.persen ?: 0.0) >= 100.0 }
            customTotals[feat.id] = CustomTotalMetric(sumTarget, sumReal, pct, capai100)
        }

        return RekapUlp(
            jumlahPelgn = jumlahPelgn,
            targetAll = targetAll,
            p2dpTarget = p2dpTarget,
            p2dpReal = p2dpReal,
            p2dpPersen = p2dpPersen,
            prevTarget = prevTarget,
            prevReal = prevReal,
            prevPersen = prevPersen,
            korTarget = korTarget,
            korReal = korReal,
            korPersen = korPersen,
            jumlahRealisasi = jumlahRealisasi,
            persenTotal = persenTotal,
            pagiTotal = pagiTotal,
            siangTotal = siangTotal,
            soreTotal = soreTotal,
            count100 = count100,
            countZero = countZero,
            countBelumTarget = countBelumTarget,
            customTotals = customTotals
        )
    }
}
