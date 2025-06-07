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
        minStock: Int
    ) {
        viewModelScope.launch {
            try {
                val nextCardNumber = (repository.getLastCardNumber() + 1).toString().padStart(3, '0')
                
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
                    maxStock = maxStock,
                    reorderStock = reorderStock,
                    minStock = minStock
                )
                
                repository.insertStockItem(stockItem)
                updateDashboardStats()
                showMessage("Item berjaya ditambah!")
            } catch (e: Exception) {
                showMessage("Ralat menambah item: ${e.message}")
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