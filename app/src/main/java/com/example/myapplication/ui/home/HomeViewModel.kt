package com.example.myapplication.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.CookingGoal
import com.example.myapplication.data.repository.PotatoRepository
import com.example.myapplication.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val selectedRecipe: String = "Драники",
    val recipes: List<CookingGoal> = emptyList(),
    val isAiCalibrated: Boolean = true
)

class HomeViewModel(
    private val repository: PotatoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<HomeUiState>>(UiState.Loading)
    val uiState: StateFlow<UiState<HomeUiState>> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                val goals = repository.getCookingGoals()
                _uiState.value = UiState.Success(
                    HomeUiState(
                        selectedRecipe = "Драники",
                        recipes = goals,
                        isAiCalibrated = true
                    )
                )
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Ошибка загрузки данных")
            }
        }
    }

    fun selectRecipe(recipeTitle: String) {
        val currentState = _uiState.value
        if (currentState is UiState.Success) {
            _uiState.value = UiState.Success(
                currentState.data.copy(selectedRecipe = recipeTitle)
            )
        }
    }
}
