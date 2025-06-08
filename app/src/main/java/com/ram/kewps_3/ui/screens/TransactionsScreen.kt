package com.ram.kewps_3.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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
    
    // Transaction management state
    var showTransactionDetails by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }
    
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "💼 Transaksi Stok (Bahagian B)",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
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
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "📝 Rekod Transaksi Baru",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                    
                    // Enhanced Document Information Section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "📄 Maklumat Dokumen",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            // Date Field - Full width for better visibility
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(selectedDate)),
                                    onValueChange = { },
                                    label = { 
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text("📅")
                                            Text("Tarikh Transaksi")
                                        }
                                    },
                                    readOnly = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                                    )
                                )
                            }
                            
                            // Document Type Field - Enhanced dropdown
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                ExposedDropdownMenuBox(
                                    expanded = documentTypeExpanded,
                                    onExpandedChange = { documentTypeExpanded = !documentTypeExpanded },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                ) {
                                    OutlinedTextField(
                                        value = documentTypes.find { it.first == documentType }?.second ?: "",
                                        onValueChange = { },
                                        readOnly = true,
                                        label = { 
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Text("📋")
                                                Text("Jenis Dokumen")
                                            }
                                        },
                                        placeholder = { Text("Pilih jenis dokumen...") },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = documentTypeExpanded) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MaterialTheme.colorScheme.secondary,
                                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                                        )
                                    )
                                    ExposedDropdownMenu(
                                        expanded = documentTypeExpanded,
                                        onDismissRequest = { documentTypeExpanded = false }
                                    ) {
                                        documentTypes.forEach { (code, description) ->
                                            DropdownMenuItem(
                                                text = { 
                                                    Text(
                                                        text = description,
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                },
                                                onClick = {
                                                    documentType = code
                                                    documentTypeExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            
                            // Document Number Field - Enhanced input
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.2f)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = documentNo,
                                    onValueChange = { documentNo = it },
                                    label = { 
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text("🔢")
                                            Text("Nombor Dokumen")
                                        }
                                    },
                                    placeholder = { Text("Masukkan nombor dokumen...") },
                                    supportingText = { Text("Contoh: 001/2024, INV-2024-001") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                                    )
                                )
                            }
                        }
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
                            text = "📥 Maklumat Terimaan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
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
                            text = "📤 Maklumat Keluaran",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
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
            TransactionRow(
                transaction = transaction,
                onView = { 
                    selectedTransaction = it
                    showTransactionDetails = true
                },
                onEdit = { 
                    selectedTransaction = it
                    showEditDialog = true
                },
                onDelete = { 
                    selectedTransaction = it
                    showDeleteDialog = true
                }
            )
        }
    }
    
    // Dialog components
    selectedTransaction?.let { transaction ->
        if (showTransactionDetails) {
            TransactionDetailsDialog(
                transaction = transaction,
                onDismiss = { 
                    showTransactionDetails = false
                    selectedTransaction = null
                }
            )
        }
        
        if (showEditDialog) {
            EditTransactionDialog(
                transaction = transaction,
                stockItems = stockItems,
                onSave = { updatedTransaction ->
                    viewModel.updateTransaction(updatedTransaction)
                    showEditDialog = false
                    selectedTransaction = null
                },
                onDismiss = { 
                    showEditDialog = false
                    selectedTransaction = null
                }
            )
        }
        
        if (showDeleteDialog) {
            DeleteTransactionDialog(
                transaction = transaction,
                onConfirm = {
                    viewModel.deleteTransaction(transaction)
                    showDeleteDialog = false
                    selectedTransaction = null
                },
                onDismiss = { 
                    showDeleteDialog = false
                    selectedTransaction = null
                }
            )
        }
    }
}

