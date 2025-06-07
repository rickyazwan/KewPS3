package com.ram.kewps_3.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stock_items")
data class StockItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cardNo: String,
    val storeName: String,
    val stockDescription: String,
    val codeNo: String,
    val unitMeasurement: String,
    val group: String,
    val movement: String,
    val warehouse: String,
    val row: String,
    val rack: String,
    val level: String,
    val compartment: String,
    val maxStock: Int,
    val reorderStock: Int,
    val minStock: Int,
    val currentBalance: Int = 0,
    val totalReceived: Int = 0,
    val totalIssued: Int = 0,
    val dateAdded: Long = System.currentTimeMillis()
) 