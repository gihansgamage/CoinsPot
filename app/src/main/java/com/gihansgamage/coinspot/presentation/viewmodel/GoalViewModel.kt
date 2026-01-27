package com.gihansgamage.coinspot.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gihansgamage.coinspot.data.local.database.entities.SavingGoal
import com.gihansgamage.coinspot.data.local.preferences.DataStoreManager
import com.gihansgamage.coinspot.domain.repository.GoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class GoalViewModel @Inject constructor(
    private val goalRepository: GoalRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalUiState())
    val uiState: StateFlow<GoalUiState> = _uiState

    init {
        loadCurrencyInfo()
    }

    private fun loadCurrencyInfo() {
        viewModelScope.launch {
            val currency = dataStoreManager.userCurrency.first()
            val symbol = dataStoreManager.currencySymbol.first()
            _uiState.value = _uiState.value.copy(
                currency = currency,
                currencySymbol = symbol
            )
        }
    }

    fun updateGoalName(name: String) {
        _uiState.value = _uiState.value.copy(goalName = name)
    }

    fun updateTargetPrice(price: Double) {
        _uiState.value = _uiState.value.copy(targetPrice = price)
        calculateEstimatedDays()
    }

    fun updateDailySavingAmount(amount: Double) {
        _uiState.value = _uiState.value.copy(dailySavingAmount = amount)
        calculateEstimatedDays()
    }

    private fun calculateEstimatedDays() {
        val state = _uiState.value
        if (state.targetPrice > 0 && state.dailySavingAmount > 0) {
            val days = (state.targetPrice / state.dailySavingAmount).toInt()
            _uiState.value = state.copy(estimatedDays = days)
        }
    }

    fun createGoal() {
        viewModelScope.launch {
            val state = _uiState.value

            val goal = SavingGoal(
                name = state.goalName,
                targetPrice = state.targetPrice,
                dailySavingAmount = state.dailySavingAmount,
                currency = state.currency,
                createdAt = Date(),
                isActive = true,
                isCompleted = false
            )

            goalRepository.createGoal(goal)
            _uiState.value = state.copy(isGoalCreated = true)
        }
    }

    data class GoalUiState(
        val goalName: String = "",
        val targetPrice: Double = 0.0,
        val dailySavingAmount: Double = 0.0,
        val estimatedDays: Int = 0,
        val currency: String = "USD",
        val currencySymbol: String = "$",
        val isGoalCreated: Boolean = false
    )
}