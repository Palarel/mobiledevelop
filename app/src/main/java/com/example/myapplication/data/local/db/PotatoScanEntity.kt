package com.example.myapplication.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myapplication.data.model.ScanResult

@Entity(tableName = "potato_scans")
data class PotatoScanEntity(
    @PrimaryKey
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
    val warningMessage: String? = null
)

fun PotatoScanEntity.toDomainModel() = ScanResult(
    id = id,
    timestamp = timestamp,
    varietyName = varietyName,
    culinaryType = culinaryType,
    starchPercentage = starchPercentage,
    fleshColor = fleshColor,
    accuracyPercentage = accuracyPercentage,
    score = score,
    verdictTitle = verdictTitle,
    verdictText = verdictText,
    warningMessage = warningMessage
)

fun ScanResult.toEntity() = PotatoScanEntity(
    id = id,
    timestamp = timestamp,
    varietyName = varietyName,
    culinaryType = culinaryType,
    starchPercentage = starchPercentage,
    fleshColor = fleshColor,
    accuracyPercentage = accuracyPercentage,
    score = score,
    verdictTitle = verdictTitle,
    verdictText = verdictText,
    warningMessage = warningMessage
)