@Composable
fun TransactionRow(
    transaction: Transaction,
    onView: (Transaction) -> Unit,
    onEdit: (Transaction) -> Unit,
    onDelete: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
            
            // Action buttons row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onView(transaction) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Visibility, 
                        contentDescription = "Lihat Butiran",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(
                    onClick = { onEdit(transaction) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Edit, 
                        contentDescription = "Edit Transaksi",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
                IconButton(
                    onClick = { onDelete(transaction) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Delete, 
                        contentDescription = "Padam Transaksi",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
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

@Composable
fun TransactionDetailsDialog(
    transaction: Transaction,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Butiran Transaksi",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }
                
                HorizontalDivider()
                
                DetailRow("Tarikh", SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(transaction.date)))
                DetailRow("No. Dokumen", "${transaction.documentType}-${transaction.documentNo}")
                DetailRow("Item Stok", transaction.stockDescription)
                DetailRow("Jenis Transaksi", if (transaction.type == "terimaan") "Terimaan" else "Keluaran")
                DetailRow("Kuantiti", transaction.quantity.toString())
                DetailRow("Harga Seunit (RM)", String.format("%.2f", transaction.unitPrice))
                DetailRow("Jumlah Harga (RM)", String.format("%.2f", transaction.totalPrice))
                
                if (transaction.type == "terimaan" && transaction.receivedFrom.isNotBlank()) {
                    DetailRow("Terima Daripada", transaction.receivedFrom)
                }
                if (transaction.type == "keluaran" && transaction.issuedTo.isNotBlank()) {
                    DetailRow("Keluar Kepada", transaction.issuedTo)
                }
                
                DetailRow("Nama Pegawai", transaction.officerName)
                DetailRow("Masa Rekod", SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(transaction.timestamp)))
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Tutup")
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
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
fun EditTransactionDialog(
    transaction: Transaction,
    stockItems: List<StockItem>,
    onSave: (Transaction) -> Unit,
    onDismiss: () -> Unit
) {
    var documentType by remember { mutableStateOf(transaction.documentType) }
    var documentNo by remember { mutableStateOf(transaction.documentNo) }
    var selectedStockItem by remember { 
        mutableStateOf(stockItems.find { it.id == transaction.stockItemId })
    }
    var transactionType by remember { mutableStateOf(transaction.type) }
    var quantity by remember { mutableStateOf(transaction.quantity.toString()) }
    var unitPrice by remember { mutableStateOf(transaction.unitPrice.toString()) }
    var receivedFrom by remember { mutableStateOf(transaction.receivedFrom) }
    var issuedTo by remember { mutableStateOf(transaction.issuedTo) }
    var officerName by remember { mutableStateOf(transaction.officerName) }
    
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
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Edit Transaksi",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }
                
                HorizontalDivider()
                
                // Document Type and Number
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
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
                        modifier = Modifier.weight(1f)
                    )
                }
                
                // Stock Item and Transaction Type
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
                            label = { Text("Item Stok") },
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
                
                // Transaction specific fields
                if (transactionType == "terimaan") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = receivedFrom,
                            onValueChange = { receivedFrom = it },
                            label = { Text("Terima Daripada") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = quantity,
                            onValueChange = { quantity = it },
                            label = { Text("Kuantiti") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = unitPrice,
                            onValueChange = { unitPrice = it },
                            label = { Text("Harga Seunit (RM)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else if (transactionType == "keluaran") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = issuedTo,
                            onValueChange = { issuedTo = it },
                            label = { Text("Keluar Kepada") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = quantity,
                            onValueChange = { quantity = it },
                            label = { Text("Kuantiti") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                
                OutlinedTextField(
                    value = officerName,
                    onValueChange = { officerName = it },
                    label = { Text("Nama Pegawai") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Batal")
                    }
                    Button(
                        onClick = {
                            selectedStockItem?.let { stockItem ->
                                if (validateTransactionForm(documentType, documentNo, transactionType, quantity, officerName)) {
                                    val updatedTransaction = transaction.copy(
                                        documentType = documentType,
                                        documentNo = documentNo,
                                        stockItemId = stockItem.id,
                                        stockDescription = stockItem.stockDescription,
                                        type = transactionType,
                                        quantity = quantity.toIntOrNull() ?: 0,
                                        unitPrice = unitPrice.toDoubleOrNull() ?: 0.0,
                                        totalPrice = (quantity.toIntOrNull() ?: 0) * (unitPrice.toDoubleOrNull() ?: 0.0),
                                        receivedFrom = receivedFrom,
                                        issuedTo = issuedTo,
                                        officerName = officerName
                                    )
                                    onSave(updatedTransaction)
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Simpan")
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteTransactionDialog(
    transaction: Transaction,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Padam Transaksi",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }
                
                HorizontalDivider()
                
                Text(
                    text = "Adakah anda pasti ingin memadam transaksi ini?",
                    style = MaterialTheme.typography.bodyLarge
                )
                
                Text(
                    text = "Tindakan ini akan membalikkan kesan transaksi pada stok dan tidak boleh dibuat asal.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Butiran Transaksi:",
                            fontWeight = FontWeight.Bold
                        )
                        Text("${transaction.documentType}-${transaction.documentNo}")
                        Text(transaction.stockDescription)
                        Text("${if (transaction.type == "terimaan") "Terimaan" else "Keluaran"}: ${transaction.quantity}")
                        Text("RM ${String.format("%.2f", transaction.totalPrice)}")
                    }
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Batal")
                    }
                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Padam")
                    }
                }
            }
        }
    }
} 