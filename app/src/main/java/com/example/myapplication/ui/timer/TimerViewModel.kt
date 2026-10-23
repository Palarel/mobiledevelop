package com.example.myapplication.ui.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TimerUiState(
    val remainingSeconds: Int = 240,
    val isRunning: Boolean = false,
    val formattedTime: String = "04:00",
    val title: String = "Жарка 1 стороны"
)

class TimerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    fun toggleTimer() {
        if (_uiState.value.isRunning) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        if (_uiState.value.remainingSeconds <= 0) {
            _uiState.value = _uiState.value.copy(remainingSeconds = 240, formattedTime = formatTime(240))
        }

        _uiState.value = _uiState.value.copy(isRunning = true)

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.remainingSeconds > 0 && _uiState.value.isRunning) {
                delay(1000)
                val newSeconds = _uiState.value.remainingSeconds - 1
                _uiState.value = _uiState.value.copy(
                    remainingSeconds = newSeconds,
                    formattedTime = formatTime(newSeconds)
                )
            }
            if (_uiState.value.remainingSeconds <= 0) {
                _uiState.value = _uiState.value.copy(isRunning = false)
            }
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(isRunning = false)
    }

    fun resetTimer() {
        timerJob?.cancel()
        _uiState.value = TimerUiState(
            remainingSeconds = 240,
            isRunning = false,
            formattedTime = formatTime(240)
        )
    }

    private fun formatTime(sec: Int): String {
        val m = (sec / 60).toString().padStart(2, '0')
        val s = (sec % 60).toString().padStart(2, '0')
        return "$m:$s"
    }
}
