package com.gihansgamage.coinspot.presentation.screen.goals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gihansgamage.coinspot.data.local.database.entities.SavingGoal
import com.gihansgamage.coinspot.data.local.preferences.DataStoreManager
import com.gihansgamage.coinspot.domain.repository.SavingGoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGoalScreen(
    onBack: () -> Unit,
    onGoalCreated: () -> Unit,
    viewModel: CreateGoalViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // ViewModel states
    val uiState by viewModel.uiState.collectAsState()
    val currency by viewModel.currency.collectAsState()
    val currencySymbol by viewModel.currencySymbol.collectAsState()

    // Form state
    var goalName by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var targetDate by remember { mutableStateOf(LocalDate.now().plusMonths(6)) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Handle UI state changes
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is CreateGoalUiState.Success -> {
                scope.launch {
                    snackbarHostState.showSnackbar(state.message)
                    onGoalCreated()
                }
                viewModel.clearUiState()
            }
            is CreateGoalUiState.Error -> {
                scope.launch {
                    snackbarHostState.showSnackbar(state.message)
                }
                viewModel.clearUiState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Create New Goal") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Set Your Savings Goal",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Define what you're saving for and when you want to achieve it",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }

            // Goal Name
            OutlinedTextField(
                value = goalName,
                onValueChange = {
                    if (it.length <= 50) {
                        goalName = it
                    }
                },
                label = { Text("Goal Name") },
                placeholder = { Text("e.g., New Laptop, Vacation, Emergency Fund") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = {
                    Text("${goalName.length}/50 characters")
                }
            )

            // Target Amount
            OutlinedTextField(
                value = targetAmount,
                onValueChange = {
                    // Only allow numbers and decimal point with max 2 decimal places
                    if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                        targetAmount = it
                    }
                },
                label = { Text("Target Amount") },
                placeholder = { Text("0.00") },
                leadingIcon = {
                    Text(
                        currencySymbol,
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                isError = targetAmount.isNotEmpty() && (targetAmount.toDoubleOrNull() ?: 0.0) <= 0,
                supportingText = {
                    if (targetAmount.isNotEmpty() && (targetAmount.toDoubleOrNull() ?: 0.0) <= 0) {
                        Text(
                            "Amount must be greater than 0",
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Text("Enter your savings target")
                    }
                }
            )

            // Target Date
            OutlinedTextField(
                value = targetDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")),
                onValueChange = { },
                label = { Text("Target Date") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = "Select date",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                enabled = false,
                supportingText = {
                    Text("When do you want to achieve this goal?")
                }
            )

            // Goal Insights Card
            val daysUntilTarget = java.time.temporal.ChronoUnit.DAYS.between(
                LocalDate.now(),
                targetDate
            )

            if (daysUntilTarget > 0 && targetAmount.isNotEmpty()) {
                val amount = targetAmount.toDoubleOrNull() ?: 0.0
                if (amount > 0) {
                    val dailySaving = amount / daysUntilTarget
                    val weeklySaving = dailySaving * 7
                    val monthlySaving = dailySaving * 30

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Savings Breakdown",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            SavingBreakdownRow(
                                label = "Duration",
                                value = "$daysUntilTarget days (${daysUntilTarget / 30} months)"
                            )
                            SavingBreakdownRow(
                                label = "Daily",
                                value = "$currencySymbol${String.format("%.2f", dailySaving)}"
                            )
                            SavingBreakdownRow(
                                label = "Weekly",
                                value = "$currencySymbol${String.format("%.2f", weeklySaving)}"
                            )
                            SavingBreakdownRow(
                                label = "Monthly",
                                value = "$currencySymbol${String.format("%.2f", monthlySaving)}"
                            )
                        }
                    }
                }
            } else if (daysUntilTarget <= 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = "⚠️ Please select a future date",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Description (Optional)
            OutlinedTextField(
                value = description,
                onValueChange = {
                    if (it.length <= 200) {
                        description = it
                    }
                },
                label = { Text("Description (Optional)") },
                placeholder = { Text("Why are you saving for this goal?") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 4,
                supportingText = {
                    Text("${description.length}/200 characters")
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Create Button
            Button(
                onClick = {
                    val amount = targetAmount.toDoubleOrNull() ?: 0.0
                    if (goalName.isNotEmpty() && amount > 0 && daysUntilTarget > 0) {
                        viewModel.createGoal(
                            name = goalName,
                            targetAmount = amount,
                            targetDate = targetDate,
                            description = description
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = goalName.isNotEmpty() &&
                        (targetAmount.toDoubleOrNull() ?: 0.0) > 0 &&
                        daysUntilTarget > 0 &&
                        uiState !is CreateGoalUiState.Loading
            ) {
                if (uiState is CreateGoalUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Create Goal", style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        CustomDatePickerDialog(
            initialDate = targetDate,
            onDateSelected = { date ->
                targetDate = date
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
private fun SavingBreakdownRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerDialog(
    initialDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    // Set initial date
    val initialDateMillis = initialDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDateMillis,
        yearRange = IntRange(
            LocalDate.now().year,
            LocalDate.now().year + 10
        )
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selectedDate = Instant
                            .ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()

                        // Validate date is in the future
                        if (selectedDate.isAfter(LocalDate.now())) {
                            onDateSelected(selectedDate)
                        }
                    }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

// ViewModel for CreateGoalScreen
@HiltViewModel
class CreateGoalViewModel @Inject constructor(
    private val goalRepository: SavingGoalRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<CreateGoalUiState>(CreateGoalUiState.Idle)
    val uiState: StateFlow<CreateGoalUiState> = _uiState.asStateFlow()

    val currency: StateFlow<String> = dataStoreManager.userCurrency
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            "USD"
        )

    val currencySymbol: StateFlow<String> = dataStoreManager.currencySymbol
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            "$"
        )

    fun createGoal(
        name: String,
        targetAmount: Double,
        targetDate: LocalDate,
        description: String = ""
    ) {
        viewModelScope.launch {
            _uiState.value = CreateGoalUiState.Loading
            try {
                val goal = SavingGoal(
                    name = name,
                    targetAmount = targetAmount,
                    startDate = LocalDate.now(),
                    targetDate = targetDate,
                    currency = currency.value,
                    currencySymbol = currencySymbol.value,
                    description = description
                )
                goalRepository.createGoal(goal)
                _uiState.value = CreateGoalUiState.Success("Goal created successfully! 🎉")
            } catch (e: Exception) {
                _uiState.value = CreateGoalUiState.Error(e.message ?: "Failed to create goal")
            }
        }
    }

    fun clearUiState() {
        _uiState.value = CreateGoalUiState.Idle
    }
}

sealed class CreateGoalUiState {
    object Idle : CreateGoalUiState()
    object Loading : CreateGoalUiState()
    data class Success(val message: String) : CreateGoalUiState()
    data class Error(val message: String) : CreateGoalUiState()
}