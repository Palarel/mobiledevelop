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
    val description: String
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
    val isGeometryOk: Boolean = true,
    val isEyeDepthOk: Boolean = true
)

data class UserProfile(
    val name: String = "Картофельный эксперт",
    val email: String = "expert@bulbascan.by",
    val achievementsCount: Int = 6,
    val totalScansCount: Int = 12
)

data class Achievement(
    val id: String,
    val title: String,
    val icon: String,
    val isUnlocked: Boolean = true
)
