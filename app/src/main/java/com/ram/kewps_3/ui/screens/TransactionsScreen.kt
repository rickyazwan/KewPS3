package com.ram.kewps_3.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ram.kewps_3.data.StockItem
import com.ram.kewps_3.data.Transaction
import com.ram.kewps_3.viewmodel.KewPS3ViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    viewModel: KewPS3ViewModel,
    modifier: Modifier = Modifier
) {
    val stockItems by viewModel.stockItems.collectAsStateWithLifecycle(initialValue = emptyList())
    val transactions by viewModel.transactions.collectAsStateWithLifecycle(initialValue = emptyList())
    
    var selectedDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var documentType by remember { mutableStateOf("") }
    var documentNo by remember { mutableStateOf("") }
    var selectedStockItem by remember { mutableStateOf<StockItem?>(null) }
    var transactionType by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var unitPrice by remember { mutableStateOf("") }
    var receivedFrom by remember { mutableStateOf("") }
    var issuedTo by remember { mutableStateOf("") }
    var officerName by remember { mutableStateOf("") }
    
    var documentTypeExpanded by remember { mutableStateOf(false) }
    var stockItemExpanded by remember { mutableStateOf(false) }
    var transactionTypeExpanded by remember { mutableStateOf(false) }
    
    val documentTypes = listOf(
        "PK" to "PK - Pesanan Kerajaan",
        "BTB" to "BTB - Borang Terimaan Barang",
        "BPSS" to "BPSS - Borang Permohonan Stok (KEW.PS-7)",
        "BPSI" to "BPSI - Borang Permohonan Stok (KEW.PS-8)",
        "BPIN" to "BPIN - Borang Pindahan Stok (KEW.PS-17)"
    )
    
    val transactionTypes = listOf("terimaan", "keluaran")
    
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Transaksi Stok (Bahagian B)",
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
                        text = "Rekod Transaksi Baru",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(selectedDate)),
                            onValueChange = { },
                            label = { Text("Tarikh") },
                            readOnly = true,
                            modifier = Modifier.weight(1f)
                        )
                        
                        ExposedDropdownMenuBox(
                            expanded = documentTypeExpanded,
                            onExpandedChange = { documentTypeExpanded = !documentTypeExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = documentTypes.find { it.first == documentType }?.second ?: "",
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Jenis Dokumen") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = documentTypeExpanded) },
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = documentTypeExpanded,
                                onDismissRequest = { documentTypeExpanded = false }
                            ) {
                                documentTypes.forEach { (code, description) ->
                                    DropdownMenuItem(
                                        text = { Text(description) },
                                        onClick = {
                                            documentType = code
                                            documentTypeExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                        
                        OutlinedTextField(
                            value = documentNo,
                            onValueChange = { documentNo = it },
                            label = { Text("No. Dokumen") },
                            placeholder = { Text("No. dokumen") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = stockItemExpanded,
                            onExpandedChange = { stockItemExpanded = !stockItemExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = selectedStockItem?.let { "${it.cardNo} - ${it.stockDescription}" } ?: "",
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Pilih Item Stok") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stockItemExpanded) },
                                modifier = Modifier.menuAnchor()
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
                        
                        ExposedDropdownMenuBox(
                            expanded = transactionTypeExpanded,
                            onExpandedChange = { transactionTypeExpanded = !transactionTypeExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = when (transactionType) {
                                    "terimaan" -> "Terimaan"
                                    "keluaran" -> "Keluaran"
                                    else -> ""
                                },
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Jenis Transaksi") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = transactionTypeExpanded) },
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = transactionTypeExpanded,
                                onDismissRequest = { transactionTypeExpanded = false }
                            ) {
                                transactionTypes.forEach { type ->
                                    DropdownMenuItem(
                                        text = { Text(if (type == "terimaan") "Terimaan" else "Keluaran") },
                                        onClick = {
                                            transactionType = type
                                            transactionTypeExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    
                    if (transactionType == "terimaan") {
                        Text(
                            text = "Maklumat Terimaan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedTextField(
                                value = receivedFrom,
                                onValueChange = { receivedFrom = it },
                                label = { Text("Terima Daripada") },
                                placeholder = { Text("Pembekal/Stor/Agensi") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = quantity,
                                onValueChange = { quantity = it },
                                label = { Text("Kuantiti") },
                                placeholder = { Text("Kuantiti diterima") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = unitPrice,
                                onValueChange = { unitPrice = it },
                                label = { Text("Harga Seunit (RM)") },
                                placeholder = { Text("0.00") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    } else if (transactionType == "keluaran") {
                        Text(
                            text = "Maklumat Keluaran",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedTextField(
                                value = issuedTo,
                                onValueChange = { issuedTo = it },
                                label = { Text("Keluar Kepada") },
                                placeholder = { Text("Stor/Pengguna/Agensi") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = quantity,
                                onValueChange = { quantity = it },
                                label = { Text("Kuantiti") },
                                placeholder = { Text("Kuantiti dikeluarkan") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    
                    OutlinedTextField(
                        value = officerName,
                        onValueChange = { officerName = it },
                        label = { Text("Nama Pegawai Stor") },
                        placeholder = { Text("Nama pegawai") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = {
                                selectedStockItem?.let { stockItem ->
                                    if (validateTransactionForm(documentType, documentNo, transactionType, quantity, officerName)) {
                                        viewModel.processTransaction(
                                            date = selectedDate,
                                            documentType = documentType,
                                            documentNo = documentNo,
                                            stockItemId = stockItem.id,
                                            stockDescription = stockItem.stockDescription,
                                            type = transactionType,
                                            quantity = quantity.toIntOrNull() ?: 0,
                                            unitPrice = unitPrice.toDoubleOrNull() ?: 0.0,
                                            receivedFrom = receivedFrom,
                                            issuedTo = issuedTo,
                                            officerName = officerName
                                        )
                                        
                                        documentType = ""
                                        documentNo = ""
                                        selectedStockItem = null
                                        transactionType = ""
                                        quantity = ""
                                        unitPrice = ""
                                        receivedFrom = ""
                                        issuedTo = ""
                                        officerName = ""
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Rekod Transaksi")
                        }
                        
                        OutlinedButton(
                            onClick = {
                                documentType = ""
                                documentNo = ""
                                selectedStockItem = null
                                transactionType = ""
                                quantity = ""
                                unitPrice = ""
                                receivedFrom = ""
                                issuedTo = ""
                                officerName = ""
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
        
        item {
            Text(
                text = "Sejarah Transaksi",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    if (transactions.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Tiada transaksi. Sila rekod transaksi baru.",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Tarikh",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "No. Dokumen",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "Item",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(2f)
                            )
                            Text(
                                text = "Jenis",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "Kuantiti",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "Harga (RM)",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "Pegawai",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        
                        HorizontalDivider()
                    }
                }
            }
        }
        
        items(transactions) { transaction ->
            TransactionRow(transaction = transaction)
        }
    }
}

@Composable
fun TransactionRow(
    transaction: Transaction,
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
                text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(transaction.date)),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${transaction.documentType}-${transaction.documentNo}",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = transaction.stockDescription,
                modifier = Modifier.weight(2f),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = if (transaction.type == "terimaan") "Terimaan" else "Keluaran",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                color = if (transaction.type == "terimaan") Color.Green else Color.Red,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = transaction.quantity.toString(),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = String.format("%.2f", transaction.totalPrice),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = transaction.officerName,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun validateTransactionForm(
    documentType: String,
    documentNo: String,
    transactionType: String,
    quantity: String,
    officerName: String
): Boolean {
    return documentType.isNotBlank() &&
            documentNo.isNotBlank() &&
            transactionType.isNotBlank() &&
            quantity.toIntOrNull() != null &&
            officerName.isNotBlank()
} 