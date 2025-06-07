package com.ram.kewps_3.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(
    entities = [StockItem::class, Transaction::class],
    version = 1,
    exportSchema = false
)
abstract class KewPS3Database : RoomDatabase() {
    abstract fun stockItemDao(): StockItemDao
    abstract fun transactionDao(): TransactionDao
    
    companion object {
        @Volatile
        private var INSTANCE: KewPS3Database? = null
        
        fun getDatabase(context: Context): KewPS3Database {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KewPS3Database::class.java,
                    "kew_ps3_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
} 