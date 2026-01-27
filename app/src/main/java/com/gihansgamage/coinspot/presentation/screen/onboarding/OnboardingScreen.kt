package com.gihansgamage.coinspot.presentation.screen.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.gihansgamage.coinspot.data.model.CountryData
import com.gihansgamage.coinspot.presentation.components.common.AppTextField
import com.gihansgamage.coinspot.presentation.screen.onboarding.components.OnboardingStep
import com.gihansgamage.coinspot.presentation.viewmodel.OnboardingViewModel

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var currentStep by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Welcome to CoinsPot",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Let's set up your saving profile",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        when (currentStep) {
            0 -> PersonalInfoStep(uiState, viewModel)
            1 -> FinancialInfoStep(uiState, viewModel)
            2 -> SavingStyleStep(uiState, viewModel)
            3 -> SummaryStep(uiState, viewModel)
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (currentStep > 0) {
                OutlinedButton(onClick = { currentStep-- }) {
                    Text("Back")
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            if (currentStep < 3) {
                Button(
                    onClick = { currentStep++ },
                    enabled = validateStep(currentStep, uiState)
                ) {
                    Text("Next")
                }
            } else {
                Button(
                    onClick = {
                        viewModel.saveUserProfile()
                        onComplete()
                    }
                ) {
                    Text("Get Started")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoStep(
    uiState: OnboardingViewModel.OnboardingUiState,
    viewModel: OnboardingViewModel
) {
    OnboardingStep(
        title = "Personal Information",
        description = "Tell us about yourself"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            AppTextField(
                value = uiState.name,
                onValueChange = { viewModel.updateName(it) },
                label = "Your Name"
            )

            AppTextField(
                value = if (uiState.age == 0) "" else uiState.age.toString(),
                onValueChange = { viewModel.updateAge(it.toIntOrNull() ?: 0) },
                label = "Age",
                keyboardType = KeyboardType.Number
            )

            // Country Selection Dropdown
            var expanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = uiState.selectedCountry?.name ?: "Select Country",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Country") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    CountryData.countries.forEach { country ->
                        DropdownMenuItem(
                            text = { Text("${country.name} (${country.currency})") },
                            onClick = {
                                viewModel.updateCountry(country)
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Display selected currency
            if (uiState.selectedCountry != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Currency:",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "${uiState.currencySymbol} ${uiState.currency}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FinancialInfoStep(
    uiState: OnboardingViewModel.OnboardingUiState,
    viewModel: OnboardingViewModel
) {
    OnboardingStep(
        title = "Financial Information",
        description = "Let's understand your finances"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            AppTextField(
                value = if (uiState.monthlyIncome == 0.0) "" else uiState.monthlyIncome.toString(),
                onValueChange = { viewModel.updateIncome(it.toDoubleOrNull() ?: 0.0) },
                label = "Monthly Income (${uiState.currencySymbol})",
                keyboardType = KeyboardType.Decimal
            )

            AppTextField(
                value = if (uiState.livingCosts == 0.0) "" else uiState.livingCosts.toString(),
                onValueChange = { viewModel.updateLivingCosts(it.toDoubleOrNull() ?: 0.0) },
                label = "Living Costs - Rent, Utilities (${uiState.currencySymbol})",
                keyboardType = KeyboardType.Decimal
            )

            AppTextField(
                value = if (uiState.foodExpenses == 0.0) "" else uiState.foodExpenses.toString(),
                onValueChange = { viewModel.updateFoodExpenses(it.toDoubleOrNull() ?: 0.0) },
                label = "Food Expenses (${uiState.currencySymbol})",
                keyboardType = KeyboardType.Decimal
            )

            AppTextField(
                value = if (uiState.otherCosts == 0.0) "" else uiState.otherCosts.toString(),
                onValueChange = { viewModel.updateOtherCosts(it.toDoubleOrNull() ?: 0.0) },
                label = "Other Monthly Costs (${uiState.currencySymbol})",
                keyboardType = KeyboardType.Decimal
            )

            // Show disposable income
            if (uiState.disposableIncome > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Disposable Income",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "${uiState.currencySymbol} ${String.format("%.2f", uiState.disposableIncome)}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SavingStyleStep(
    uiState: OnboardingViewModel.OnboardingUiState,
    viewModel: OnboardingViewModel
) {
    OnboardingStep(
        title = "Saving Style",
        description = "Choose how aggressively you want to save"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            listOf("Conservative", "Balanced", "Aggressive").forEach { style ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (uiState.savingStyle == style.lowercase()) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    ),
                    onClick = { viewModel.updateSavingStyle(style.lowercase()) }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = style,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = when (style) {
                                "Conservative" -> "Save 5% of disposable income"
                                "Balanced" -> "Save 10% of disposable income"
                                "Aggressive" -> "Save 20% of disposable income"
                                else -> ""
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            if (uiState.recommendedDailySaving > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Recommended Daily Saving",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = "${uiState.currencySymbol} ${String.format("%.2f", uiState.recommendedDailySaving)}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryStep(
    uiState: OnboardingViewModel.OnboardingUiState,
    viewModel: OnboardingViewModel
) {
    OnboardingStep(
        title = "Summary",
        description = "Review your saving plan"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            SummaryItem(
                title = "Name",
                value = uiState.name
            )
            SummaryItem(
                title = "Country",
                value = uiState.selectedCountry?.name ?: ""
            )
            SummaryItem(
                title = "Currency",
                value = "${uiState.currencySymbol} ${uiState.currency}"
            )
            SummaryItem(
                title = "Disposable Income",
                value = "${uiState.currencySymbol} ${String.format("%.2f", uiState.disposableIncome)}"
            )
            SummaryItem(
                title = "Saving Style",
                value = uiState.savingStyle.replaceFirstChar { it.uppercase() }
            )
            SummaryItem(
                title = "Daily Saving Amount",
                value = "${uiState.currencySymbol} ${String.format("%.2f", uiState.recommendedDailySaving)}"
            )
            SummaryItem(
                title = "Monthly Saving",
                value = "${uiState.currencySymbol} ${String.format("%.2f", uiState.recommendedDailySaving * 30)}"
            )
        }
    }
}

@Composable
fun SummaryItem(
    title: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

fun validateStep(step: Int, uiState: OnboardingViewModel.OnboardingUiState): Boolean {
    return when (step) {
        0 -> uiState.name.isNotEmpty() && uiState.age > 0 && uiState.selectedCountry != null
        1 -> uiState.monthlyIncome > 0
        2 -> uiState.savingStyle.isNotEmpty()
        3 -> true
        else -> false
    }
}