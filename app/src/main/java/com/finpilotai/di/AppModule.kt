package com.finpilotai.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.finpilotai.data.local.FinPilotDatabase
import com.finpilotai.data.local.dao.ExpenseDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "finpilot_prefs")

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE expenses ADD COLUMN imagePath TEXT")
    }
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.dataStore

    @Provides @Singleton
    fun provideFinPilotDatabase(@ApplicationContext context: Context): FinPilotDatabase =
        Room.databaseBuilder(
            context,
            FinPilotDatabase::class.java,
            "finpilot_db"
        )
            .addMigrations(MIGRATION_1_2)
            .build()

    @Provides @Singleton
    fun provideExpenseDao(database: FinPilotDatabase): ExpenseDao =
        database.expenseDao()
}