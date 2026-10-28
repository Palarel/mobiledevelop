package com.example.myapplication.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.Achievement
import com.example.myapplication.data.model.Recipe
import com.example.myapplication.data.model.ScanResult
import com.example.myapplication.data.model.UserProfile
import com.example.myapplication.data.repository.PotatoRepository
import com.example.myapplication.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class ProfileUiState(
    val userProfile: UserProfile = UserProfile(),
    val achievements: List<Achievement> = emptyList(),
    val scanHistory: List<ScanResult> = emptyList(),
    val recipes: List<Recipe> = emptyList(),
    val isDarkTheme: Boolean = true
)

class ProfileViewModel(
    private val repository: PotatoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<ProfileUiState>>(UiState.Loading)
    val uiState: StateFlow<UiState<ProfileUiState>> = _uiState.asStateFlow()

    init {
        loadProfileData()
    }

    fun loadProfileData() {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                combine(
                    repository.getUserProfile(),
                    repository.getScanHistory(),
                    repository.isDarkTheme
                ) { profile, history, isDark ->
                    ProfileUiState(
                        userProfile = profile.copy(totalScansCount = history.size),
                        achievements = repository.getAchievements(),
                        scanHistory = history,
                        recipes = repository.getRecipes(),
                        isDarkTheme = isDark
                    )
                }.collect { state ->
                    _uiState.value = UiState.Success(state)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Ошибка загрузки профиля")
            }
        }
    }

    fun updateName(newName: String) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is UiState.Success) {
                val currentProfile = currentState.data.userProfile
                repository.updateUserProfile(newName, currentProfile.email)
            }
        }
    }

    fun deleteScanHistoryItem(id: String) {
        viewModelScope.launch {
            repository.deleteScanResult(id)
        }
    }

    fun toggleTheme(isDark: Boolean) {
        viewModelScope.launch {
            repository.setDarkTheme(isDark)
        }
    }
}
