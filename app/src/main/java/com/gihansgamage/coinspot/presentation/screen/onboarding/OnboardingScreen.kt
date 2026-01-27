package com.gihansgamage.coinspot.presentation.screen.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gihansgamage.coinspot.data.model.Country
import com.gihansgamage.coinspot.presentation.viewmodel.OnboardingViewModel
import com.gihansgamage.coinspot.presentation.viewmodel.OnboardingUiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var currentStep by remember { mutableStateOf(0) }
    var name by remember { mutableStateOf("") }
    var selectedCountry by remember { mutableStateOf<Country?>(null) }
    var monthlyIncome by remember { mutableStateOf("") }
    var monthlyExpenses by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()

    // Handle UI state
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is OnboardingUiState.Success -> {
                onComplete()
            }
            is OnboardingUiState.Error -> {
                scope.launch {
                    snackbarHostState.showSnackbar(state.message)
                }
                viewModel.clearUiState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Progress indicator
            LinearProgressIndicator(
                progress = (currentStep + 1) / 3f,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            when (currentStep) {
                0 -> WelcomeStep(
                    name = name,
                    onNameChange = { name = it },
                    onNext = {
                        if (name.isNotEmpty()) currentStep++
                    }
                )

                1 -> CountryStep(
                    selectedCountry = selectedCountry,
                    onCountrySelected = { selectedCountry = it },
                    onNext = {
                        if (selectedCountry != null) currentStep++
                    },
                    onBack = { currentStep-- }
                )

                2 -> FinancialStep(
                    monthlyIncome = monthlyIncome,
                    onIncomeChange = { monthlyIncome = it },
                    monthlyExpenses = monthlyExpenses,
                    onExpensesChange = { monthlyExpenses = it },
                    onComplete = {
                        selectedCountry?.let { country ->
                            viewModel.completeOnboarding(
                                name = name,
                                country = country.name,
                                currency = country.currency,
                                currencySymbol = country.symbol,
                                monthlyIncome = monthlyIncome.toDoubleOrNull() ?: 0.0,
                                monthlyExpenses = monthlyExpenses.toDoubleOrNull() ?: 0.0
                            )
                        }
                    },
                    onBack = { currentStep-- },
                    isLoading = uiState is OnboardingUiState.Loading
                )
            }
        }
    }
}

@Composable
fun WelcomeStep(
    name: String,
    onNameChange: (String) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "👋",
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Welcome to CoinsPot!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Let's start your savings journey",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(48.dp))

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("What's your name?") },
            placeholder = { Text("Enter your name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotEmpty()
        ) {
            Text("Next")
        }
    }
}

@Composable
fun CountryStep(
    selectedCountry: Country?,
    onCountrySelected: (Country) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val countries = remember {
        listOf(
            Country("United States", "USD", "$"),
            Country("United Kingdom", "GBP", "£"),
            Country("European Union", "EUR", "€"),
            Country("Sri Lanka", "LKR", "Rs"),
            Country("India", "INR", "₹"),
            Country("Japan", "JPY", "¥"),
            Country("Australia", "AUD", "A$"),
            Country("Canada", "CAD", "C$")
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🌍",
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Select Your Country",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "This helps us set your currency",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            countries.forEach { country ->
                CountryCard(
                    country = country,
                    isSelected = selectedCountry == country,
                    onClick = { onCountrySelected(country) }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text("Back")
            }

            Button(
                onClick = onNext,
                modifier = Modifier.weight(1f),
                enabled = selectedCountry != null
            ) {
                Text("Next")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryCard(
    country: Country,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (isSelected) {
            CardDefaults.outlinedCardBorder().copy(
                width = 2.dp,
                brush = androidx.compose.ui.graphics.SolidColor(
                    MaterialTheme.colorScheme.primary
                )
            )
        } else {
            CardDefaults.outlinedCardBorder()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = country.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${country.currency} (${country.symbol})",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isSelected) {
                Text(
                    text = "✓",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun FinancialStep(
    monthlyIncome: String,
    onIncomeChange: (String) -> Unit,
    monthlyExpenses: String,
    onExpensesChange: (String) -> Unit,
    onComplete: () -> Unit,
    onBack: () -> Unit,
    isLoading: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "💰",
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Financial Information",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Optional: This helps us give you better insights",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = monthlyIncome,
            onValueChange = {
                if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                    onIncomeChange(it)
                }
            },
            label = { Text("Monthly Income (Optional)") },
            placeholder = { Text("0.00") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = monthlyExpenses,
            onValueChange = {
                if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                    onExpensesChange(it)
                }
            },
            label = { Text("Monthly Expenses (Optional)") },
            placeholder = { Text("0.00") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f),
                enabled = !isLoading
            ) {
                Text("Back")
            }

            Button(
                onClick = onComplete,
                modifier = Modifier.weight(1f),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Complete")
                }
            }
        }
    }
}