package com.finpilotai.domain.repository

import com.finpilotai.domain.model.ReceiptScanResult

interface ReceiptRepository {
    suspend fun extractTextFromImage(imagePath: String): Result<String>
    suspend fun parseReceiptText(rawText: String): Result<ReceiptScanResult>
}