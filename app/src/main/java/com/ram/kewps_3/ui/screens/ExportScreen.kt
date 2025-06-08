package com.ram.kewps_3.ui.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.ram.kewps_3.utils.GoogleDocsIntegration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ram.kewps_3.data.StockItem
import com.ram.kewps_3.data.Transaction
import com.ram.kewps_3.viewmodel.KewPS3ViewModel
import kotlinx.coroutines.launch

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
    var isGenerating by remember { mutableStateOf(false) }
    var shareMessage by remember { mutableStateOf<String?>(null) }
    var isSignedIn by remember { mutableStateOf(false) }
    var copiedContent by remember { mutableStateOf<String?>(null) }
    var showCopiedContent by remember { mutableStateOf(false) }
    
    // Google Docs integration
    val googleDocsIntegration = remember { GoogleDocsIntegration(context) }
    val googleDocsUrl = "https://docs.google.com/document/d/1pbYb5NFCo0Oh2a4--9aGyT7yq7Irox_1/edit"
    
    // Google Sign-In launcher
    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                val account = task.getResult(ApiException::class.java)
                googleDocsIntegration.handleSignInResult(account)
                isSignedIn = true
                shareMessage = "Berjaya log masuk ke Google. Anda kini boleh mengisi dokumen secara automatik."
            } catch (e: ApiException) {
                shareMessage = "Gagal log masuk: ${e.message}"
            }
        }
    }
    
    // Check sign-in status on start
    LaunchedEffect(Unit) {
        isSignedIn = googleDocsIntegration.isSignedIn()
    }
    
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Statistics Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "📊 Statistik Sistem",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                    // Modern stats grid with cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Total Items Card
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = dashboardStats.totalItems.toString(),
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "Total Item\nStok",
                                    style = MaterialTheme.typography.labelMedium,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                        
                        // Total Receipts Card
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = dashboardStats.totalReceipts.toString(),
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "Total\nTerimaan",
                                    style = MaterialTheme.typography.labelMedium,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                        
                        // Total Issues Card
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f),
                                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = dashboardStats.totalIssues.toString(),
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "Total\nKeluaran",
                                    style = MaterialTheme.typography.labelMedium,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Google Docs Integration Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "📋 Smart Copy-Paste KEW.PS-3 ke Google Docs",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                    
                    Text(
                        text = "Sistem akan salin data KEW.PS-3 dengan format rasmi ke clipboard dan buka Google Docs. Anda hanya perlu tampal (Ctrl+V) untuk mengisi dokumen dengan sempurna.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Stock Item Selection
                    ExposedDropdownMenuBox(
                        expanded = stockItemExpanded,
                        onExpandedChange = { stockItemExpanded = !stockItemExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedStockItem?.let { "${it.cardNo} - ${it.stockDescription}" } ?: "Pilih item stok...",
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Pilih Item Stok") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stockItemExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        
                        ExposedDropdownMenu(
                            expanded = stockItemExpanded,
                            onDismissRequest = { stockItemExpanded = false }
                        ) {
                            stockItems.forEach { stockItem ->
                                DropdownMenuItem(
                                    text = { 
                                        Column {
                                            Text("${stockItem.cardNo} - ${stockItem.stockDescription}", fontWeight = FontWeight.Bold)
                                            Text("Stor: ${stockItem.storeName}", style = MaterialTheme.typography.bodySmall)
                                        }
                                    },
                                    onClick = {
                                        selectedStockItem = stockItem
                                        stockItemExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Action Buttons
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Google Sign-In Button (if not signed in)
                        if (!isSignedIn) {
                            Button(
                                onClick = {
                                    signInLauncher.launch(googleDocsIntegration.getSignInIntent())
                                },
                                enabled = !isGenerating,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(Icons.Default.OpenInBrowser, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("🔐 Log Masuk ke Google")
                            }
                        }
                        
                        // Auto-populate Button (if signed in and item selected)
                        if (isSignedIn && selectedStockItem != null) {
                            Button(
                                onClick = {
                                    selectedStockItem?.let { stockItem ->
                                        scope.launch {
                                            isGenerating = true
                                            shareMessage = "Mengisi dokumen Google Docs secara automatik..."
                                            
                                            val itemTransactions = transactions.filter { it.stockItemId == stockItem.id }
                                            val result = googleDocsIntegration.populateDocument(stockItem, itemTransactions)
                                            
                                            result.fold(
                                                onSuccess = { message ->
                                                    copiedContent = googleDocsIntegration.getLastGeneratedContent()
                                                    showCopiedContent = true
                                                    shareMessage = "✅ $message"
                                                },
                                                onFailure = { error ->
                                                    shareMessage = "❌ Ralat: ${error.message}\n\nSila cuba lagi atau pastikan anda telah log masuk ke Google."
                                                }
                                            )
                                            
                                            isGenerating = false
                                        }
                                    }
                                },
                                enabled = !isGenerating,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                if (isGenerating) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Mengisi...")
                                } else {
                                    Icon(Icons.Default.OpenInBrowser, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("✨ Isi Dokumen Automatik")
                                }
                            }
                        }
                        
                        // Create New Document Button
                        if (isSignedIn) {
                            Button(
                                onClick = {
                                    selectedStockItem?.let { stockItem ->
                                        scope.launch {
                                            isGenerating = true
                                            shareMessage = "Membuat dokumen KEW.PS-3 baru..."
                                            
                                            val itemTransactions = transactions.filter { it.stockItemId == stockItem.id }
                                            val result = googleDocsIntegration.createNewDocument(stockItem, itemTransactions)
                                            
                                            result.fold(
                                                onSuccess = { message ->
                                                    copiedContent = googleDocsIntegration.getLastGeneratedContent()
                                                    shareMessage = "✅ $message"
                                                },
                                                onFailure = { error ->
                                                    shareMessage = "❌ Ralat: ${error.message}\n\nSila cuba lagi atau pastikan anda telah log masuk ke Google."
                                                }
                                            )
                                            
                                            isGenerating = false
                                        }
                                    }
                                },
                                enabled = !isGenerating,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.tertiary
                                )
                            ) {
                                if (isGenerating) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Membuat...")
                                } else {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("📄 Buat Dokumen Baru")
                                }
                            }
                        }
                        
                        // Open Google Docs Button
                        Button(
                            onClick = {
                                scope.launch {
                                    isGenerating = true
                                    shareMessage = "Membuka Google Docs..."
                                    
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(googleDocsUrl))
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        context.startActivity(intent)
                                        shareMessage = "Google Docs telah dibuka. ${if (isSignedIn) "Gunakan butang 'Isi Dokumen Automatik' untuk mengisi data secara automatik." else "Log masuk terlebih dahulu untuk mengisi data secara automatik."}"
                                    } catch (e: Exception) {
                                        shareMessage = "Ralat membuka Google Docs: ${e.message}"
                                    }
                                    
                                    isGenerating = false
                                }
                            },
                            enabled = !isGenerating,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            if (isGenerating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Membuka...")
                            } else {
                                Icon(Icons.Default.OpenInBrowser, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("🌐 Buka Google Docs")
                            }
                        }


                    }

                    // Status message
                    shareMessage?.let { message ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (message.contains("Ralat")) 
                                    MaterialTheme.colorScheme.errorContainer 
                                else 
                                    MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Text(
                                text = message,
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = if (message.contains("Ralat")) 
                                    MaterialTheme.colorScheme.onErrorContainer 
                                else 
                                    MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }

        // Copied Content Preview (if content has been copied)
        if (showCopiedContent && copiedContent != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📋 KANDUNGAN YANG DISALIN",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            IconButton(
                                onClick = { showCopiedContent = false }
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Tutup",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                        
                        Text(
                            text = "Kandungan ini telah disalin ke clipboard. Tampal dalam Google Docs untuk mengisi dokumen:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Text(
                                text = copiedContent!!.take(500) + if (copiedContent!!.length > 500) "\n\n... (dan banyak lagi)" else "",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(googleDocsUrl))
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.OpenInBrowser, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Buka Google Docs")
                            }
                            
                            Button(
                                onClick = {
                                    // Copy to clipboard again
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                    val clip = android.content.ClipData.newPlainText("KEW.PS-3", copiedContent!!)
                                    clipboard.setPrimaryClip(clip)
                                    shareMessage = "📋 Data disalin semula ke clipboard!"
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                )
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Salin Semula")
                            }
                        }
                    }
                }
            }
        }

        // Data Preview Card (if item selected but no content copied yet)
        if (!showCopiedContent) {
            selectedStockItem?.let { stockItem ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Modern title with gradient-like background
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "📊 Pratonton Data",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                )
                            }
                            
                            val itemTransactions = transactions.filter { it.stockItemId == stockItem.id }
                            
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = generatePreviewText(stockItem, itemTransactions),
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Instructions Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Modern title with gradient-like background
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "📋 Arahan Penggunaan",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                    
                    // Content card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                    
                    Text(
                        text = "1. Log masuk ke Google menggunakan butang 'Log Masuk ke Google'",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "2. Pilih item stok yang ingin anda dokumentasikan",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "3. Tekan 'Isi Dokumen Automatik' - sistem akan buka Google Docs dan salin data",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "4. Dalam Google Docs: Tekan Ctrl+A (pilih semua) kemudian Ctrl+V (tampal)",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "5. Dokumen KEW.PS-3 akan terisi dengan format yang sempurna!",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                            Text(
                                text = "✨ Kelebihan Smart Copy-Paste ke Google Docs:",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            Text(
                                text = "• Data disalin ke clipboard secara automatik - hanya perlu tampal",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "• Format KEW.PS-3 rasmi dengan jadual cantik dan suku tahunan",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "• Google Docs dibuka automatik ke dokumen yang betul",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "• Simpan automatik ke cloud Google Drive",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "• Kolaborasi masa nyata dengan rakan sekerja",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "• Boleh dieksport ke PDF atau Word jika diperlukan",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}



private fun generatePreviewText(stockItem: StockItem, transactions: List<Transaction>): String {
    val sb = StringBuilder()
    
    // Basic Information
    sb.appendLine("📋 MAKLUMAT ASAS")
    sb.appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
    sb.appendLine("No. Kad        : ${stockItem.cardNo}")
    sb.appendLine("Perihal Stok   : ${stockItem.stockDescription}")
    sb.appendLine("Kod Barang     : ${stockItem.codeNo}")
    sb.appendLine("Unit Ukuran    : ${stockItem.unitMeasurement}")
    sb.appendLine("Stor/Jabatan   : ${stockItem.storeName}")
    sb.appendLine("Kumpulan       : ${stockItem.group}")
    sb.appendLine("Status         : ${stockItem.movement}")
    sb.appendLine()
    
    // Location Information
    sb.appendLine("📍 LOKASI PENYIMPANAN")
    sb.appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
    sb.appendLine("Gudang         : ${stockItem.warehouse}")
    sb.appendLine("Baris          : ${stockItem.row}")
    sb.appendLine("Rak            : ${stockItem.rack}")
    sb.appendLine("Aras           : ${stockItem.level}")
    sb.appendLine("Petak          : ${stockItem.compartment}")
    sb.appendLine()
    
    // Stock Levels
    sb.appendLine("📊 TAHAP STOK")
    sb.appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
    sb.appendLine("Stok Semasa    : ${stockItem.currentBalance} ${stockItem.unitMeasurement}")
    sb.appendLine("Stok Maksimum  : ${stockItem.maxStock} ${stockItem.unitMeasurement}")
    sb.appendLine("Tahap Reorder  : ${stockItem.reorderStock} ${stockItem.unitMeasurement}")
    sb.appendLine("Stok Minimum   : ${stockItem.minStock} ${stockItem.unitMeasurement}")
    
    // Stock Movement Summary
    sb.appendLine("Jumlah Diterima: ${stockItem.totalReceived} ${stockItem.unitMeasurement}")
    sb.appendLine("Jumlah Keluaran: ${stockItem.totalIssued} ${stockItem.unitMeasurement}")
    sb.appendLine()
    
    // Transaction Analysis
    if (transactions.isNotEmpty()) {
        sb.appendLine("💼 ANALISA TRANSAKSI")
        sb.appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        sb.appendLine("Jumlah Rekod   : ${transactions.size} transaksi")
        
        val receiptTxns = transactions.filter { it.type == "terimaan" }
        val issueTxns = transactions.filter { it.type == "keluaran" }
        
        sb.appendLine("Terimaan       : ${receiptTxns.size} rekod")
        sb.appendLine("Keluaran       : ${issueTxns.size} rekod")
        
        val totalValue = transactions.sumOf { it.totalPrice }
        val totalReceiptValue = receiptTxns.sumOf { it.totalPrice }
        val totalIssueValue = issueTxns.sumOf { it.totalPrice }
        
        sb.appendLine("Nilai Keseluruhan: RM ${String.format("%.2f", totalValue)}")
        sb.appendLine("Nilai Terimaan : RM ${String.format("%.2f", totalReceiptValue)}")
        sb.appendLine("Nilai Keluaran : RM ${String.format("%.2f", totalIssueValue)}")
        
        // Recent transactions preview
        if (transactions.isNotEmpty()) {
            sb.appendLine()
            sb.appendLine("📅 TRANSAKSI TERKINI (5 Terbaru)")
            sb.appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            val recentTransactions = transactions.sortedByDescending { it.timestamp }.take(5)
            recentTransactions.forEach { txn ->
                val date = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date(txn.date))
                val typeEmoji = if (txn.type == "terimaan") "📥" else "📤"
                sb.appendLine("$typeEmoji $date | ${txn.type.uppercase()} | ${txn.quantity} unit | RM ${String.format("%.2f", txn.totalPrice)}")
            }
        }
    } else {
        sb.appendLine("💼 ANALISA TRANSAKSI")
        sb.appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        sb.appendLine("Belum ada transaksi direkodkan untuk item ini.")
    }
    
    return sb.toString()
}