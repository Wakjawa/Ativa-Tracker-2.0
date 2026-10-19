package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM commitment_settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<CommitmentSettingsEntity?>

    @Query("SELECT * FROM commitment_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): CommitmentSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: CommitmentSettingsEntity)
}
