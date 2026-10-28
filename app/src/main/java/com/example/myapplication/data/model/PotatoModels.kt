package com.example.myapplication.data.model

data class CookingGoal(
    val id: String,
    val title: String,
    val icon: String,
    val defaultRecipe: String
)

data class PotatoVariety(
    val id: String,
    val name: String,
    val culinaryType: String,
    val starchPercentage: Int,
    val fleshColor: String,
    val dranikScore: Int,
    val description: String,
    val country: String = "Беларусь",
    val yield: String = "до 55 т/га",
    val boilability: String = "Среднеразвариваемый",
    val diseaseResistance: String = "Высокая (устойчив к фитофторозу)",
    val bestDishes: String = "Драники, Пюре, Клецки",
    val isFavorite: Boolean = false
)

data class ScanResult(
    val id: String,
    val timestamp: Long,
    val varietyName: String,
    val culinaryType: String,
    val starchPercentage: Int,
    val fleshColor: String,
    val accuracyPercentage: Int,
    val score: Int,
    val verdictTitle: String,
    val verdictText: String,
    val warningMessage: String? = null,
    val userNote: String? = null,
    val isFavorite: Boolean = false,
    val isGeometryOk: Boolean = true,
    val isEyeDepthOk: Boolean = true
)

data class UserProfile(
    val name: String = "Картофельный эксперт",
    val email: String = "expert@bulbascan.by",
    val isLoggedIn: Boolean = false,
    val achievementsCount: Int = 6,
    val totalScansCount: Int = 0
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val isUnlocked: Boolean = false,
    val currentProgress: Int = 0,
    val targetProgress: Int = 1
)

data class ScanAnalytics(
    val totalScans: Int = 0,
    val dranikiCount: Int = 0,
    val pureeCount: Int = 0,
    val fryCount: Int = 0,
    val averageStarch: Double = 0.0,
    val topVariety: String = "Манифест"
)
