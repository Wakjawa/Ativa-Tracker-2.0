package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expense_subjects ORDER BY orderIndex ASC")
    fun getAllExpensesFlow(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expense_subjects ORDER BY orderIndex ASC")
    suspend fun getAllExpenses(): List<ExpenseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(expenses: List<ExpenseEntity>)

    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("DELETE FROM expense_subjects WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE expense_subjects SET isEnabled = :isEnabled WHERE id = :id")
    suspend fun updateEnabled(id: String, isEnabled: Boolean)

    @Query("UPDATE expense_subjects SET fundAccountId = :accountId WHERE id = :expenseId")
    suspend fun updateFundAccount(expenseId: String, accountId: String?)

    @Query("UPDATE expense_subjects SET fundAccountId = null WHERE fundAccountId = :accountId")
    suspend fun unassignAllForAccount(accountId: String)

    @Query("SELECT COUNT(*) FROM expense_subjects")
    suspend fun count(): Int
}
