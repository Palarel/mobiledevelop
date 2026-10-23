package com.example.myapplication.di

import android.content.Context
import com.example.myapplication.data.local.datastore.UserPreferencesRepository
import com.example.myapplication.data.repository.PotatoRepository
import com.example.myapplication.data.repository.PotatoRepositoryImpl

class AppContainer(context: Context) {
    val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context.applicationContext)
    }

    val potatoRepository: PotatoRepository by lazy {
        PotatoRepositoryImpl(userPreferencesRepository)
    }
}
