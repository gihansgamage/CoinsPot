package com.gihansgamage.coinspot.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gihansgamage.coinspot.data.local.database.entities.UserProfile
import com.gihansgamage.coinspot.data.local.preferences.DataStoreManager
import com.gihansgamage.coinspot.data.model.Country
import com.gihansgamage.coinspot.data.model.CountryData
import com.gihansgamage.coinspot.domain.repository.UserRepository
import com.gihansgamage.coinspot.domain.usecase.CalculateDailySavingUseCase
import com.gihansgamage.coinspot.domain.usecase.CalculateDisposableIncomeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val dataStoreManager: DataStoreManager,
    private val calculateDisposableIncome: CalculateDisposableIncomeUseCase,
    private val calculateDailySaving: CalculateDailySavingUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun updateAge(age: Int) {
        _uiState.value = _uiState.value.copy(age = age)
    }

    fun updateCountry(country: Country) {
        _uiState.value = _uiState.value.copy(
            selectedCountry = country,
            currency = country.currency,
            currencySymbol = country.currencySymbol
        )
    }

    fun updateIncome(income: Double) {
        _uiState.value = _uiState.value.copy(monthlyIncome = income)
        recalculateSavings()
    }

    fun updateLivingCosts(costs: Double) {
        _uiState.value = _uiState.value.copy(livingCosts = costs)
        recalculateSavings()
    }

    fun updateFoodExpenses(expenses: Double) {
        _uiState.value = _uiState.value.copy(foodExpenses = expenses)
        recalculateSavings()
    }

    fun updateOtherCosts(costs: Double) {
        _uiState.value = _uiState.value.copy(otherCosts = costs)
        recalculateSavings()
    }

    fun updateSavingStyle(style: String) {
        _uiState.value = _uiState.value.copy(savingStyle = style)
        recalculateSavings()
    }

    private fun recalculateSavings() {
        val state = _uiState.value
        val disposableIncome = calculateDisposableIncome(
            state.monthlyIncome,
            state.livingCosts,
            state.foodExpenses,
            state.otherCosts
        )

        val dailySaving = calculateDailySaving(
            disposableIncome,
            state.savingStyle
        )

        _uiState.value = state.copy(
            disposableIncome = disposableIncome,
            recommendedDailySaving = dailySaving
        )
    }

    fun saveUserProfile() {
        viewModelScope.launch {
            val state = _uiState.value

            // Save to DataStore
            dataStoreManager.saveUserName(state.name)
            dataStoreManager.saveCountryAndCurrency(
                state.selectedCountry?.name ?: "",
                state.currency,
                state.currencySymbol
            )
            dataStoreManager.setOnboardingComplete(true)

            // Save to Database
            val userProfile = UserProfile(
                age = state.age,
                monthlyIncome = state.monthlyIncome,
                livingCosts = state.livingCosts,
                foodExpenses = state.foodExpenses,
                otherCosts = state.otherCosts,
                currency = state.currency,
                country = state.selectedCountry?.name ?: "",
                savingStyle = state.savingStyle,
                dailySavingAmount = state.recommendedDailySaving,
                disposableIncome = state.disposableIncome
            )
            userRepository.saveUserProfile(userProfile)
        }
    }

    data class OnboardingUiState(
        val name: String = "",
        val age: Int = 25,
        val selectedCountry: Country? = null,
        val currency: String = "USD",
        val currencySymbol: String = "$",
        val monthlyIncome: Double = 0.0,
        val livingCosts: Double = 0.0,
        val foodExpenses: Double = 0.0,
        val otherCosts: Double = 0.0,
        val savingStyle: String = "balanced",
        val disposableIncome: Double = 0.0,
        val recommendedDailySaving: Double = 0.0
    )
}