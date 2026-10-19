package com.example.data

import kotlinx.coroutines.flow.Flow
import java.util.UUID

class CommitmentRepository(
    private val expenseDao: ExpenseDao,
    private val settingsDao: SettingsDao,
    private val fundAccountDao: FundAccountDao
) {
    val allExpenses: Flow<List<ExpenseEntity>> = expenseDao.getAllExpensesFlow()
    val settings: Flow<CommitmentSettingsEntity?> = settingsDao.getSettingsFlow()
    val allFundAccounts: Flow<List<FundAccountEntity>> = fundAccountDao.getAllAccountsFlow()

    suspend fun ensureSeeded() {
        val accountMaybankId = UUID.randomUUID().toString()
        val accountSavingsId = UUID.randomUUID().toString()
        val accountTngId = UUID.randomUUID().toString()

        if (fundAccountDao.count() == 0) {
            val defaultAccounts = listOf(
                FundAccountEntity(
                    id = accountMaybankId,
                    name = "Maybank MAE (Car Loan)",
                    accountType = FundAccountType.BANK_ACCOUNT.name,
                    colorHex = 0xFFF59E0B,
                    notes = "Auto-debit for Ativa hire-purchase monthly installment",
                    collectedAmount = 3000.0,
                    orderIndex = 0
                ),
                FundAccountEntity(
                    id = accountSavingsId,
                    name = "Maintenance & Insurance Pocket",
                    accountType = FundAccountType.SAVINGS_POCKET.name,
                    colorHex = 0xFF10B981,
                    notes = "Dedicated reserve pocket for scheduled servicing, wear & tear, and annual insurance",
                    collectedAmount = 1200.0,
                    orderIndex = 1
                ),
                FundAccountEntity(
                    id = accountTngId,
                    name = "Touch 'n Go eWallet",
                    accountType = FundAccountType.EWALLET.name,
                    colorHex = 0xFF0284C7,
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

    suspend fun addExpense(
        title: String,
        amount: Double,
        periodType: PeriodType,
        category: ExpenseCategory,
        notes: String,
        fundAccountId: String? = null
    ) {
        val monthlyAmount = if (periodType == PeriodType.YEARLY) amount / 12.0 else amount
        val currentList = expenseDao.getAllExpenses()
        val expense = ExpenseEntity(
            id = UUID.randomUUID().toString(),
            title = title,
            monthlyAmount = monthlyAmount,
            rawAmount = amount,
            periodType = periodType.name,
            category = category.name,
            notes = notes,
            isEnabled = true,
            orderIndex = currentList.size,
            isDefaultSubject = false,
            fundAccountId = fundAccountId
        )
        expenseDao.insertExpense(expense)
    }

    suspend fun updateExpense(
        id: String,
        title: String,
        amount: Double,
        periodType: PeriodType,
        category: ExpenseCategory,
        notes: String,
        isEnabled: Boolean,
        orderIndex: Int,
        isDefaultSubject: Boolean,
        fundAccountId: String? = null
    ) {
        val monthlyAmount = if (periodType == PeriodType.YEARLY) amount / 12.0 else amount
        val expense = ExpenseEntity(
            id = id,
            title = title,
            monthlyAmount = monthlyAmount,
            rawAmount = amount,
            periodType = periodType.name,
            category = category.name,
            notes = notes,
            isEnabled = isEnabled,
            orderIndex = orderIndex,
            isDefaultSubject = isDefaultSubject,
            fundAccountId = fundAccountId
        )
        expenseDao.updateExpense(expense)
    }

    suspend fun setExpenseEnabled(id: String, isEnabled: Boolean) {
        expenseDao.updateEnabled(id, isEnabled)
    }

    suspend fun deleteExpense(id: String) {
        expenseDao.deleteById(id)
    }

    suspend fun assignSubjectToAccount(expenseId: String, accountId: String?) {
        expenseDao.updateFundAccount(expenseId, accountId)
    }

    // Fund Accounts Operations
    suspend fun addFundAccount(
        name: String,
        type: FundAccountType,
        colorHex: Long,
        notes: String,
        collectedAmount: Double = 0.0
    ) {
        val currentAccounts = fundAccountDao.getAllAccounts()
        val account = FundAccountEntity(
            id = UUID.randomUUID().toString(),
            name = name,
            accountType = type.name,
            colorHex = colorHex,
            notes = notes,
            collectedAmount = maxOf(0.0, collectedAmount),
            orderIndex = currentAccounts.size
        )
        fundAccountDao.insertAccount(account)
    }

    suspend fun updateFundAccount(
        id: String,
        name: String,
        type: FundAccountType,
        colorHex: Long,
        notes: String,
        collectedAmount: Double,
        orderIndex: Int
    ) {
        val account = FundAccountEntity(
            id = id,
            name = name,
            accountType = type.name,
            colorHex = colorHex,
            notes = notes,
            collectedAmount = maxOf(0.0, collectedAmount),
            orderIndex = orderIndex
        )
        fundAccountDao.updateAccount(account)
    }

    suspend fun updateAccountCollectedAmount(id: String, newAmount: Double) {
        fundAccountDao.updateCollectedAmount(id, maxOf(0.0, newAmount))
    }

    suspend fun updateAllAccountCollectedAmounts(amounts: Map<String, Double>) {
        amounts.forEach { (id, amount) ->
            fundAccountDao.updateCollectedAmount(id, maxOf(0.0, amount))
        }
    }

    suspend fun deleteFundAccount(id: String) {
        expenseDao.unassignAllForAccount(id)
        fundAccountDao.deleteById(id)
    }

    suspend fun updateSettings(settings: CommitmentSettingsEntity) {
        settingsDao.saveSettings(settings)
    }
}
