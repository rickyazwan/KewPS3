package com.ram.kewps_3.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<Transaction>>
    
    @Query("SELECT * FROM transactions WHERE stockItemId = :stockItemId ORDER BY timestamp DESC")
    fun getTransactionsByStockItem(stockItemId: Long): Flow<List<Transaction>>
    
    @Query("SELECT COUNT(*) FROM transactions WHERE type = 'terimaan'")
    suspend fun getTotalReceiptTransactions(): Int
    
    @Query("SELECT COUNT(*) FROM transactions WHERE type = 'keluaran'")
    suspend fun getTotalIssueTransactions(): Int
    
    @Query("SELECT COUNT(*) FROM transactions")
    suspend fun getTotalTransactions(): Int
    
    @Insert
    suspend fun insertTransaction(transaction: Transaction): Long
    
    @Update
    suspend fun updateTransaction(transaction: Transaction)
    
    @Delete
    suspend fun deleteTransaction(transaction: Transaction)
} 