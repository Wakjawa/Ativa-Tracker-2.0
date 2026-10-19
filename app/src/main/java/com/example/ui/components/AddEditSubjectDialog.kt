package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.ExpenseCategory
import com.example.data.ExpenseEntity
import com.example.data.FundAccountEntity
import com.example.data.PeriodType
import com.example.ui.CurrencyUtils

data class PresetSubject(
    val title: String,
    val amount: Double,
    val period: PeriodType,
    val category: ExpenseCategory,
    val notes: String
)

val PRESET_SUBJECTS = listOf(
    PresetSubject("Fuel / RON95 Petrol", 180.0, PeriodType.MONTHLY, ExpenseCategory.FUEL, "~1,200 km/mo at 18.9 km/L"),
    PresetSubject("Tolls & Touch 'n Go", 60.0, PeriodType.MONTHLY, ExpenseCategory.TOLL, "Daily highway toll commutes"),
    PresetSubject("Car Wash & Detailing", 50.0, PeriodType.MONTHLY, ExpenseCategory.DETAILING, "Bi-weekly wash & wax to maintain Pearl White paint"),
    PresetSubject("Tire Replacement Fund", 70.0, PeriodType.MONTHLY, ExpenseCategory.MAINTENANCE, "Save for 4x 17-inch tires every 3 years"),
    PresetSubject("Accessories & Tinting", 120.0, PeriodType.MONTHLY, ExpenseCategory.ACCESSORIES, "High rejection solar film installment"),
    PresetSubject("Emergency Repairs Fund", 100.0, PeriodType.MONTHLY, ExpenseCategory.MAINTENANCE, "Unexpected battery/tire puncture reserve")
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditSubjectDialog(
    existingExpense: ExpenseEntity?,
    fundAccounts: List<FundAccountEntity>,
    onDismiss: () -> Unit,
    onSave: (title: String, amount: Double, period: PeriodType, category: ExpenseCategory, notes: String, fundAccountId: String?) -> Unit
) {
    val isEditing = existingExpense != null
    var title by remember { mutableStateOf(existingExpense?.title ?: "") }
    var amountText by remember {
        mutableStateOf(
            if (existingExpense != null) {
                if (existingExpense.rawAmount > 0) {
                    if (existingExpense.rawAmount % 1.0 == 0.0) {
                        existingExpense.rawAmount.toInt().toString()
                    } else {
                        String.format(java.util.Locale.US, "%.2f", existingExpense.rawAmount)
                    }
                } else ""
            } else ""
        )
    }
    var selectedPeriod by remember {
        mutableStateOf(
            if (existingExpense?.periodType == PeriodType.YEARLY.name) PeriodType.YEARLY else PeriodType.MONTHLY
        )
    }
    var selectedCategory by remember {
        mutableStateOf(
            try {
                if (existingExpense != null) ExpenseCategory.valueOf(existingExpense.category) else ExpenseCategory.OTHER
            } catch (e: Exception) {
                ExpenseCategory.OTHER
            }
        )
    }
    var selectedFundAccountId by remember { mutableStateOf<String?>(existingExpense?.fundAccountId) }
    var notes by remember { mutableStateOf(existingExpense?.notes ?: "") }
    var errorMessage by remember { mutableStateOf("") }

    val parsedAmount = amountText.toDoubleOrNull() ?: 0.0
    val effectiveMonthly = if (selectedPeriod == PeriodType.YEARLY) parsedAmount / 12.0 else parsedAmount

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 24.dp)
                .testTag("add_edit_subject_dialog"),
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
                    Text(
                        text = if (isEditing) "Edit Commitment Subject" else "Add Commitment Subject",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = if (isEditing) "Update the monthly amount or details for this car expense."
                    else "Add an additional monthly or yearly expense for your Ativa.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Quick Presets if adding new
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
                        PRESET_SUBJECTS.forEach { preset ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable {
                                    title = preset.title
                                    amountText = preset.amount.toInt().toString()
                                    selectedPeriod = preset.period
                                    selectedCategory = preset.category
                                    notes = preset.notes
                                }
                            ) {
                                Text(
                                    text = "+ ${preset.title}",
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

                // Subject Title
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (errorMessage.isNotEmpty()) errorMessage = ""
                    },
                    label = { Text("Subject Name") },
                    placeholder = { Text("e.g. Monthly Car Payment, Petrol, Toll") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("subject_name_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Period Selector (Monthly vs Yearly)
                Text(
                    text = "Billing Frequency",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                TabRow(
                    selectedTabIndex = if (selectedPeriod == PeriodType.MONTHLY) 0 else 1,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedPeriod == PeriodType.MONTHLY,
                        onClick = { selectedPeriod = PeriodType.MONTHLY },
                        text = { Text("Monthly", fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = selectedPeriod == PeriodType.YEARLY,
                        onClick = { selectedPeriod = PeriodType.YEARLY },
                        text = { Text("Yearly (e.g. Insurance)", fontWeight = FontWeight.SemiBold) }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Amount Field
                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = it.filter { char -> char.isDigit() || char == '.' }
                        if (errorMessage.isNotEmpty()) errorMessage = ""
                    },
                    label = {
                        Text(if (selectedPeriod == PeriodType.MONTHLY) "Monthly Amount (RM)" else "Yearly Total (RM)")
                    },
                    placeholder = { Text("e.g. 788.00") },
                    prefix = { Text("RM ", fontWeight = FontWeight.Bold) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("subject_amount_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Effective Monthly preview if yearly
                if (selectedPeriod == PeriodType.YEARLY && parsedAmount > 0) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "= ${CurrencyUtils.formatRm(effectiveMonthly)} per month into savings fund",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Fund Account Selection (Dividing Commitments)
                if (fundAccounts.isNotEmpty()) {
                    Text(
                        text = "Paid From Fund Account",
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
                        FilterChip(
                            selected = selectedFundAccountId == null,
                            onClick = { selectedFundAccountId = null },
                            label = { Text("None / General", fontSize = 11.sp) },
                            shape = RoundedCornerShape(8.dp)
                        )
                        fundAccounts.forEach { acc ->
                            val isSelected = selectedFundAccountId == acc.id
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedFundAccountId = acc.id },
                                label = { Text(acc.name, fontSize = 11.sp) },
                                leadingIcon = {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color(acc.colorHex))
                                    )
                                },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Category selector
                Text(
                    text = "Category Icon",
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
                    ExpenseCategory.values().forEach { cat ->
                        val isSelected = selectedCategory == cat
                        val (icon, color) = getCategoryIconAndColor(cat.name)
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat.name.replace("_", " ").lowercase().capitalize(), fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else color
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

                // Optional Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / Description (Optional)") },
                    placeholder = { Text("e.g. 9-year tenure, renewal schedule") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("subject_notes_input"),
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

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isBlank()) {
                                errorMessage = "Please enter a subject name."
                                return@Button
                            }
                            val amt = amountText.toDoubleOrNull()
                            if (amt == null || amt <= 0.0) {
                                errorMessage = "Please enter a valid amount greater than 0."
                                return@Button
                            }
                            onSave(title.trim(), amt, selectedPeriod, selectedCategory, notes.trim(), selectedFundAccountId)
                        },
                        modifier = Modifier.testTag("save_subject_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (isEditing) "Save Changes" else "Add Subject")
                    }
                }
            }
        }
    }
}

private fun String.capitalize(): String =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.getDefault()) else it.toString() }
