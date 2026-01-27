package com.gihansgamage.coinspot.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gihansgamage.coinspot.data.local.database.entities.UserProfile
import com.gihansgamage.coinspot.data.local.preferences.DataStoreManager
import com.gihansgamage.coinspot.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<OnboardingUiState>(OnboardingUiState.Idle)
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun completeOnboarding(
        name: String,
        country: String,
        currency: String,
        currencySymbol: String,
        monthlyIncome: Double = 0.0,
        monthlyExpenses: Double = 0.0
    ) {
        viewModelScope.launch {
            _uiState.value = OnboardingUiState.Loading
            try {
                // Save user profile
                val userProfile = UserProfile(
                    name = name,
                    country = country,
                    currency = currency,
                    currencySymbol = currencySymbol,
                    monthlyIncome = monthlyIncome,
                    monthlyExpenses = monthlyExpenses
                )
                userRepository.createUserProfile(userProfile)

                // Save to DataStore
                dataStoreManager.saveUserName(name)
                dataStoreManager.saveCountryAndCurrency(country, currency, currencySymbol)
                dataStoreManager.setOnboardingComplete(true)

                _uiState.value = OnboardingUiState.Success
            } catch (e: Exception) {
                _uiState.value = OnboardingUiState.Error(e.message ?: "Failed to complete onboarding")
            }
        }
    }

    fun clearUiState() {
        _uiState.value = OnboardingUiState.Idle
    }
}

sealed class OnboardingUiState {
    object Idle : OnboardingUiState()
    object Loading : OnboardingUiState()
    object Success : OnboardingUiState()
    data class Error(val message: String) : OnboardingUiState()
}