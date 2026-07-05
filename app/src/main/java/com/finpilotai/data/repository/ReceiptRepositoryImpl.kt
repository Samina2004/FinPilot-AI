package com.finpilotai.data.repository

import com.finpilotai.data.local.ocr.TextRecognitionHelper
import com.finpilotai.data.remote.gemini.GeminiReceiptParser
import com.finpilotai.domain.model.ReceiptScanResult
import com.finpilotai.domain.repository.ReceiptRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReceiptRepositoryImpl @Inject constructor(
    private val textRecognitionHelper: TextRecognitionHelper,
    private val geminiReceiptParser: GeminiReceiptParser
) : ReceiptRepository {

    override suspend fun extractTextFromImage(imagePath: String): Result<String> =
        textRecognitionHelper.recognizeText(imagePath)

    override suspend fun parseReceiptText(rawText: String): Result<ReceiptScanResult> {
        val parsedResult = geminiReceiptParser.parseReceiptText(rawText)

        return parsedResult.fold(
            onSuccess = { parsed ->
                Result.success(
                    ReceiptScanResult(
                        merchant = parsed.merchant,
                        amount   = parsed.amount,
                        category = parsed.category,
                        rawText  = rawText
                    )
                )
            },
            onFailure = { Result.failure(it) }
        )
    }
}