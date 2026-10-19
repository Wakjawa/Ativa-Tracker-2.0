package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FundAccountDao {
    @Query("SELECT * FROM fund_accounts ORDER BY orderIndex ASC")
    fun getAllAccountsFlow(): Flow<List<FundAccountEntity>>

    @Query("SELECT * FROM fund_accounts ORDER BY orderIndex ASC")
    suspend fun getAllAccounts(): List<FundAccountEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: FundAccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(accounts: List<FundAccountEntity>)

    @Update
    suspend fun updateAccount(account: FundAccountEntity)

    @Query("UPDATE fund_accounts SET collectedAmount = :amount WHERE id = :id")
    suspend fun updateCollectedAmount(id: String, amount: Double)

    @Delete
    suspend fun deleteAccount(account: FundAccountEntity)

    @Query("DELETE FROM fund_accounts WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT COUNT(*) FROM fund_accounts")
    suspend fun count(): Int
}
