package com.example.myapplication.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myapplication.BulbaApplication
import com.example.myapplication.ui.analysis.AnalysisViewModel
import com.example.myapplication.ui.home.HomeViewModel
import com.example.myapplication.ui.profile.ProfileViewModel
import com.example.myapplication.ui.timer.TimerViewModel

object AppViewModelProvider {
    val Factory: ViewModelProvider.Factory = viewModelFactory {
        initializer {
            HomeViewModel(bulbaApplication().container.potatoRepository)
        }
        initializer {
            AnalysisViewModel(bulbaApplication().container.potatoRepository)
        }
        initializer {
            TimerViewModel()
        }
        initializer {
            ProfileViewModel(bulbaApplication().container.potatoRepository)
        }
    }
}

fun CreationExtras.bulbaApplication(): BulbaApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BulbaApplication)
