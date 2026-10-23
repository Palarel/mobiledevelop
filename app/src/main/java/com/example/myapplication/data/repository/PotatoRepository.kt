package com.example.myapplication.data.repository

import com.example.myapplication.data.model.Achievement
import com.example.myapplication.data.model.CookingGoal
import com.example.myapplication.data.model.PotatoVariety
import com.example.myapplication.data.model.ScanResult
import com.example.myapplication.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface PotatoRepository {
    fun getCookingGoals(): List<CookingGoal>
    fun getPotatoVarieties(): List<PotatoVariety>
    fun getScanHistory(): Flow<List<ScanResult>>
    suspend fun saveScanResult(result: ScanResult)
    fun getUserProfile(): Flow<UserProfile>
    suspend fun updateUserProfile(name: String, email: String)
    fun getAchievements(): List<Achievement>
    fun simulateAnalysis(selectedGoal: String): Flow<Int>
    fun generateScanVerdict(selectedGoal: String): ScanResult
}
