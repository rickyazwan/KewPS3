package com.ram.kewps_3.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StockItemDao {
    @Query("SELECT * FROM stock_items ORDER BY cardNo ASC")
    fun getAllStockItems(): Flow<List<StockItem>>
    
    @Query("SELECT * FROM stock_items WHERE id = :id")
    suspend fun getStockItemById(id: Long): StockItem?
    
    @Query("SELECT COUNT(*) FROM stock_items WHERE currentBalance <= minStock")
    suspend fun getLowStockCount(): Int
    
    @Query("SELECT COUNT(*) FROM stock_items")
    suspend fun getTotalItemsCount(): Int
    
    @Insert
    suspend fun insertStockItem(stockItem: StockItem): Long
    
    @Update
    suspend fun updateStockItem(stockItem: StockItem)
    
    @Delete
    suspend fun deleteStockItem(stockItem: StockItem)
    
    @Query("SELECT MAX(CAST(cardNo AS INTEGER)) FROM stock_items")
    suspend fun getLastCardNumber(): Int?
} 