package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.ui.components.SecurityAuthDialog
import com.example.ui.theme.*

@Composable
fun MasterOfficersScreen(
    officers: List<Officer>,
    activeUlp: UlpItem,
    onAddOfficer: (Officer) -> Unit,
    onUpdateOfficer: (Officer) -> Unit,
    onDeleteOfficer: (Int) -> Unit,
    onClearOfficers: () -> Unit,
    onResetDefaults: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var showAddModal by remember { mutableStateOf(false) }
    var editingOfficer by remember { mutableStateOf<Officer?>(null) }
    var officerToEditPendingAuth by remember { mutableStateOf<Officer?>(null) }
    var showAuthDialog by remember { mutableStateOf(false) }

    val filtered = remember(officers, searchQuery) {
        if (searchQuery.isBlank()) officers
        else officers.filter {
            it.petugas.contains(searchQuery, ignoreCase = true) ||
                    it.kodePetugas.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate100)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Header & Actions
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "DATA MASTER PETUGAS",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = PlnNavyPrimary
                        )
                        Text(
                            text = "${activeUlp.nama} • Total ${officers.size} Petugas",
                            fontSize = 11.sp,
                            color = Slate700
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = { showAddModal = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PlnEmerald),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("add_officer_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Tambah Petugas", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari petugas atau kode billman...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Officers List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filtered, key = { it.id }) { o ->
                OfficerCard(
                    officer = o,
                    onEditClick = {
                        officerToEditPendingAuth = o
                        showAuthDialog = true
                    },
                    onDeleteClick = {
                        onDeleteOfficer(o.id)
                    }
                )
            }
        }
    }

    // Security Dialog for Editing an Officer
    SecurityAuthDialog(
        isOpen = showAuthDialog,
        title = "Otorisasi Edit Petugas",
        prompt = "Masukkan sandi otorisasi edit sebelum mengubah data ${officerToEditPendingAuth?.petugas}:",
        onDismiss = {
            showAuthDialog = false
            officerToEditPendingAuth = null
        },
        onVerify = { code ->
            if (code == DefaultData.EDIT_OFFICER_CODE || code == DefaultData.SUPER_ADMIN_CODE) {
                editingOfficer = officerToEditPendingAuth
                showAuthDialog = false
                officerToEditPendingAuth = null
                true
            } else false
        }
    )

    // Edit Officer Dialog
    if (editingOfficer != null) {
        val curr = editingOfficer!!
        var name by remember { mutableStateOf(curr.petugas) }
        var pelgn by remember { mutableStateOf(curr.jumlahPelgn.toString()) }
        var p2dpT by remember { mutableStateOf(curr.p2dpTarget.toString()) }
        var p2dpR by remember { mutableStateOf(curr.p2dpReal.toString()) }
        var prevT by remember { mutableStateOf(curr.prevTarget.toString()) }
        var prevR by remember { mutableStateOf(curr.prevReal.toString()) }
        var korT by remember { mutableStateOf(curr.korTarget.toString()) }
        var korR by remember { mutableStateOf(curr.korReal.toString()) }

        AlertDialog(
            onDismissRequest = { editingOfficer = null },
            title = { Text("Edit Petugas: ${curr.kodePetugas}", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nama Petugas") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = pelgn,
                        onValueChange = { pelgn = it },
                        label = { Text("Jumlah Pelanggan") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(value = p2dpT, onValueChange = { p2dpT = it }, label = { Text("P2DP Target") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = p2dpR, onValueChange = { p2dpR = it }, label = { Text("P2DP Real") }, modifier = Modifier.weight(1f))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(value = prevT, onValueChange = { prevT = it }, label = { Text("Prev Target") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = prevR, onValueChange = { prevR = it }, label = { Text("Prev Real") }, modifier = Modifier.weight(1f))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(value = korT, onValueChange = { korT = it }, label = { Text("Kor Target") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = korR, onValueChange = { korR = it }, label = { Text("Kor Real") }, modifier = Modifier.weight(1f))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updated = curr.copy(
                            petugas = name.trim().uppercase(),
                            jumlahPelgn = pelgn.toIntOrNull() ?: curr.jumlahPelgn,
                            p2dpTarget = p2dpT.toIntOrNull() ?: curr.p2dpTarget,
                            p2dpReal = p2dpR.toIntOrNull() ?: curr.p2dpReal,
                            prevTarget = prevT.toIntOrNull() ?: curr.prevTarget,
                            prevReal = prevR.toIntOrNull() ?: curr.prevReal,
                            korTarget = korT.toIntOrNull() ?: curr.korTarget,
                            korReal = korR.toIntOrNull() ?: curr.korReal
                        )
                        onUpdateOfficer(updated)
                        editingOfficer = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PlnNavyPrimary)
                ) {
                    Text("Simpan Perubahan")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingOfficer = null }) {
                    Text("Batal")
                }
            }
        )
    }

    // Add Officer Dialog
    if (showAddModal) {
        var name by remember { mutableStateOf("") }
        var pelgn by remember { mutableStateOf("1250") }
        var p2dpT by remember { mutableStateOf("25") }
        var prevT by remember { mutableStateOf("20") }
        var korT by remember { mutableStateOf("10") }

        AlertDialog(
            onDismissRequest = { showAddModal = false },
            title = { Text("Tambah 1 Petugas Baru", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nama Lengkap Petugas") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = pelgn,
                        onValueChange = { pelgn = it },
                        label = { Text("Jumlah Pelanggan") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(value = p2dpT, onValueChange = { p2dpT = it }, label = { Text("Target P2DP") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = prevT, onValueChange = { prevT = it }, label = { Text("Target Prev") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = korT, onValueChange = { korT = it }, label = { Text("Target Kor") }, modifier = Modifier.weight(1f))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            val newOfficer = Officer(
                                id = officers.size + 1,
                                no = officers.size + 1,
                                kodePetugas = "${activeUlp.kodePrefix}-${(officers.size + 1).toString().padStart(3, '0')}",
                                petugas = name.trim().uppercase(),
                                wilayah = "RBM ${activeUlp.nama.replace("ULP ", "")}",
                                jumlahPelgn = pelgn.toIntOrNull() ?: 1200,
                                targetAll = (p2dpT.toIntOrNull() ?: 25) + (prevT.toIntOrNull() ?: 20) + (korT.toIntOrNull() ?: 10),
                                p2dpTarget = p2dpT.toIntOrNull() ?: 25,
                                p2dpReal = 0,
                                prevTarget = prevT.toIntOrNull() ?: 20,
                                prevReal = 0,
                                korTarget = korT.toIntOrNull() ?: 10,
                                korReal = 0,
                                pagi = PeriodMetric(0, 0, 0),
                                siang = PeriodMetric(0, 0, 0),
                                sore = PeriodMetric(0, 0, 0)
                            )
                            onAddOfficer(newOfficer)
                            showAddModal = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PlnEmerald)
                ) {
                    Text("Tambah")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddModal = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun OfficerCard(
    officer: Officer,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "#${officer.no} ${officer.petugas}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Slate900
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Slate100)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(officer.kodePetugas, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Slate700)
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${officer.wilayah} • ${PerformanceHelper.fmtNum(officer.jumlahPelgn)} Pelanggan",
                    fontSize = 11.sp,
                    color = Slate700
                )
                Text(
                    text = "P2DP: ${officer.p2dpReal}/${officer.p2dpTarget} • Prev: ${officer.prevReal}/${officer.prevTarget} • Kor: ${officer.korReal}/${officer.korTarget}",
                    fontSize = 10.sp,
                    color = PlnNavyPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF4F46E5), modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = PlnRose, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
