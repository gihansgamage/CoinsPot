package com.gihansgamage.coinspot.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gihansgamage.coinspot.data.local.database.entities.SavingGoal
import com.gihansgamage.coinspot.data.local.preferences.DataStoreManager
import com.gihansgamage.coinspot.domain.repository.SavingGoalRepository
import com.gihansgamage.coinspot.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val savingGoalRepository: SavingGoalRepository,
    private val userRepository: UserRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    // User info
    val userName: StateFlow<String> = dataStoreManager.userName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val currency: StateFlow<String> = dataStoreManager.userCurrency
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "USD")

    val currencySymbol: StateFlow<String> = dataStoreManager.currencySymbol
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "$")

    // Goals data
    val activeGoals: StateFlow<List<SavingGoal>> = savingGoalRepository.getActiveGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedGoals: StateFlow<List<SavingGoal>> = savingGoalRepository.getCompletedGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalSavings: StateFlow<Double> = savingGoalRepository.getTotalSavings()
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalRemaining: StateFlow<Double> = savingGoalRepository.getTotalRemainingAmount()
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val completedGoalsCount: StateFlow<Int> = savingGoalRepository.getCompletedGoalsCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                _uiState.value = HomeUiState.Success
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Failed to load data")
            }
        }
    }

    fun refreshData() {
        loadData()
    }

    // Quick add money to a goal
    fun quickAddMoney(goalId: Int, amount: Double) {
        viewModelScope.launch {
            savingGoalRepository.addMoneyToGoal(goalId, amount, "Quick add")
        }
    }
}

sealed class HomeUiState {
    object Loading : HomeUiState()
    object Success : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}