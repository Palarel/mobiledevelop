package com.example.myapplication.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PotatoScanDao {

    @Query("SELECT * FROM potato_scans ORDER BY timestamp DESC")
    fun getAllScans(): Flow<List<PotatoScanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: PotatoScanEntity)

    @Query("DELETE FROM potato_scans WHERE id = :id")
    suspend fun deleteScanById(id: String)

    @Query("DELETE FROM potato_scans")
    suspend fun clearAllScans()
}
