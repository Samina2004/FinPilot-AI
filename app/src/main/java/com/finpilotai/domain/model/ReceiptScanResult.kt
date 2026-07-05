package com.finpilotai.domain.model

data class ReceiptScanResult(
    val merchant: String,
    val amount: Double,
    val category: String,
    val date: Long = System.currentTimeMillis(),
    val rawText: String = ""
)