package com.ram.kewps_3.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ram.kewps_3.data.*
import com.ram.kewps_3.repository.KewPS3Repository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class KewPS3ViewModel(application: Application) : AndroidViewModel(application) {
    private val database = KewPS3Database.getDatabase(application)
    private val repository = KewPS3Repository(database.stockItemDao(), database.transactionDao())
    
    // Flows for UI data
    val stockItems: Flow<List<StockItem>> = repository.getAllStockItems()
    val transactions: Flow<List<Transaction>> = repository.getAllTransactions()
    
    // Dashboard statistics
    private val _dashboardStats = MutableStateFlow(DashboardStats())
    val dashboardStats: StateFlow<DashboardStats> = _dashboardStats.asStateFlow()
    
    // UI state
    private val _uiState = MutableStateFlow(KewPS3UiState())
    val uiState: StateFlow<KewPS3UiState> = _uiState.asStateFlow()
    
    init {
        updateDashboardStats()
    }
    
    // Stock Item operations
    fun addStockItem(
        storeName: String,
        stockDescription: String,
        codeNo: String,
        unitMeasurement: String,
        group: String,
        movement: String,
        warehouse: String,
        row: String,
        rack: String,
        level: String,
        compartment: String,
        maxStock: Int,
        reorderStock: Int,
        minStock: Int,
        initialStock: Int = 0
    ) {
        viewModelScope.launch {
            try {
                val nextCardNumber = (repository.getLastCardNumber() + 1).toString().padStart(3, '0')
                
                // Auto-generate intelligent default values if not provided
                val intelligentMaxStock = if (maxStock <= 0) generateIntelligentMaxStock(unitMeasurement, group) else maxStock
                val intelligentReorderStock = if (reorderStock <= 0) (intelligentMaxStock * 0.3).toInt().coerceAtLeast(10) else reorderStock
                val intelligentMinStock = if (minStock <= 0) (intelligentReorderStock * 0.5).toInt().coerceAtLeast(5) else minStock
                
                val stockItem = StockItem(
                    cardNo = nextCardNumber,
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
                    maxStock = intelligentMaxStock,
                    reorderStock = intelligentReorderStock,
                    minStock = intelligentMinStock,
                    currentBalance = initialStock,
                    totalReceived = initialStock
                )
                
                repository.insertStockItem(stockItem)
                updateDashboardStats()
                showMessage("Item berjaya ditambah! Nilai stok auto-ditetapkan secara bijak.")
            } catch (e: Exception) {
                showMessage("Ralat menambah item: ${e.message}")
            }
        }
    }
    
    /**
     * Generate intelligent maximum stock based on unit measurement and group
     */
    private fun generateIntelligentMaxStock(unitMeasurement: String, group: String): Int {
        return when (unitMeasurement.lowercase()) {
            "buah", "unit" -> when (group) {
                "A" -> 500 // High usage items
                "B" -> 200 // Medium usage items
                else -> 100
            }
            "kotak", "rim" -> when (group) {
                "A" -> 100
                "B" -> 50
                else -> 25
            }
            "kg", "liter" -> when (group) {
                "A" -> 1000
                "B" -> 500
                else -> 200
            }
            "batang", "bilah" -> when (group) {
                "A" -> 200
                "B" -> 100
                else -> 50
            }
            else -> when (group) {
                "A" -> 300
                "B" -> 150
                else -> 75
            }
        }
    }
    
    fun processTransaction(
        date: Long,
        documentType: String,
        documentNo: String,
        stockItemId: Long,
        stockDescription: String,
        type: String,
        quantity: Int,
        unitPrice: Double,
        receivedFrom: String = "",
        issuedTo: String = "",
        officerName: String
    ) {
        viewModelScope.launch {
            try {
                val transaction = Transaction(
                    date = date,
                    documentType = documentType,
                    documentNo = documentNo,
                    stockItemId = stockItemId,
                    stockDescription = stockDescription,
                    type = type,
                    quantity = quantity,
                    unitPrice = unitPrice,
                    totalPrice = quantity * unitPrice,
                    receivedFrom = receivedFrom,
                    issuedTo = issuedTo,
                    officerName = officerName
                )
                
                val success = repository.processTransaction(transaction)
                if (success) {
                    updateDashboardStats()
                    showMessage("Transaksi berjaya direkod!")
                } else {
                    showMessage("Ralat: Kuantiti keluaran melebihi stok yang ada!")
                }
            } catch (e: Exception) {
                showMessage("Ralat merekod transaksi: ${e.message}")
            }
        }
    }
    
    fun getTransactionsByStockItem(stockItemId: Long): Flow<List<Transaction>> {
        return repository.getTransactionsByStockItem(stockItemId)
    }
    
    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                repository.updateTransaction(transaction)
                updateDashboardStats()
                showMessage("Transaksi berjaya dikemas kini!")
            } catch (e: Exception) {
                showMessage("Ralat mengemas kini transaksi: ${e.message}")
            }
        }
    }
    
    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                // Get the associated stock item and reverse the transaction effect
                val stockItem = repository.getStockItemById(transaction.stockItemId)
                stockItem?.let { item ->
                    val updatedStockItem = if (transaction.type == "terimaan") {
                        // Reverse receipt: subtract from balance and totalReceived
                        item.copy(
                            currentBalance = item.currentBalance - transaction.quantity,
                            totalReceived = item.totalReceived - transaction.quantity
                        )
                    } else {
                        // Reverse issue: add back to balance and subtract from totalIssued
                        item.copy(
                            currentBalance = item.currentBalance + transaction.quantity,
                            totalIssued = item.totalIssued - transaction.quantity
                        )
                    }
                    
                    repository.updateStockItem(updatedStockItem)
                }
                
                repository.deleteTransaction(transaction)
                updateDashboardStats()
                showMessage("Transaksi berjaya dipadam!")
            } catch (e: Exception) {
                showMessage("Ralat memadam transaksi: ${e.message}")
            }
        }
    }
    
    fun updateStockItem(stockItem: StockItem) {
        viewModelScope.launch {
            try {
                repository.updateStockItem(stockItem)
                updateDashboardStats()
                showMessage("Item berjaya dikemas kini!")
            } catch (e: Exception) {
                showMessage("Ralat mengemas kini item: ${e.message}")
            }
        }
    }
    
    fun deleteStockItem(stockItem: StockItem) {
        viewModelScope.launch {
            try {
                repository.deleteStockItem(stockItem)
                updateDashboardStats()
                showMessage("Item berjaya dipadam!")
            } catch (e: Exception) {
                showMessage("Ralat memadam item: ${e.message}")
            }
        }
    }
    
    private fun updateDashboardStats() {
        viewModelScope.launch {
            try {
                val totalItems = repository.getTotalItemsCount()
                val totalReceipts = repository.getTotalReceiptTransactions()
                val totalIssues = repository.getTotalIssueTransactions()
                val lowStock = repository.getLowStockCount()
                
                _dashboardStats.value = DashboardStats(
                    totalItems = totalItems,
                    totalReceipts = totalReceipts,
                    totalIssues = totalIssues,
                    lowStock = lowStock
                )
            } catch (e: Exception) {
                showMessage("Ralat mengemas kini statistik: ${e.message}")
            }
        }
    }
    
    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
    
    private fun showMessage(message: String) {
        _uiState.value = _uiState.value.copy(message = message)
    }
    
    fun setSelectedTab(tab: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }
}

data class DashboardStats(
    val totalItems: Int = 0,
    val totalReceipts: Int = 0,
    val totalIssues: Int = 0,
    val lowStock: Int = 0
)

data class KewPS3UiState(
    val selectedTab: Int = 0,
    val message: String? = null
) 