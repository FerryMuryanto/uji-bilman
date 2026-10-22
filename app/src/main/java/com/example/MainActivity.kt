package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.BillmanViewModel
import com.example.model.DefaultData
import com.example.ui.components.SecurityAuthDialog
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.util.configureAppCheck
import kotlinx.coroutines.launch

data class MenuItemDef(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val badge: String? = null
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Step 1: Configure App Check before UI or Firebase operations
        configureAppCheck(this, intent)

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                BillmanApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillmanApp(vm: BillmanViewModel = viewModel()) {
    val context = LocalContext.current
    val currentUser by vm.currentUser.collectAsStateWithLifecycle()
    val activeUlp by vm.activeUlpMeta.collectAsStateWithLifecycle()
    val selectedPeriod by vm.selectedPeriodView.collectAsStateWithLifecycle()
    val settings by vm.settings.collectAsStateWithLifecycle()
    val processedOfficers by vm.processedOfficers.collectAsStateWithLifecycle()
    val rekapUlp by vm.rekapUlp.collectAsStateWithLifecycle()
    val historyList by vm.historyList.collectAsStateWithLifecycle()
    val customFeatures by vm.customFeatures.collectAsStateWithLifecycle()
    val currentOfficers by vm.currentOfficers.collectAsStateWithLifecycle()
    val isMenu12Unlocked by vm.isMenu12Unlocked.collectAsStateWithLifecycle()
    val toastMessage by vm.toastMessage.collectAsStateWithLifecycle()

    var activeMenu by remember { mutableStateOf("tabel_utama") }
    var showMenu12AuthDialog by remember { mutableStateOf(false) }
    var showUlpDropdown by remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Handle back button: if not in main table, navigate back to main table
    BackHandler(enabled = currentUser != null && activeMenu != "tabel_utama") {
        activeMenu = "tabel_utama"
    }

    // Auto-dismiss toast
    LaunchedEffect(toastMessage) {
        if (toastMessage != null) {
            kotlinx.coroutines.delay(3500)
            vm.clearToast()
        }
    }

    if (currentUser == null) {
        LoginScreen(
            onLogin = { u, p -> vm.login(u, p) }
        )
        return
    }

    val baseMenus = remember(isMenu12Unlocked) {
        listOf(
            MenuItemDef("tabel_utama", "Tabel Referensi Utama", Icons.Default.TableChart, "Acuan"),
            MenuItemDef("dashboard", "1. Dashboard Monitoring", Icons.Default.Dashboard),
            MenuItemDef("kontak_pic", "Kontak WhatsApp PIC ULP", Icons.Default.Chat, "WA PIC"),
            MenuItemDef("ai_assistant", "Asisten AI Billman (Gemini)", Icons.Default.SmartToy, "AI"),
            MenuItemDef("gas_scripts", "Kode Apps Script & Web", Icons.Default.Code, "GAS"),
            MenuItemDef("upload", "2. Upload Data Harian", Icons.Default.CloudUpload),
            MenuItemDef("master_petugas", "3. Data Master Petugas", Icons.Default.People, "Sandi"),
            MenuItemDef("mon_p2dp", "4. Monitoring P2DP", Icons.Default.Shield),
            MenuItemDef("mon_preventif", "5. Preventif New ITO", Icons.Default.Build),
            MenuItemDef("mon_korektif", "6. Korektif New ITO", Icons.Default.CheckCircle),
            MenuItemDef("ranking", "7. Ranking Kinerja", Icons.Default.EmojiEvents),
            MenuItemDef("rekap_ulp", "8. Rekapitulasi Total ULP", Icons.Default.Calculate),
            MenuItemDef("riwayat", "9. Riwayat Update Harian", Icons.Default.History),
            MenuItemDef("ekspor", "10. Ekspor & Cover Slide", Icons.Default.Download, "16:9"),
            MenuItemDef("pengaturan", "11. Pengaturan Target & KPI", Icons.Default.Settings),
            MenuItemDef("n8n_source", "12. Integrasi n8n & Kode Lokal", if (isMenu12Unlocked) Icons.Default.LockOpen else Icons.Default.Lock, "Super Admin"),
            MenuItemDef("db_dan_fitur", "13. Cek Database & Tambah Fitur", Icons.Default.Storage, "Dev & DB")
        )
    }

    fun openActiveUlpWhatsApp() {
        val phone = activeUlp.picPhone
        val message = "Halo ${activeUlp.picName}, saya dari Admin Billman UP3 Tegal terkait koordinasi target dan realisasi P2DP ${activeUlp.nama}."
        val uri = Uri.parse("https://api.whatsapp.com/send?phone=$phone&text=${Uri.encode(message)}")
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (e: Exception) {
            Toast.makeText(context, "Tidak dapat membuka WhatsApp.", Toast.LENGTH_SHORT).show()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = PlnNavyDark,
                modifier = Modifier.width(300.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 16.dp)
                ) {
                    // Drawer Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(PlnNavyPrimary)
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(PlnGold),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("PLN", fontWeight = FontWeight.Black, fontSize = 14.sp, color = PlnNavyDark)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("BILLMAN P2DP", color = PlnGold, fontWeight = FontWeight.Black, fontSize = 13.sp)
                                    Text("PT PLN Electricity Services", color = Slate300, fontSize = 9.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(activeUlp.nama, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(
                                text = "${currentUser?.name} (${currentUser?.role})",
                                color = Color(0xFF93C5FD),
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Navigation Menu List
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        baseMenus.forEach { item ->
                            val isSelected = activeMenu == item.id
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) Color(0xFF4F46E5) else Color.Transparent,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (item.id == "n8n_source" && !isMenu12Unlocked) {
                                            showMenu12AuthDialog = true
                                        } else {
                                            activeMenu = item.id
                                            scope.launch { drawerState.close() }
                                        }
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            item.icon,
                                            contentDescription = null,
                                            tint = if (isSelected) PlnGold else Slate300,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = item.label,
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                    if (item.badge != null) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(
                                                    if (item.badge == "WA PIC") Color(0xFF059669)
                                                    else if (item.badge == "AI") Color(0xFF7C3AED)
                                                    else if (isSelected) PlnGold else Color(0xFF334155)
                                                )
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                item.badge,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Dynamic Custom Modules (Menu 14+)
                        if (customFeatures.isNotEmpty()) {
                            Divider(color = Color(0xFF334155), modifier = Modifier.padding(vertical = 6.dp))
                            Text(
                                "MODUL BARU (MENU 14+):",
                                color = PlnGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                            customFeatures.forEachIndexed { idx, feat ->
                                val menuKey = "custom_${feat.id}"
                                val isSelected = activeMenu == menuKey
                                val num = 14 + idx
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) Color(0xFF0D9488) else Color.Transparent,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            activeMenu = menuKey
                                            scope.launch { drawerState.close() }
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Layers, contentDescription = null, tint = Color(0xFF5EEAD4), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("$num. ${feat.namaFitur}", color = Color.White, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }

                    // Logout in Drawer Bottom
                    Divider(color = Color(0xFF334155))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF991B1B).copy(alpha = 0.2f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .clickable { vm.logout() }
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Logout, contentDescription = null, tint = PlnRose, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Keluar Aplikasi", color = Color(0xFFFECDD3), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = activeUlp.nama,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                                if (currentUser?.canSwitchUlp == true) {
                                    IconButton(onClick = { showUlpDropdown = !showUlpDropdown }) {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Pilih ULP", tint = PlnGold)
                                    }
                                    DropdownMenu(
                                        expanded = showUlpDropdown,
                                        onDismissRequest = { showUlpDropdown = false }
                                    ) {
                                        DefaultData.DAFTAR_ULP.forEach { u ->
                                            DropdownMenuItem(
                                                text = { Text("${u.nama} (${u.kodePrefix})", fontWeight = if (u.id == activeUlp.id) FontWeight.Bold else FontWeight.Normal) },
                                                onClick = {
                                                    vm.setUlp(u.id)
                                                    showUlpDropdown = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            Text(
                                text = "Update: ${settings.waktuUpdate} WIB",
                                fontSize = 10.sp,
                                color = Slate300
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    },
                    actions = {
                        // Direct WhatsApp PIC Button in TopAppBar
                        IconButton(
                            onClick = { openActiveUlpWhatsApp() },
                            modifier = Modifier.testTag("topbar_wa_btn")
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = "WA PIC", tint = Color(0xFF34D399))
                        }

                        // Quick Period Switcher (Pagi / Siang / Sore)
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(PlnNavyDark)
                                .padding(2.dp)
                        ) {
                            listOf("pagi", "siang", "sore").forEach { p ->
                                val isSelected = selectedPeriod == p
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) PlnGold else Color.Transparent)
                                        .clickable { vm.setPeriod(p) }
                                        .padding(horizontal = 6.dp, vertical = 3.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = p.replaceFirstChar { it.uppercase() },
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) PlnNavyDark else Slate300
                                    )
                                }
                            }
                        }

                        IconButton(onClick = { vm.saveAllChanges() }) {
                            Icon(Icons.Default.Save, contentDescription = "Simpan", tint = PlnEmerald)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = PlnNavyPrimary
                    )
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when {
                    activeMenu == "tabel_utama" -> {
                        MainTableScreen(
                            processedOfficers = processedOfficers,
                            rekapUlp = rekapUlp,
                            settings = settings,
                            activeUlp = activeUlp,
                            selectedPeriod = selectedPeriod,
                            customFeatures = customFeatures,
                            onSaveAll = { vm.saveAllChanges() }
                        )
                    }
                    activeMenu == "dashboard" -> {
                        DashboardScreen(
                            rekap = rekapUlp,
                            processedOfficers = processedOfficers,
                            activeUlp = activeUlp,
                            allUlps = DefaultData.DAFTAR_ULP,
                            currentUser = currentUser,
                            customFeatures = customFeatures,
                            onSelectUlp = { vm.setUlp(it) }
                        )
                    }
                    activeMenu == "kontak_pic" -> {
                        PicContactScreen(
                            currentUlp = activeUlp,
                            allUlps = DefaultData.DAFTAR_ULP
                        )
                    }
                    activeMenu == "ai_assistant" -> {
                        ChatbotScreen(
                            activeUlp = activeUlp,
                            rekap = rekapUlp,
                            processedOfficers = processedOfficers
                        )
                    }
                    activeMenu == "gas_scripts" -> {
                        GasScriptViewerScreen()
                    }
                    activeMenu == "upload" -> {
                        DailyUploadScreen(
                            activeUlp = activeUlp,
                            processedOfficers = processedOfficers,
                            settings = settings,
                            currentUser = currentUser,
                            onSaveSnapshot = { vm.addSnapshot(it) }
                        )
                    }
                    activeMenu == "master_petugas" -> {
                        MasterOfficersScreen(
                            officers = currentOfficers,
                            activeUlp = activeUlp,
                            onAddOfficer = { vm.addOfficer(it) },
                            onUpdateOfficer = { vm.updateOfficer(it) },
                            onDeleteOfficer = { vm.deleteOfficer(it) },
                            onClearOfficers = { vm.clearOfficers() },
                            onResetDefaults = { vm.replaceOfficers(DefaultData.generateOfficers(activeUlp.id)) }
                        )
                    }
                    activeMenu == "mon_p2dp" -> {
                        MonitoringDomainScreen(
                            domain = MonitoringDomain.P2DP,
                            processedOfficers = processedOfficers,
                            rekap = rekapUlp,
                            settings = settings,
                            activeUlp = activeUlp
                        )
                    }
                    activeMenu == "mon_preventif" -> {
                        MonitoringDomainScreen(
                            domain = MonitoringDomain.PREVENTIF,
                            processedOfficers = processedOfficers,
                            rekap = rekapUlp,
                            settings = settings,
                            activeUlp = activeUlp
                        )
                    }
                    activeMenu == "mon_korektif" -> {
                        MonitoringDomainScreen(
                            domain = MonitoringDomain.KOREKTIF,
                            processedOfficers = processedOfficers,
                            rekap = rekapUlp,
                            settings = settings,
                            activeUlp = activeUlp
                        )
                    }
                    activeMenu == "ranking" -> {
                        RankingScreen(
                            processedOfficers = processedOfficers,
                            settings = settings,
                            activeUlp = activeUlp
                        )
                    }
                    activeMenu == "rekap_ulp" -> {
                        RekapUlpScreen(
                            rekap = rekapUlp,
                            activeUlp = activeUlp,
                            settings = settings
                        )
                    }
                    activeMenu == "riwayat" -> {
                        HistoryScreen(
                            historyList = historyList,
                            processedOfficers = processedOfficers,
                            activeUlp = activeUlp
                        )
                    }
                    activeMenu == "ekspor" -> {
                        ExportPresentationScreen(
                            rekap = rekapUlp,
                            processedOfficers = processedOfficers,
                            activeUlp = activeUlp,
                            settings = settings
                        )
                    }
                    activeMenu == "pengaturan" -> {
                        SettingsScreen(
                            currentSettings = settings,
                            onSaveSettings = { vm.updateSettings(it) }
                        )
                    }
                    activeMenu == "n8n_source" -> {
                        N8nAutomationScreen(
                            settings = settings,
                            rekap = rekapUlp,
                            activeUlp = activeUlp,
                            onLockMenu = {
                                vm.lockMenu12()
                                activeMenu = "tabel_utama"
                            }
                        )
                    }
                    activeMenu == "db_dan_fitur" -> {
                        DatabaseFeatureBuilderScreen(
                            customFeatures = customFeatures,
                            onAddFeature = { vm.addCustomFeature(it) },
                            onRemoveFeature = { vm.removeCustomFeature(it) },
                            onToggleShowInTable = { id, show -> vm.toggleFeatureInMainTable(id, show) },
                            onToggleIncludeInTotal = { id, inc -> vm.toggleFeatureInTotalUlp(id, inc) },
                            onNavigateToCustomModule = { id -> activeMenu = "custom_$id" }
                        )
                    }
                    activeMenu.startsWith("custom_") -> {
                        val featId = activeMenu.removePrefix("custom_")
                        val featIndex = customFeatures.indexOfFirst { it.id == featId }
                        val feat = customFeatures.getOrNull(featIndex)
                        if (feat != null) {
                            CustomModuleScreen(
                                feature = feat,
                                menuNumber = 14 + featIndex,
                                processedOfficers = processedOfficers,
                                rekap = rekapUlp,
                                settings = settings,
                                activeUlp = activeUlp,
                                onUpdateOfficerMetric = { offId, fId, t, r ->
                                    vm.updateCustomMetric(offId, fId, t, r)
                                }
                            )
                        } else {
                            activeMenu = "tabel_utama"
                        }
                    }
                }

                // Floating Toast Notification
                if (toastMessage != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = PlnNavyDark,
                            shadowElevation = 8.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PlnEmerald, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = toastMessage ?: "",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Security Dialog for Menu 12
    SecurityAuthDialog(
        isOpen = showMenu12AuthDialog,
        title = "Otorisasi Super Admin — Menu 12",
        prompt = "Masukkan Kode Khusus Super Admin untuk membuka halaman Integrasi n8n & Kode Lokal:",
        onDismiss = { showMenu12AuthDialog = false },
        onVerify = { code ->
            val ok = vm.unlockMenu12(code)
            if (ok) {
                showMenu12AuthDialog = false
                activeMenu = "n8n_source"
                scope.launch { drawerState.close() }
            }
            ok
        }
    )
}
