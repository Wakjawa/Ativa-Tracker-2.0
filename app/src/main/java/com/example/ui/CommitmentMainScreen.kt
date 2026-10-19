package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ExpenseEntity
import com.example.data.FundAccountEntity
import com.example.ui.components.AddEditFundAccountDialog
import com.example.ui.components.AddEditSubjectDialog
import com.example.ui.components.AssignCommitmentsDialog
import com.example.ui.components.CarHeroCard
import com.example.ui.components.CommitmentSummaryCard
import com.example.ui.components.EditCollectedAmountDialog
import com.example.ui.components.FundsTabContent
import com.example.ui.components.SubjectItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommitmentMainScreen(
    viewModel: CommitmentViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }

    // Dialog States for Subjects & Collected Amount
    var showAddSubjectDialog by remember { mutableStateOf(false) }
    var editingExpense by remember { mutableStateOf<ExpenseEntity?>(null) }
    var deletingExpense by remember { mutableStateOf<ExpenseEntity?>(null) }
    var showEditCollectedDialog by remember { mutableStateOf(false) }

    // Dialog States for Fund Accounts
    var showAddAccountDialog by remember { mutableStateOf(false) }
    var editingAccount by remember { mutableStateOf<FundAccountEntity?>(null) }
    var deletingAccount by remember { mutableStateOf<FundAccountEntity?>(null) }
    var assigningAccount by remember { mutableStateOf<FundAccountEntity?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                CenterAlignedTopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsCar,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Ativa Tracker",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Perodua Ativa AV 2-Tone",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )

                // Navigation Tabs
                PrimaryTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary,
                    indicator = {
                        TabRowDefaults.PrimaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(selectedTabIndex = selectedTab),
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsCar,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Commitments", fontWeight = FontWeight.SemiBold)
                            }
                        },
                        modifier = Modifier.testTag("tab_commitments")
                    )

                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalance,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Funds & Accounts", fontWeight = FontWeight.SemiBold)
                            }
                        },
                        modifier = Modifier.testTag("tab_funds")
                    )
                }
            }
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                ExtendedFloatingActionButton(
                    onClick = { showAddSubjectDialog = true },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Add Subject", fontWeight = FontWeight.SemiBold) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.testTag("fab_add_subject")
                )
            } else {
                ExtendedFloatingActionButton(
                    onClick = { showAddAccountDialog = true },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Add Account", fontWeight = FontWeight.SemiBold) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.testTag("fab_add_account")
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (selectedTab == 0) {
                // TAB 0: COMMITMENTS
                // 1. Car Hero Showcase (Perodua Ativa AV 2-Tone Pearl Diamond White without RM 73,400 OTR)
                item(key = "car_hero") {
                    CarHeroCard()
                }

                // 2. Total Monthly Commitment Summary
                item(key = "commitment_summary") {
                    CommitmentSummaryCard(
                        state = uiState,
                        onOpenEditCollected = { showEditCollectedDialog = true }
                    )
                }

                // 3. Section Header for Monthly Subjects
                item(key = "subjects_header") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Monthly Commitment Subjects",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Tap any subject to alter payment, frequency, or account",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        TextButton(
                            onClick = { showAddSubjectDialog = true },
                            modifier = Modifier.testTag("header_add_subject_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // 4. List of Subjects
                items(
                    items = uiState.expenses,
                    key = { it.id }
                ) { expense ->
                    val linkedAccount = uiState.fundAccounts.firstOrNull { it.id == expense.fundAccountId }
                    SubjectItemCard(
                        expense = expense,
                        accountName = linkedAccount?.name,
                        accountColorHex = linkedAccount?.colorHex,
                        onEdit = { editingExpense = expense },
                        onDelete = { deletingExpense = expense },
                        onToggle = { isEnabled -> viewModel.toggleExpense(expense.id, isEnabled) }
                    )
                }
            } else {
                // TAB 1: FUNDS & ACCOUNTS
                item(key = "funds_tab_content") {
                    FundsTabContent(
                        state = uiState,
                        onAddNewAccount = { showAddAccountDialog = true },
                        onEditAccount = { editingAccount = it },
                        onDeleteAccount = { deletingAccount = it },
                        onManageAssignments = { assigningAccount = it }
                    )
                }
            }

            // Bottom Spacer for FAB
            item(key = "bottom_spacer") {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }

    // Add Subject Dialog
    if (showAddSubjectDialog) {
        AddEditSubjectDialog(
            existingExpense = null,
            fundAccounts = uiState.fundAccounts,
            onDismiss = { showAddSubjectDialog = false },
            onSave = { title, amount, period, category, notes, fundAccountId ->
                viewModel.addSubject(title, amount, period, category, notes, fundAccountId)
                showAddSubjectDialog = false
            }
        )
    }

    // Edit Subject Dialog
    editingExpense?.let { expense ->
        AddEditSubjectDialog(
            existingExpense = expense,
            fundAccounts = uiState.fundAccounts,
            onDismiss = { editingExpense = null },
            onSave = { title, amount, period, category, notes, fundAccountId ->
                viewModel.updateSubject(
                    id = expense.id,
                    title = title,
                    amount = amount,
                    period = period,
                    category = category,
                    notes = notes,
                    isEnabled = expense.isEnabled,
                    orderIndex = expense.orderIndex,
                    isDefault = expense.isDefaultSubject,
                    fundAccountId = fundAccountId
                )
                editingExpense = null
            }
        )
    }

    // Delete Subject Confirmation Dialog
    deletingExpense?.let { expense ->
        AlertDialog(
            onDismissRequest = { deletingExpense = null },
            title = { Text("Delete Commitment Subject?") },
            text = {
                Text("Are you sure you want to remove \"${expense.title}\" (${CurrencyUtils.formatRm(expense.monthlyAmount)}/mo)? You can always re-add it later.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteSubject(expense.id)
                        deletingExpense = null
                    },
                    modifier = Modifier.testTag("confirm_delete_subject_button")
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingExpense = null }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(18.dp)
        )
    }

    // Add Fund Account Dialog
    if (showAddAccountDialog) {
        AddEditFundAccountDialog(
            existingAccount = null,
            onDismiss = { showAddAccountDialog = false },
            onSave = { name, type, colorHex, notes, collectedAmount ->
                viewModel.addFundAccount(name, type, colorHex, notes, collectedAmount)
                showAddAccountDialog = false
            }
        )
    }

    // Edit Fund Account Dialog
    editingAccount?.let { account ->
        AddEditFundAccountDialog(
            existingAccount = account,
            onDismiss = { editingAccount = null },
            onSave = { name, type, colorHex, notes, collectedAmount ->
                viewModel.updateFundAccount(account.id, name, type, colorHex, notes, collectedAmount, account.orderIndex)
                editingAccount = null
            },
            onDelete = {
                deletingAccount = account
                editingAccount = null
            }
        )
    }

    // Delete Fund Account Confirmation Dialog
    deletingAccount?.let { account ->
        AlertDialog(
            onDismissRequest = { deletingAccount = null },
            title = { Text("Delete Fund Account?") },
            text = {
                Text("Are you sure you want to remove \"${account.name}\"? Any commitment subjects currently assigned to this account will become unassigned.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteFundAccount(account.id)
                        deletingAccount = null
                    },
                    modifier = Modifier.testTag("confirm_delete_account_button")
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingAccount = null }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(18.dp)
        )
    }

    // Assign Commitments to Account Dialog
    assigningAccount?.let { account ->
        AssignCommitmentsDialog(
            account = account,
            allExpenses = uiState.expenses.filter { it.isEnabled },
            onDismiss = { assigningAccount = null },
            onSaveAssignments = { assignedExpenseIds ->
                uiState.expenses.forEach { expense ->
                    if (assignedExpenseIds.contains(expense.id)) {
                        viewModel.assignExpenseToAccount(expense.id, account.id)
                    } else if (expense.fundAccountId == account.id) {
                        viewModel.assignExpenseToAccount(expense.id, null)
                    }
                }
                assigningAccount = null
            }
        )
    }

    // Edit Collected Amount Dialog
    if (showEditCollectedDialog) {
        EditCollectedAmountDialog(
            fundAccounts = uiState.fundAccounts,
            totalCollected = uiState.collectedSoFar,
            onDismiss = { showEditCollectedDialog = false },
            onSaveBalances = { balancesMap ->
                viewModel.updateAllAccountBalances(balancesMap)
                showEditCollectedDialog = false
            },
            onAddNewAccount = {
                showEditCollectedDialog = false
                selectedTab = 1
                showAddAccountDialog = true
            }
        )
    }
}
