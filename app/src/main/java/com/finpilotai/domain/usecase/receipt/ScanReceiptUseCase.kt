package com.finpilotai.domain.usecase.receipt

import com.finpilotai.domain.model.ReceiptScanResult
import com.finpilotai.domain.repository.ReceiptRepository
import javax.inject.Inject

class ScanReceiptUseCase @Inject constructor(
    private val repository: ReceiptRepository
) {
    suspend operator fun invoke(imagePath: String): Result<ReceiptScanResult> {
        val textResult = repository.extractTextFromImage(imagePath)
        val rawText = textResult.getOrElse {
            return Result.failure(it)
        }

        if (rawText.isBlank()) {
            return Result.failure(Exception("No text found in image. Try a clearer photo."))
        }

        return repository.parseReceiptText(rawText)
    }
}