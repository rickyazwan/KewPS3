package com.ram.kewps_3.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ram.kewps_3.data.StockItem
import com.ram.kewps_3.viewmodel.KewPS3ViewModel

@Composable
fun DashboardScreen(
    viewModel: KewPS3ViewModel,
    modifier: Modifier = Modifier
) {
    val dashboardStats by viewModel.dashboardStats.collectAsStateWithLifecycle()
    val stockItems by viewModel.stockItems.collectAsStateWithLifecycle(initialValue = emptyList())
    var showDeleteDialog by remember { mutableStateOf<StockItem?>(null) }
    var showViewDialog by remember { mutableStateOf<StockItem?>(null) }
    var showEditDialog by remember { mutableStateOf<StockItem?>(null) }
    
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Dashboard Stok",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Jumlah Item",
                    value = dashboardStats.totalItems.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Jumlah Terimaan",
                    value = dashboardStats.totalReceipts.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Jumlah Keluaran",
                    value = dashboardStats.totalIssues.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Stok Rendah",
                    value = dashboardStats.lowStock.toString(),
                    modifier = Modifier.weight(1f),
                    valueColor = if (dashboardStats.lowStock > 0) Color.Red else MaterialTheme.colorScheme.onSurface
                )
            }
        }
        
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Senarai Item Stok",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (stockItems.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Tiada data. Sila tambah item baru.",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        // Header row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "No. Kad",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "Perihal Stok",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(2f)
                            )
                            Text(
                                text = "Kumpulan",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "Baki",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "Status",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "Aksi",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1.5f)
                            )
                        }
                        
                        HorizontalDivider()
                    }
                }
            }
        }
        
        items(stockItems) { item ->
            StockItemRowWithDelete(
                stockItem = item,
                onViewClick = { showViewDialog = item },
                onEditClick = { showEditDialog = item },
                onDeleteClick = { showDeleteDialog = item }
            )
        }
    }

    // View Dialog
    showViewDialog?.let { item ->
        ViewItemDialog(
            stockItem = item,
            onDismiss = { showViewDialog = null }
        )
    }

    // Edit Dialog
    showEditDialog?.let { item ->
        EditItemDialog(
            stockItem = item,
            onDismiss = { showEditDialog = null },
            onSave = { updatedItem ->
                viewModel.updateStockItem(updatedItem)
                showEditDialog = null
            },
            onDelete = { itemToDelete ->
                viewModel.deleteStockItem(itemToDelete)
                showEditDialog = null
            }
        )
    }

    // Delete Confirmation Dialog
    showDeleteDialog?.let { item ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Padam Item") },
            text = { 
                Text("Adakah anda pasti untuk memadam item '${item.stockDescription}'? Tindakan ini tidak boleh dibatalkan.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteStockItem(item)
                        showDeleteDialog = null
                    }
                ) {
                    Text("Padam", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun StockItemRowWithDelete(
    stockItem: StockItem,
    onViewClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stockItem.cardNo,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = stockItem.stockDescription,
                modifier = Modifier.weight(2f),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = stockItem.group,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${stockItem.currentBalance} ${stockItem.unitMeasurement}",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium
            )
            
            // Status - Fixed logic to handle edge cases
            val statusText = when {
                stockItem.minStock <= 0 -> "Tidak Ditetapkan" // Handle cases where minStock is not set
                stockItem.currentBalance <= stockItem.minStock -> "Stok Rendah"
                stockItem.currentBalance <= stockItem.reorderStock -> "Perlu Reorder"
                else -> "Normal"
            }
            val statusColor = when {
                stockItem.minStock <= 0 -> Color.Gray
                stockItem.currentBalance <= stockItem.minStock -> Color.Red
                stockItem.currentBalance <= stockItem.reorderStock -> Color(0xFFFFA500)
                else -> Color.Green
            }
            
            Text(
                text = statusText,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodySmall,
                color = statusColor,
                fontWeight = FontWeight.Bold
            )
            
            // Action buttons - DEBUG: Three buttons should be visible
            Row(
                modifier = Modifier.weight(1.5f),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = onViewClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Visibility,
                        contentDescription = "Lihat",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Button(
                    onClick = onDeleteClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        text = "DEL",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
fun ViewItemDialog(
    stockItem: StockItem,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Butiran Item") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DetailRow("No. Kad:", stockItem.cardNo)
                DetailRow("Kedai:", stockItem.storeName)
                DetailRow("Perihal:", stockItem.stockDescription)
                DetailRow("Kod No:", stockItem.codeNo)
                DetailRow("Unit:", stockItem.unitMeasurement)
                DetailRow("Kumpulan:", stockItem.group)
                DetailRow("Pergerakan:", stockItem.movement)
                DetailRow("Gudang:", stockItem.warehouse)
                DetailRow("Baris:", stockItem.row)
                DetailRow("Rak:", stockItem.rack)
                DetailRow("Aras:", stockItem.level)
                DetailRow("Petak:", stockItem.compartment)
                DetailRow("Stok Maksimum:", stockItem.maxStock.toString())
                DetailRow("Stok Reorder:", stockItem.reorderStock.toString())
                DetailRow("Stok Minimum:", stockItem.minStock.toString())
                DetailRow("Baki Semasa:", "${stockItem.currentBalance} ${stockItem.unitMeasurement}")
                DetailRow("Jumlah Diterima:", "${stockItem.totalReceived} ${stockItem.unitMeasurement}")
                DetailRow("Jumlah Dikeluarkan:", "${stockItem.totalIssued} ${stockItem.unitMeasurement}")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup")
            }
        }
    )
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditItemDialog(
    stockItem: StockItem,
    onDismiss: () -> Unit,
    onSave: (StockItem) -> Unit,
    onDelete: (StockItem) -> Unit = {}
) {
    var storeName by remember { mutableStateOf(stockItem.storeName) }
    var stockDescription by remember { mutableStateOf(stockItem.stockDescription) }
    var codeNo by remember { mutableStateOf(stockItem.codeNo) }
    var unitMeasurement by remember { mutableStateOf(stockItem.unitMeasurement) }
    var group by remember { mutableStateOf(stockItem.group) }
    var movement by remember { mutableStateOf(stockItem.movement) }
    var warehouse by remember { mutableStateOf(stockItem.warehouse) }
    var row by remember { mutableStateOf(stockItem.row) }
    var rack by remember { mutableStateOf(stockItem.rack) }
    var level by remember { mutableStateOf(stockItem.level) }
    var compartment by remember { mutableStateOf(stockItem.compartment) }
    var maxStock by remember { mutableStateOf(stockItem.maxStock.toString()) }
    var reorderStock by remember { mutableStateOf(stockItem.reorderStock.toString()) }
    var minStock by remember { mutableStateOf(stockItem.minStock.toString()) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Item") },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.heightIn(max = 400.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = storeName,
                        onValueChange = { storeName = it },
                        label = { Text("Nama Kedai") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = stockDescription,
                        onValueChange = { stockDescription = it },
                        label = { Text("Perihal Stok") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = codeNo,
                        onValueChange = { codeNo = it },
                        label = { Text("Kod No") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = unitMeasurement,
                        onValueChange = { unitMeasurement = it },
                        label = { Text("Unit Pengukuran") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = group,
                        onValueChange = { group = it },
                        label = { Text("Kumpulan") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = movement,
                        onValueChange = { movement = it },
                        label = { Text("Pergerakan") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = warehouse,
                        onValueChange = { warehouse = it },
                        label = { Text("Gudang") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = row,
                            onValueChange = { row = it },
                            label = { Text("Baris") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = rack,
                            onValueChange = { rack = it },
                            label = { Text("Rak") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = level,
                            onValueChange = { level = it },
                            label = { Text("Aras") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = compartment,
                            onValueChange = { compartment = it },
                            label = { Text("Petak") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = maxStock,
                            onValueChange = { maxStock = it },
                            label = { Text("Stok Max") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = reorderStock,
                            onValueChange = { reorderStock = it },
                            label = { Text("Reorder") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = minStock,
                            onValueChange = { minStock = it },
                            label = { Text("Stok Min") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(
                    onClick = { showDeleteConfirmation = true },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("Padam")
                }
                TextButton(
                    onClick = {
                        val updatedItem = stockItem.copy(
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
                            maxStock = maxStock.toIntOrNull() ?: stockItem.maxStock,
                            reorderStock = reorderStock.toIntOrNull() ?: stockItem.reorderStock,
                            minStock = minStock.toIntOrNull() ?: stockItem.minStock
                        )
                        onSave(updatedItem)
                    }
                ) {
                    Text("Simpan")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
    
    // Delete confirmation dialog
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Padam Item") },
            text = { 
                Text("Adakah anda pasti untuk memadam item '${stockItem.stockDescription}'? Tindakan ini tidak boleh dibatalkan.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(stockItem)
                        showDeleteConfirmation = false
                        onDismiss()
                    }
                ) {
                    Text("Padam", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Batal")
                }
            }
        )
    }
} 