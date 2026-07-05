package com.finpilotai.di

import com.finpilotai.data.repository.AuthRepositoryImpl
import com.finpilotai.data.repository.ExpenseRepositoryImpl
import com.finpilotai.data.repository.InsightsRepositoryImpl
import com.finpilotai.data.repository.ReceiptRepositoryImpl
import com.finpilotai.data.repository.UserPrefsRepositoryImpl
import com.finpilotai.domain.repository.AuthRepository
import com.finpilotai.domain.repository.ExpenseRepository
import com.finpilotai.domain.repository.InsightsRepository
import com.finpilotai.domain.repository.ReceiptRepository
import com.finpilotai.domain.repository.UserPrefsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds @Singleton
    abstract fun bindUserPrefsRepository(impl: UserPrefsRepositoryImpl): UserPrefsRepository

    @Binds @Singleton
    abstract fun bindExpenseRepository(impl: ExpenseRepositoryImpl): ExpenseRepository

    @Binds @Singleton
    abstract fun bindReceiptRepository(impl: ReceiptRepositoryImpl): ReceiptRepository

    @Binds @Singleton
    abstract fun bindInsightsRepository(impl: InsightsRepositoryImpl): InsightsRepository
}