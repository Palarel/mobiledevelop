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
    val initialSeconds: Int = 240,
    val remainingSeconds: Int = 240,
    val isRunning: Boolean = false,
    val isFinished: Boolean = false,
    val formattedTime: String = "04:00",
    val title: String = "Жарка 1 стороны"
)

class TimerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    fun setGoalTimer(goalName: String) {
        val (seconds, title) = when {
            goalName.contains("Пюре", ignoreCase = true) -> 900 to "Варка до готовности"
            goalName.contains("Жарка", ignoreCase = true) -> 600 to "Жарка на сковороде"
            goalName.contains("Запекание", ignoreCase = true) -> 1200 to "Запекание в духовке"
            else -> 240 to "Жарка 1 стороны"
        }

        timerJob?.cancel()
        _uiState.value = TimerUiState(
            initialSeconds = seconds,
            remainingSeconds = seconds,
            isRunning = false,
            isFinished = false,
            formattedTime = formatTime(seconds),
            title = title
        )
    }

    fun toggleTimer() {
        if (_uiState.value.isRunning) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        if (_uiState.value.remainingSeconds <= 0) {
            val initSec = _uiState.value.initialSeconds
            _uiState.value = _uiState.value.copy(
                remainingSeconds = initSec,
                isFinished = false,
                formattedTime = formatTime(initSec)
            )
        }

        _uiState.value = _uiState.value.copy(isRunning = true, isFinished = false)

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
                _uiState.value = _uiState.value.copy(isRunning = false, isFinished = true)
            }
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(isRunning = false)
    }

    fun resetTimer() {
        timerJob?.cancel()
        val initSec = _uiState.value.initialSeconds
        _uiState.value = _uiState.value.copy(
            remainingSeconds = initSec,
            isRunning = false,
            isFinished = false,
            formattedTime = formatTime(initSec)
        )
    }

    private fun formatTime(sec: Int): String {
        val m = (sec / 60).toString().padStart(2, '0')
        val s = (sec % 60).toString().padStart(2, '0')
        return "$m:$s"
    }
}
