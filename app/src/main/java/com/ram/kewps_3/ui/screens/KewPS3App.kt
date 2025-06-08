package com.ram.kewps_3.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ram.kewps_3.viewmodel.KewPS3ViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KewPS3App(
    modifier: Modifier = Modifier,
    viewModel: KewPS3ViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Show snackbar for messages
    uiState.message?.let { message ->
        LaunchedEffect(message) {
            // Clear message after showing
            viewModel.clearMessage()
        }
    }
    
    Column(modifier = modifier.fillMaxSize()) {
        // Header
        CenterAlignedTopAppBar(
            title = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "KEW.PS-3 Sistem Pengurusan Stok",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Pekeliling Perbendaharaan Malaysia AM 6.3",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )
        
        // Content based on selected tab
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (uiState.selectedTab) {
                0 -> DashboardScreen(viewModel = viewModel)
                1 -> AddItemScreen(viewModel = viewModel)
                2 -> TransactionsScreen(viewModel = viewModel)
                3 -> ExportScreen(viewModel = viewModel)
            }
        }
        
        // Bottom Navigation
        NavigationBar {
            BottomNavItem.entries.forEachIndexed { index, item ->
                NavigationBarItem(
                    icon = { Icon(item.icon, contentDescription = item.title) },
                    label = { Text(item.title) },
                    selected = uiState.selectedTab == index,
                    onClick = { viewModel.setSelectedTab(index) }
                )
            }
        }
    }
    
    // Show message snackbar
    uiState.message?.let { message ->
        LaunchedEffect(message) {
            // You can add SnackbarHost here if needed
        }
    }
}

enum class BottomNavItem(val title: String, val icon: ImageVector) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard),
    ADD_ITEM("Add Item", Icons.Default.Add),
    TRANSACTIONS("Transactions", Icons.Default.Receipt),
    EXPORT("Documents", Icons.Default.Share)
} 