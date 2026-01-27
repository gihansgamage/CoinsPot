package com.gihansgamage.coinspot.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gihansgamage.coinspot.data.local.database.entities.DailySaving
import com.gihansgamage.coinspot.data.local.database.entities.SavingGoal
import com.gihansgamage.coinspot.domain.repository.SavingGoalRepository
import com.gihansgamage.coinspot.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class GoalViewModel @Inject constructor(
    private val goalRepository: SavingGoalRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<GoalUiState>(GoalUiState.Loading)
    val uiState: StateFlow<GoalUiState> = _uiState.asStateFlow()

    val allGoals: StateFlow<List<SavingGoal>> = goalRepository.getAllGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeGoals: StateFlow<List<SavingGoal>> = goalRepository.getActiveGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedGoals: StateFlow<List<SavingGoal>> = goalRepository.getCompletedGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalSavings: StateFlow<Double> = goalRepository.getTotalSavings()
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalRemaining: StateFlow<Double> = goalRepository.getTotalRemainingAmount()
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    private val _selectedGoal = MutableStateFlow<SavingGoal?>(null)
    val selectedGoal: StateFlow<SavingGoal?> = _selectedGoal.asStateFlow()

    private val _savingsHistory = MutableStateFlow<List<DailySaving>>(emptyList())
    val savingsHistory: StateFlow<List<DailySaving>> = _savingsHistory.asStateFlow()

    fun selectGoal(goalId: Int) {
        viewModelScope.launch {
            goalRepository.getGoalById(goalId).collect { goal ->
                _selectedGoal.value = goal
            }
            goalRepository.getSavingsHistory(goalId).collect { history ->
                _savingsHistory.value = history
            }
        }
    }

    fun createGoal(
        name: String,
        targetAmount: Double,
        targetDate: LocalDate,
        currency: String,
        currencySymbol: String,
        description: String = ""
    ) {
        viewModelScope.launch {
            _uiState.value = GoalUiState.Loading
            try {
                val goal = SavingGoal(
                    name = name,
                    targetAmount = targetAmount,
                    startDate = LocalDate.now(),
                    targetDate = targetDate,
                    currency = currency,
                    currencySymbol = currencySymbol,
                    description = description
                )
                goalRepository.createGoal(goal)
                _uiState.value = GoalUiState.Success("Goal created successfully!")
            } catch (e: Exception) {
                _uiState.value = GoalUiState.Error(e.message ?: "Failed to create goal")
            }
        }
    }

    fun updateGoal(goal: SavingGoal) {
        viewModelScope.launch {
            _uiState.value = GoalUiState.Loading
            try {
                goalRepository.updateGoal(goal)
                _uiState.value = GoalUiState.Success("Goal updated successfully!")
            } catch (e: Exception) {
                _uiState.value = GoalUiState.Error(e.message ?: "Failed to update goal")
            }
        }
    }

    fun deleteGoal(goal: SavingGoal) {
        viewModelScope.launch {
            _uiState.value = GoalUiState.Loading
            try {
                goalRepository.deleteGoal(goal)
                _uiState.value = GoalUiState.Success("Goal deleted successfully!")
            } catch (e: Exception) {
                _uiState.value = GoalUiState.Error(e.message ?: "Failed to delete goal")
            }
        }
    }

    fun addMoney(goalId: Int, amount: Double, note: String = "") {
        viewModelScope.launch {
            _uiState.value = GoalUiState.Loading
            try {
                val result = goalRepository.addMoneyToGoal(goalId, amount, note)
                result.fold(
                    onSuccess = {
                        _uiState.value = GoalUiState.Success("Money added successfully!")
                    },
                    onFailure = { error ->
                        _uiState.value = GoalUiState.Error(error.message ?: "Failed to add money")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = GoalUiState.Error(e.message ?: "Failed to add money")
            }
        }
    }

    fun withdrawMoney(goalId: Int, amount: Double, note: String = "") {
        viewModelScope.launch {
            _uiState.value = GoalUiState.Loading
            try {
                val result = goalRepository.withdrawMoneyFromGoal(goalId, amount, note)
                result.fold(
                    onSuccess = {
                        _uiState.value = GoalUiState.Success("Money withdrawn successfully!")
                    },
                    onFailure = { error ->
                        _uiState.value = GoalUiState.Error(error.message ?: "Failed to withdraw money")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = GoalUiState.Error(e.message ?: "Failed to withdraw money")
            }
        }
    }

    fun clearUiState() {
        _uiState.value = GoalUiState.Idle
    }
}

sealed class GoalUiState {
    object Idle : GoalUiState()
    object Loading : GoalUiState()
    data class Success(val message: String) : GoalUiState()
    data class Error(val message: String) : GoalUiState()
}