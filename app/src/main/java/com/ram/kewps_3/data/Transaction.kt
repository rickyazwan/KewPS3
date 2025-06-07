package com.ram.kewps_3.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Long,
    val documentType: String,
    val documentNo: String,
    val stockItemId: Long,
    val stockDescription: String,
    val type: String, // "terimaan" or "keluaran"
    val quantity: Int,
    val unitPrice: Double,
    val totalPrice: Double,
    val receivedFrom: String = "",
    val issuedTo: String = "",
    val officerName: String,
    val timestamp: Long = System.currentTimeMillis()
) 