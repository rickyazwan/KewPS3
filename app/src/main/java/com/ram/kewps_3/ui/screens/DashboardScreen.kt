package com.ram.kewps_3.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
                                modifier = Modifier.weight(1f)
                            )
                        }
                        
                        HorizontalDivider()
                    }
                }
            }
        }
        
        items(stockItems) { item ->
            StockItemRow(
                stockItem = item,
                onViewClick = { /* TODO: Show item details */ }
            )
        }
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
fun StockItemRow(
    stockItem: StockItem,
    onViewClick: () -> Unit,
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
            
            // Status
            val statusText = when {
                stockItem.currentBalance <= stockItem.minStock -> "Stok Rendah"
                stockItem.currentBalance <= stockItem.reorderStock -> "Perlu Reorder"
                else -> "Normal"
            }
            val statusColor = when {
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
            
            IconButton(
                onClick = onViewClick,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    Icons.Default.Visibility,
                    contentDescription = "Lihat",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
} 