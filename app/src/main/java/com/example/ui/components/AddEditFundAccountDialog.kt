package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.FundAccountEntity
import com.example.data.FundAccountType

val PRESET_ACCOUNT_NAMES = listOf(
    "Maybank MAE",
    "CIMB Bank",
    "Touch 'n Go eWallet",
    "GXBank Pocket",
    "Tabung Haji / ASB",
    "Emergency Cash Pocket",
    "Salary Auto-Deduction"
)

val ACCOUNT_COLOR_OPTIONS = listOf(
    0xFFF59E0B, // Maybank Gold
    0xFFEF4444, // CIMB / Red
    0xFF0284C7, // TnG / Cyan
    0xFF10B981, // Emerald Green
    0xFF8B5CF6, // Purple
    0xFF6366F1, // Indigo
    0xFFEC4899, // Pink
    0xFF64748B  // Slate
)

fun getAccountTypeIcon(type: String): ImageVector {
    return when (type) {
        FundAccountType.BANK_ACCOUNT.name -> Icons.Default.AccountBalance
        FundAccountType.EWALLET.name -> Icons.Default.AccountBalanceWallet
        FundAccountType.SAVINGS_POCKET.name -> Icons.Default.Savings
        FundAccountType.SALARY_DEDUCTION.name -> Icons.Default.Payments
        FundAccountType.CASH.name -> Icons.Default.Money
        else -> Icons.Default.AccountBalance
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditFundAccountDialog(
    existingAccount: FundAccountEntity?,
    onDismiss: () -> Unit,
    onSave: (name: String, type: FundAccountType, colorHex: Long, notes: String, collectedAmount: Double) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    val isEditing = existingAccount != null
    var name by remember { mutableStateOf(existingAccount?.name ?: "") }
    var collectedText by remember {
        mutableStateOf(
            if (existingAccount != null && existingAccount.collectedAmount > 0) {
                if (existingAccount.collectedAmount % 1.0 == 0.0) existingAccount.collectedAmount.toInt().toString()
                else String.format(java.util.Locale.US, "%.2f", existingAccount.collectedAmount)
            } else ""
        )
    }
    var selectedType by remember {
        mutableStateOf(
            try {
                if (existingAccount != null) FundAccountType.valueOf(existingAccount.accountType) else FundAccountType.BANK_ACCOUNT
            } catch (e: Exception) {
                FundAccountType.BANK_ACCOUNT
            }
        )
    }
    var selectedColor by remember { mutableLongStateOf(existingAccount?.colorHex ?: 0xFF0284C7) }
    var notes by remember { mutableStateOf(existingAccount?.notes ?: "") }
    var errorMessage by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 24.dp)
                .testTag("add_edit_fund_account_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(selectedColor).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getAccountTypeIcon(selectedType.name),
                                contentDescription = null,
                                tint = Color(selectedColor),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isEditing) "Edit Fund Account" else "Add Fund Account",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = "Add an account or pocket to divide your commitments and track how much money is collected in it.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Quick Presets
                if (!isEditing) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Quick Presets:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PRESET_ACCOUNT_NAMES.forEach { preset ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable {
                                    name = preset
                                    if (preset.contains("eWallet", ignoreCase = true)) {
                                        selectedType = FundAccountType.EWALLET
                                        selectedColor = 0xFF0284C7
                                    } else if (preset.contains("Maybank", ignoreCase = true)) {
                                        selectedType = FundAccountType.BANK_ACCOUNT
                                        selectedColor = 0xFFF59E0B
                                    } else if (preset.contains("CIMB", ignoreCase = true)) {
                                        selectedType = FundAccountType.BANK_ACCOUNT
                                        selectedColor = 0xFFEF4444
                                    } else if (preset.contains("Pocket", ignoreCase = true) || preset.contains("Savings", ignoreCase = true)) {
                                        selectedType = FundAccountType.SAVINGS_POCKET
                                        selectedColor = 0xFF10B981
                                    }
                                }
                            ) {
                                Text(
                                    text = preset,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Account Name
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (errorMessage.isNotEmpty()) errorMessage = ""
                    },
                    label = { Text("Account / Fund Name") },
                    placeholder = { Text("e.g. Maybank Salary, Touch 'n Go") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("fund_account_name_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Collected Amount in this Account
                OutlinedTextField(
                    value = collectedText,
                    onValueChange = { collectedText = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Collected Money in this Account (RM)") },
                    prefix = { Text("RM ", fontWeight = FontWeight.Bold, color = Color(0xFF10B981)) },
                    placeholder = { Text("e.g. 3000.00") },
                    supportingText = { Text("Tallies directly into 'Collected So Far' on the dashboard") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("fund_account_collected_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Account Type Selector
                Text(
                    text = "Account Type",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FundAccountType.values().forEach { type ->
                        val isSelected = selectedType == type
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedType = type },
                            label = { Text(type.name.replace("_", " ").lowercase().capitalize(), fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = getAccountTypeIcon(type.name),
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Color Theme Selector
                Text(
                    text = "Account Color Badge",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ACCOUNT_COLOR_OPTIONS.forEach { colorVal ->
                        val isSelected = selectedColor == colorVal
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(colorVal))
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = colorVal }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Notes / Description
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (e.g. auto-debit on 5th, purpose)") },
                    placeholder = { Text("e.g. Auto-debit on 5th of every month") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("fund_account_notes_input"),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 2
                )

                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isEditing && onDelete != null) {
                        TextButton(
                            onClick = {
                                onDismiss()
                                onDelete()
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier.testTag("dialog_delete_account_button")
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Delete Account", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = onDismiss) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (name.isBlank()) {
                                    errorMessage = "Please enter an account name."
                                    return@Button
                                }
                                val collected = collectedText.toDoubleOrNull() ?: 0.0
                                onSave(name.trim(), selectedType, selectedColor, notes.trim(), collected)
                            },
                            modifier = Modifier.testTag("save_fund_account_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(if (isEditing) "Save Changes" else "Add Account")
                        }
                    }
                }
            }
        }
    }
}

private fun String.capitalize(): String =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.getDefault()) else it.toString() }
