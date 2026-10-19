package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AtivaDatabase
import com.example.data.CommitmentRepository
import com.example.data.CommitmentSettingsEntity
import com.example.data.ExpenseCategory
import com.example.data.ExpenseEntity
import com.example.data.FundAccountEntity
import com.example.data.FundAccountType
import com.example.data.PeriodType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FundAccountWithExpenses(
    val account: FundAccountEntity,
    val assignedExpenses: List<ExpenseEntity>,
    val totalMonthlyNeed: Double,
    val percentageOfTotal: Float
)

data class CommitmentUiState(
    val expenses: List<ExpenseEntity> = emptyList(),
    val fundAccounts: List<FundAccountEntity> = emptyList(),
    val accountsWithExpenses: List<FundAccountWithExpenses> = emptyList(),
    val unassignedExpenses: List<ExpenseEntity> = emptyList(),
    val unassignedMonthlyTotal: Double = 0.0,
    val settings: CommitmentSettingsEntity = CommitmentSettingsEntity(),
    val isInitialized: Boolean = false,
    val totalMonthlyCommitment: Double = 0.0,
    val collectedSoFar: Double = 4500.0, // Tally of all fund accounts' collected amounts
    val downPaymentAmount: Double = 7340.0,
    val calculatedLoanInstallment: Double = 788.0
)

class CommitmentViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AtivaDatabase.getDatabase(application)
    private val repository = CommitmentRepository(
        database.expenseDao(),
        database.settingsDao(),
        database.fundAccountDao()
    )

    val uiState: StateFlow<CommitmentUiState> = combine(
        repository.allExpenses,
        repository.settings,
        repository.allFundAccounts
    ) { expensesList, settingsEntity, accountsList ->
        val currentSettings = settingsEntity ?: CommitmentSettingsEntity()
        val activeExpenses = expensesList.filter { it.isEnabled }
        val monthlyTotal = activeExpenses.sumOf { it.monthlyAmount }

        // Loan calculations
        val carPrice = currentSettings.carPriceOtr
        val downPayment = carPrice * (currentSettings.downPaymentPercent / 100.0)
        val principal = maxOf(0.0, carPrice - downPayment)
        val interest = principal * (currentSettings.loanInterestRate / 100.0) * currentSettings.loanTenureYears
        val totalLoan = principal + interest
        val loanMonths = currentSettings.loanTenureYears * 12
        val calculatedInstallment = if (loanMonths > 0) totalLoan / loanMonths else 0.0

        // Compute fund account distributions
        val accountsWithExpenses = accountsList.map { account ->
            val assigned = activeExpenses.filter { it.fundAccountId == account.id }
            val accountTotal = assigned.sumOf { it.monthlyAmount }
            val pct = if (monthlyTotal > 0.0) (accountTotal / monthlyTotal).toFloat() else 0f
            FundAccountWithExpenses(
                account = account,
                assignedExpenses = assigned,
                totalMonthlyNeed = accountTotal,
                percentageOfTotal = pct
            )
        }

        val unassigned = activeExpenses.filter { expense ->
            expense.fundAccountId == null || accountsList.none { it.id == expense.fundAccountId }
        }
        val unassignedTotal = unassigned.sumOf { it.monthlyAmount }

        // Collected So Far is explicitly calculated to tally with all fund accounts' collected amounts!
        val totalCollectedAcrossAccounts = accountsList.sumOf { it.collectedAmount }

        CommitmentUiState(
            expenses = expensesList,
            fundAccounts = accountsList,
            accountsWithExpenses = accountsWithExpenses,
            unassignedExpenses = unassigned,
            unassignedMonthlyTotal = unassignedTotal,
            settings = currentSettings,
            isInitialized = true,
            totalMonthlyCommitment = monthlyTotal,
            collectedSoFar = totalCollectedAcrossAccounts,
            downPaymentAmount = downPayment,
            calculatedLoanInstallment = calculatedInstallment
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        CommitmentUiState()
    )

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
        }
    }

    fun toggleExpense(id: String, isEnabled: Boolean) {
        viewModelScope.launch {
            repository.setExpenseEnabled(id, isEnabled)
        }
    }

    fun addSubject(
        title: String,
        amount: Double,
        period: PeriodType,
        category: ExpenseCategory,
        notes: String,
        fundAccountId: String? = null
    ) {
        viewModelScope.launch {
            repository.addExpense(title, amount, period, category, notes, fundAccountId)
        }
    }

    fun updateSubject(
        id: String,
        title: String,
        amount: Double,
        period: PeriodType,
        category: ExpenseCategory,
        notes: String,
        isEnabled: Boolean,
        orderIndex: Int,
        isDefault: Boolean,
        fundAccountId: String? = null
    ) {
        viewModelScope.launch {
            repository.updateExpense(id, title, amount, period, category, notes, isEnabled, orderIndex, isDefault, fundAccountId)
        }
    }

    fun deleteSubject(id: String) {
        viewModelScope.launch {
            repository.deleteExpense(id)
        }
    }

    fun assignExpenseToAccount(expenseId: String, accountId: String?) {
        viewModelScope.launch {
            repository.assignSubjectToAccount(expenseId, accountId)
        }
    }

    // Fund Accounts actions
    fun addFundAccount(
        name: String,
        type: FundAccountType,
        colorHex: Long,
        notes: String,
        collectedAmount: Double
    ) {
        viewModelScope.launch {
            repository.addFundAccount(name, type, colorHex, notes, collectedAmount)
        }
    }

    fun updateFundAccount(
        id: String,
        name: String,
        type: FundAccountType,
        colorHex: Long,
        notes: String,
        collectedAmount: Double,
        orderIndex: Int
    ) {
        viewModelScope.launch {
            repository.updateFundAccount(id, name, type, colorHex, notes, collectedAmount, orderIndex)
        }
    }

    fun updateAccountBalance(id: String, newBalance: Double) {
        viewModelScope.launch {
            repository.updateAccountCollectedAmount(id, newBalance)
        }
    }

    fun updateAllAccountBalances(balances: Map<String, Double>) {
        viewModelScope.launch {
            repository.updateAllAccountCollectedAmounts(balances)
        }
    }

    fun addQuickDepositToAccount(id: String, delta: Double) {
        val account = uiState.value.fundAccounts.firstOrNull { it.id == id }
        if (account != null) {
            val newAmount = maxOf(0.0, account.collectedAmount + delta)
            viewModelScope.launch {
                repository.updateAccountCollectedAmount(id, newAmount)
            }
        }
    }

    fun deleteFundAccount(id: String) {
        viewModelScope.launch {
            repository.deleteFundAccount(id)
        }
    }
}
