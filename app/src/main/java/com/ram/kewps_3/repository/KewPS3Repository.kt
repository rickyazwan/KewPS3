package com.ram.kewps_3.repository

import com.ram.kewps_3.data.*
import kotlinx.coroutines.flow.Flow

class KewPS3Repository(
    private val stockItemDao: StockItemDao,
    private val transactionDao: TransactionDao
) {
    // StockItem operations
    fun getAllStockItems(): Flow<List<StockItem>> = stockItemDao.getAllStockItems()
    
    suspend fun getStockItemById(id: Long): StockItem? = stockItemDao.getStockItemById(id)
    
    suspend fun insertStockItem(stockItem: StockItem): Long = stockItemDao.insertStockItem(stockItem)
    
    suspend fun updateStockItem(stockItem: StockItem) = stockItemDao.updateStockItem(stockItem)
    
    suspend fun deleteStockItem(stockItem: StockItem) = stockItemDao.deleteStockItem(stockItem)
    
    suspend fun getLastCardNumber(): Int = stockItemDao.getLastCardNumber() ?: 0
    
    suspend fun getLowStockCount(): Int = stockItemDao.getLowStockCount()
    
    suspend fun getTotalItemsCount(): Int = stockItemDao.getTotalItemsCount()
    
    // Transaction operations
    fun getAllTransactions(): Flow<List<Transaction>> = transactionDao.getAllTransactions()
    
    fun getTransactionsByStockItem(stockItemId: Long): Flow<List<Transaction>> = 
        transactionDao.getTransactionsByStockItem(stockItemId)
    
    suspend fun insertTransaction(transaction: Transaction): Long = transactionDao.insertTransaction(transaction)
    
    suspend fun updateTransaction(transaction: Transaction) = transactionDao.updateTransaction(transaction)
    
    suspend fun deleteTransaction(transaction: Transaction) = transactionDao.deleteTransaction(transaction)
    
    suspend fun getTotalReceiptTransactions(): Int = transactionDao.getTotalReceiptTransactions()
    
    suspend fun getTotalIssueTransactions(): Int = transactionDao.getTotalIssueTransactions()
    
    suspend fun getTotalTransactions(): Int = transactionDao.getTotalTransactions()
    
    // Combined operations
    suspend fun processTransaction(transaction: Transaction): Boolean {
        try {
            val stockItem = getStockItemById(transaction.stockItemId) ?: return false
            
            // Check if issue quantity exceeds available stock
            if (transaction.type == "keluaran" && transaction.quantity > stockItem.currentBalance) {
                return false
            }
            
            // Update stock balances
            val updatedStockItem = if (transaction.type == "terimaan") {
                stockItem.copy(
                    currentBalance = stockItem.currentBalance + transaction.quantity,
                    totalReceived = stockItem.totalReceived + transaction.quantity
                )
            } else {
                stockItem.copy(
                    currentBalance = stockItem.currentBalance - transaction.quantity,
                    totalIssued = stockItem.totalIssued + transaction.quantity
                )
            }
            
            // Save transaction and update stock item
            insertTransaction(transaction)
            updateStockItem(updatedStockItem)
            
            return true
        } catch (e: Exception) {
            return false
        }
    }
} 