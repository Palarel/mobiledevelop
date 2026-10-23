package com.example.myapplication.ui.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.ScanResult
import com.example.myapplication.data.repository.PotatoRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AnalysisUiState(
    val progressPercentage: Int = 0,
    val isComplete: Boolean = false,
    val statusText: String = "Нейросеть рассчитывает крахмал...",
    val scanVerdict: ScanResult? = null,
    val selectedGoal: String = "Драники"
)

class AnalysisViewModel(
    private val repository: PotatoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalysisUiState())
    val uiState: StateFlow<AnalysisUiState> = _uiState.asStateFlow()

    private var analysisJob: Job? = null

    fun startAnalysis(selectedGoal: String = "Драники") {
        analysisJob?.cancel()
        _uiState.value = AnalysisUiState(progressPercentage = 0, selectedGoal = selectedGoal)

        analysisJob = viewModelScope.launch {
            repository.simulateAnalysis(selectedGoal).collect { percent ->
                val isComplete = percent >= 100
                val verdict = if (isComplete) repository.generateScanVerdict(selectedGoal) else null

                if (verdict != null) {
                    repository.saveScanResult(verdict)
                }

                _uiState.value = _uiState.value.copy(
                    progressPercentage = percent,
                    isComplete = isComplete,
                    scanVerdict = verdict,
                    statusText = if (isComplete) "Анализ завершен!" else "Нейросеть рассчитывает крахмал..."
                )
            }
        }
    }

    fun cancelAnalysis() {
        analysisJob?.cancel()
        _uiState.value = AnalysisUiState(progressPercentage = 0, isComplete = false)
    }
}
