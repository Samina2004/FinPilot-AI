package com.finpilotai.presentation.receipt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finpilotai.domain.model.ReceiptScanResult
import com.finpilotai.domain.usecase.receipt.ScanReceiptUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ScanReceiptUiState(
    val imagePath: String? = null,
    val isScanning: Boolean = false,
    val scanResult: ReceiptScanResult? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class ScanReceiptViewModel @Inject constructor(
    private val scanReceiptUseCase: ScanReceiptUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScanReceiptUiState())
    val uiState: StateFlow<ScanReceiptUiState> = _uiState

    fun onImageCaptured(imagePath: String) {
        _uiState.update { it.copy(imagePath = imagePath, scanResult = null, errorMessage = null) }
        scanReceipt(imagePath)
    }

    private fun scanReceipt(imagePath: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isScanning = true, errorMessage = null) }

            scanReceiptUseCase(imagePath).fold(
                onSuccess = { result ->
                    _uiState.update { it.copy(isScanning = false, scanResult = result) }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(isScanning = false, errorMessage = e.message ?: "Failed to scan receipt")
                    }
                }
            )
        }
    }

    fun retryWithNewImage() {
        _uiState.update { ScanReceiptUiState() }
    }

    fun clearError() = _uiState.update { it.copy(errorMessage = null) }
}