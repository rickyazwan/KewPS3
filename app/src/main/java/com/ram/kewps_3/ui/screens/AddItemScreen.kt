package com.ram.kewps_3.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ram.kewps_3.viewmodel.KewPS3ViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(
    viewModel: KewPS3ViewModel,
    modifier: Modifier = Modifier
) {
    var storeName by remember { mutableStateOf("") }
    var stockDescription by remember { mutableStateOf("") }
    var codeNo by remember { mutableStateOf("") }
    var unitMeasurement by remember { mutableStateOf("") }
    var group by remember { mutableStateOf("") }
    var movement by remember { mutableStateOf("Aktif") }
    var warehouse by remember { mutableStateOf("") }
    var row by remember { mutableStateOf("") }
    var rack by remember { mutableStateOf("") }
    var level by remember { mutableStateOf("") }
    var compartment by remember { mutableStateOf("") }
    var maxStock by remember { mutableStateOf("") }
    var reorderStock by remember { mutableStateOf("") }
    var minStock by remember { mutableStateOf("") }
    var initialStock by remember { mutableStateOf("") }
    
    var unitExpanded by remember { mutableStateOf(false) }
    var groupExpanded by remember { mutableStateOf(false) }
    var movementExpanded by remember { mutableStateOf(false) }
    
    val unitOptions = listOf("buah", "batang", "bilah", "kotak", "rim", "kg", "liter","unit")
    val groupOptions = listOf("A", "B")
    val movementOptions = listOf("Aktif", "Tidak Aktif")
    
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Tambah Item Stok Baru (Bahagian A)",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Maklumat Asas",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = "Auto-generated",
                            onValueChange = { },
                            label = { Text("No. Kad") },
                            enabled = false,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = storeName,
                            onValueChange = { storeName = it },
                            label = { Text("Nama Stor") },
                            placeholder = { Text("Contoh: Stor Utama Jabatan...") },
                            modifier = Modifier.weight(2f)
                        )
                    }
                    
                    OutlinedTextField(
                        value = stockDescription,
                        onValueChange = { stockDescription = it },
                        label = { Text("Perihal Stok") },
                        placeholder = { Text("Contoh: Kertas A4 Putih 80 gsm") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = codeNo,
                            onValueChange = { codeNo = it },
                            label = { Text("No. Kod") },
                            placeholder = { Text("Kod berdasarkan Pusat Rujukan") },
                            modifier = Modifier.weight(1f)
                        )
                        
                        ExposedDropdownMenuBox(
                            expanded = unitExpanded,
                            onExpandedChange = { unitExpanded = !unitExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = unitMeasurement,
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Unit Pengukuran") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded) },
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = unitExpanded,
                                onDismissRequest = { unitExpanded = false }
                            ) {
                                unitOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            unitMeasurement = option
                                            unitExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = groupExpanded,
                            onExpandedChange = { groupExpanded = !groupExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = group,
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Kumpulan") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = groupExpanded) },
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = groupExpanded,
                                onDismissRequest = { groupExpanded = false }
                            ) {
                                groupOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text("Kumpulan $option") },
                                        onClick = {
                                            group = option
                                            groupExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                        
                        ExposedDropdownMenuBox(
                            expanded = movementExpanded,
                            onExpandedChange = { movementExpanded = !movementExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = movement,
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Pergerakan") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = movementExpanded) },
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = movementExpanded,
                                onDismissRequest = { movementExpanded = false }
                            ) {
                                movementOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            movement = option
                                            movementExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Lokasi Penyimpanan",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = warehouse,
                            onValueChange = { warehouse = it },
                            label = { Text("Gudang/Seksyen") },
                            placeholder = { Text("Nama gudang/seksyen") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = row,
                            onValueChange = { row = it },
                            label = { Text("Baris") },
                            placeholder = { Text("Baris") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = rack,
                            onValueChange = { rack = it },
                            label = { Text("Rak") },
                            placeholder = { Text("Rak") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = level,
                            onValueChange = { level = it },
                            label = { Text("Tingkat") },
                            placeholder = { Text("Tingkat") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = compartment,
                            onValueChange = { compartment = it },
                            label = { Text("Petak") },
                            placeholder = { Text("Petak") },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
        
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Paras Stok",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = maxStock,
                            onValueChange = { maxStock = it },
                            label = { Text("Maksimum (3 bulan penggunaan)") },
                            placeholder = { Text("Kuantiti maksimum") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = reorderStock,
                            onValueChange = { reorderStock = it },
                            label = { Text("Menokok (2 bulan penggunaan)") },
                            placeholder = { Text("Kuantiti menokok") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = minStock,
                            onValueChange = { minStock = it },
                            label = { Text("Minimum (1 bulan penggunaan)") },
                            placeholder = { Text("Kuantiti minimum") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Column {
                        OutlinedTextField(
                            value = initialStock,
                            onValueChange = { initialStock = it },
                            label = { Text("Stok Awal") },
                            placeholder = { Text("Kuantiti stok semasa") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "Masukkan kuantiti stok semasa jika ada. Jika tidak ada, biarkan kosong (0).",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
                }
            }
        }
        
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        if (validateForm(storeName, stockDescription, codeNo, unitMeasurement, group, maxStock, reorderStock, minStock)) {
                            viewModel.addStockItem(
                                storeName = storeName,
                                stockDescription = stockDescription,
                                codeNo = codeNo,
                                unitMeasurement = unitMeasurement,
                                group = group,
                                movement = movement,
                                warehouse = warehouse,
                                row = row,
                                rack = rack,
                                level = level,
                                compartment = compartment,
                                maxStock = maxStock.toIntOrNull() ?: 0,
                                reorderStock = reorderStock.toIntOrNull() ?: 0,
                                minStock = minStock.toIntOrNull() ?: 0,
                                initialStock = initialStock.toIntOrNull() ?: 0
                            )
                            
                            // Clear form
                            storeName = ""
                            stockDescription = ""
                            codeNo = ""
                            unitMeasurement = ""
                            group = ""
                            movement = "Aktif"
                            warehouse = ""
                            row = ""
                            rack = ""
                            level = ""
                            compartment = ""
                            maxStock = ""
                            reorderStock = ""
                            minStock = ""
                            initialStock = ""
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Simpan Item")
                }
                
                OutlinedButton(
                    onClick = {
                        storeName = ""
                        stockDescription = ""
                        codeNo = ""
                        unitMeasurement = ""
                        group = ""
                        movement = "Aktif"
                        warehouse = ""
                        row = ""
                        rack = ""
                        level = ""
                        compartment = ""
                        maxStock = ""
                        reorderStock = ""
                        minStock = ""
                        initialStock = ""
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Clear, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Kosongkan")
                }
            }
        }
    }
}

private fun validateForm(
    storeName: String,
    stockDescription: String,
    codeNo: String,
    unitMeasurement: String,
    group: String,
    maxStock: String,
    reorderStock: String,
    minStock: String
): Boolean {
    return storeName.isNotBlank() &&
            stockDescription.isNotBlank() &&
            codeNo.isNotBlank() &&
            unitMeasurement.isNotBlank() &&
            group.isNotBlank() &&
            maxStock.toIntOrNull() != null &&
            reorderStock.toIntOrNull() != null &&
            minStock.toIntOrNull() != null
} 