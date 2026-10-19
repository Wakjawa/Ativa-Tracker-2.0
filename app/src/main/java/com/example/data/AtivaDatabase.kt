package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@Database(
    entities = [ExpenseEntity::class, CommitmentSettingsEntity::class, FundAccountEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AtivaDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun settingsDao(): SettingsDao
    abstract fun fundAccountDao(): FundAccountDao

    companion object {
        @Volatile
        private var INSTANCE: AtivaDatabase? = null

        fun getDatabase(context: Context): AtivaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AtivaDatabase::class.java,
                    "ativa_commitment.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        seedDatabase(database)
                    }
                }
            }
        }

        suspend fun seedDatabase(database: AtivaDatabase) {
            val expenseDao = database.expenseDao()
            val settingsDao = database.settingsDao()
            val fundAccountDao = database.fundAccountDao()

            val accountMaybankId = UUID.randomUUID().toString()
            val accountSavingsId = UUID.randomUUID().toString()
            val accountTngId = UUID.randomUUID().toString()

            if (fundAccountDao.count() == 0) {
                val defaultAccounts = listOf(
                    FundAccountEntity(
                        id = accountMaybankId,
                        name = "Maybank MAE (Car Loan)",
                        accountType = FundAccountType.BANK_ACCOUNT.name,
                        colorHex = 0xFFF59E0B, // Maybank Gold
                        notes = "Auto-debit for Ativa hire-purchase monthly installment",
                        collectedAmount = 3000.0,
                        orderIndex = 0
                    ),
                    FundAccountEntity(
                        id = accountSavingsId,
                        name = "Maintenance & Insurance Pocket",
                        accountType = FundAccountType.SAVINGS_POCKET.name,
                        colorHex = 0xFF10B981, // Emerald Green
                        notes = "Dedicated reserve pocket for scheduled servicing, wear & tear, and annual insurance",
                        collectedAmount = 1200.0,
                        orderIndex = 1
                    ),
                    FundAccountEntity(
                        id = accountTngId,
                        name = "Touch 'n Go eWallet",
                        accountType = FundAccountType.EWALLET.name,
                        colorHex = 0xFF0284C7, // TnG Cyan
                        notes = "Road tax renewal, highway tolls & monthly commute expenses",
                        collectedAmount = 300.0,
                        orderIndex = 2
                    )
                )
                fundAccountDao.insertAll(defaultAccounts)
            }

            if (expenseDao.count() == 0) {
                val defaultExpenses = listOf(
                    ExpenseEntity(
                        id = UUID.randomUUID().toString(),
                        title = "Monthly Car Payment",
                        monthlyAmount = 788.0,
                        rawAmount = 788.0,
                        periodType = PeriodType.MONTHLY.name,
                        category = ExpenseCategory.CAR_PAYMENT.name,
                        notes = "Perodua Ativa AV 2-Tone (9 yrs loan @ 3.2% interest, 10% downpayment)",
                        isEnabled = true,
                        orderIndex = 0,
                        isDefaultSubject = true,
                        fundAccountId = accountMaybankId
                    ),
                    ExpenseEntity(
                        id = UUID.randomUUID().toString(),
                        title = "Monthly Car Maintenance",
                        monthlyAmount = 150.0,
                        rawAmount = 150.0,
                        periodType = PeriodType.MONTHLY.name,
                        category = ExpenseCategory.MAINTENANCE.name,
                        notes = "Scheduled servicing (every 10k km / 6 mo), tire rotations & wear/tear fund",
                        isEnabled = true,
                        orderIndex = 1,
                        isDefaultSubject = true,
                        fundAccountId = accountSavingsId
                    ),
                    ExpenseEntity(
                        id = UUID.randomUUID().toString(),
                        title = "Monthly Road Tax",
                        monthlyAmount = 10.0,
                        rawAmount = 20.0,
                        periodType = PeriodType.YEARLY.name,
                        category = ExpenseCategory.ROAD_TAX.name,
                        notes = "1.0L Turbo engine road tax in Peninsular Malaysia (RM 20/year + buffer)",
                        isEnabled = true,
                        orderIndex = 2,
                        isDefaultSubject = true,
                        fundAccountId = accountTngId
                    ),
                    ExpenseEntity(
                        id = UUID.randomUUID().toString(),
                        title = "Monthly Insurance",
                        monthlyAmount = 155.0,
                        rawAmount = 1860.0,
                        periodType = PeriodType.YEARLY.name,
                        category = ExpenseCategory.INSURANCE.name,
                        notes = "Comprehensive 1st year insurance (~RM 1,860/year for RM 73,400 sum insured)",
                        isEnabled = true,
                        orderIndex = 3,
                        isDefaultSubject = true,
                        fundAccountId = accountSavingsId
                    ),
                    ExpenseEntity(
                        id = UUID.randomUUID().toString(),
                        title = "Fuel / Petrol (RON95)",
                        monthlyAmount = 180.0,
                        rawAmount = 180.0,
                        periodType = PeriodType.MONTHLY.name,
                        category = ExpenseCategory.FUEL.name,
                        notes = "Estimated ~1,200 km/month at 18.9 km/L fuel efficiency (approx 2 full tanks)",
                        isEnabled = true,
                        orderIndex = 4,
                        isDefaultSubject = false,
                        fundAccountId = accountMaybankId
                    ),
                    ExpenseEntity(
                        id = UUID.randomUUID().toString(),
                        title = "Tolls & Touch 'n Go RFID",
                        monthlyAmount = 60.0,
                        rawAmount = 60.0,
                        periodType = PeriodType.MONTHLY.name,
                        category = ExpenseCategory.TOLL.name,
                        notes = "Daily commute highway tolls & Touch 'n Go eWallet reloads",
                        isEnabled = true,
                        orderIndex = 5,
                        isDefaultSubject = false,
                        fundAccountId = accountTngId
                    )
                )
                expenseDao.insertAll(defaultExpenses)
            }

            if (settingsDao.getSettings() == null) {
                settingsDao.saveSettings(CommitmentSettingsEntity())
            }
        }
    }
}
