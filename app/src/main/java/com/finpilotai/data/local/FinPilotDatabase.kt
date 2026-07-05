package com.finpilotai.data.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.finpilotai.data.local.dao.ExpenseDao
import com.finpilotai.data.local.entity.ExpenseEntity

@Database(
    entities = [ExpenseEntity::class],
    version = 2,
    exportSchema = false
)
abstract class FinPilotDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
}