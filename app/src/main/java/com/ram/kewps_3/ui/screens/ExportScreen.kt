package com.ram.kewps_3.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ram.kewps_3.data.StockItem
import com.ram.kewps_3.utils.DocumentExporter
import com.ram.kewps_3.viewmodel.KewPS3ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(
    viewModel: KewPS3ViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dashboardStats by viewModel.dashboardStats.collectAsStateWithLifecycle()
    val stockItems by viewModel.stockItems.collectAsStateWithLifecycle(initialValue = emptyList())
    val transactions by viewModel.transactions.collectAsStateWithLifecycle(initialValue = emptyList())
    
    var selectedStockItem by remember { mutableStateOf<StockItem?>(null) }
    var stockItemExpanded by remember { mutableStateOf(false) }
    var isExporting by remember { mutableStateOf(false) }
    var exportMessage by remember { mutableStateOf<String?>(null) }
    
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Eksport Dokumen KEW.PS-3",
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
                        text = "Eksport Dokumen DOCX KEW.PS-3",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "Eksport dokumen rasmi KEW.PS-3 dalam format Microsoft Word yang mengikuti borang asal Pekeliling Perbendaharaan Malaysia AM 6.3.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    ExposedDropdownMenuBox(
                        expanded = stockItemExpanded,
                        onExpandedChange = { stockItemExpanded = !stockItemExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedStockItem?.let { "${it.cardNo} - ${it.stockDescription}" } ?: "",
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Pilih Item untuk Eksport") },
                            placeholder = { Text("Pilih item stok...") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stockItemExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = stockItemExpanded,
                            onDismissRequest = { stockItemExpanded = false }
                        ) {
                            stockItems.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text("${item.cardNo} - ${item.stockDescription}") },
                                    onClick = {
                                        selectedStockItem = item
                                        stockItemExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    
                    Button(
                        onClick = {
                            selectedStockItem?.let { stockItem ->
                                scope.launch {
                                    isExporting = true
                                    try {
                                        val itemTransactions = transactions.filter { it.stockItemId == stockItem.id }
                                        val documentExporter = DocumentExporter(context)
                                        
                                        val file = withContext(Dispatchers.IO) {
                                            documentExporter.exportKEWPS3Document(stockItem, itemTransactions)
                                        }
                                        
                                        if (file != null) {
                                            exportMessage = "Dokumen KEW.PS-3 berjaya dieksport ke: ${file.name}"
                                            // Optionally share the document
                                            documentExporter.shareDocument(file)
                                        } else {
                                            exportMessage = "Ralat mengeksport dokumen"
                                        }
                                    } catch (e: Exception) {
                                        exportMessage = "Ralat: ${e.message}"
                                    } finally {
                                        isExporting = false
                                    }
                                }
                            }
                        },
                        enabled = selectedStockItem != null && !isExporting,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isExporting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Mengeksport...")
                        } else {
                            Icon(Icons.Default.FileDownload, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("📄 Eksport Dokumen KEW.PS-3 (DOCX)")
                        }
                    }
                    
                    exportMessage?.let { message ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (message.contains("berjaya")) 
                                    MaterialTheme.colorScheme.primaryContainer 
                                else 
                                    MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(
                                    onClick = { exportMessage = null }
                                ) {
                                    Text("Tutup")
                                }
                            }
                        }
                    }
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "📋 Maklumat:",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Dokumen yang dieksport akan mengikuti format rasmi KEW.PS-3 dengan Bahagian A (Maklumat Stok) dan Bahagian B (Transaksi Stok).",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
        
        item {
            Text(
                text = "Statistik Data",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Jumlah Item (Bahagian A)",
                    value = dashboardStats.totalItems.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Jumlah Transaksi (Bahagian B)",
                    value = (dashboardStats.totalReceipts + dashboardStats.totalIssues).toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Maklumat Tambahan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Transaksi Terimaan:")
                        Text(
                            text = dashboardStats.totalReceipts.toString(),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Transaksi Keluaran:")
                        Text(
                            text = dashboardStats.totalIssues.toString(),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Item Stok Rendah:")
                        Text(
                            text = dashboardStats.lowStock.toString(),
                            fontWeight = FontWeight.Bold,
                            color = if (dashboardStats.lowStock > 0) 
                                MaterialTheme.colorScheme.error 
                            else 
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
        
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Format Dokumen",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "• Dokumen dieksport dalam format Microsoft Word (.docx)",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "• Mengikuti format rasmi Pekeliling Perbendaharaan Malaysia AM 6.3",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "• Termasuk Bahagian A (Maklumat Stok) dan Bahagian B (Transaksi)",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "• Boleh dibuka dengan Microsoft Word, Google Docs, atau aplikasi serupa",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
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
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
} 