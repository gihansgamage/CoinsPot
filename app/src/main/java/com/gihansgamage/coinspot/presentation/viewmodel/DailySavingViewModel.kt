package com.gihansgamage.coinspot.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gihansgamage.coinspot.data.local.database.entities.DailySaving
import com.gihansgamage.coinspot.data.local.database.entities.TransactionType
import com.gihansgamage.coinspot.domain.repository.SavingGoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DailySavingViewModel @Inject constructor(
    private val goalRepository: SavingGoalRepository
) : ViewModel() {

    private val _selectedGoalId = MutableStateFlow<Int?>(null)
    val selectedGoalId: StateFlow<Int?> = _selectedGoalId.asStateFlow()

    private val _uiState = MutableStateFlow<DailySavingUiState>(DailySavingUiState.Idle)
    val uiState: StateFlow<DailySavingUiState> = _uiState.asStateFlow()

    private val _savingsHistory = MutableStateFlow<List<DailySaving>>(emptyList())
    val savingsHistory: StateFlow<List<DailySaving>> = _savingsHistory.asStateFlow()

    private val _averageDailySaving = MutableStateFlow(0.0)
    val averageDailySaving: StateFlow<Double> = _averageDailySaving.asStateFlow()

    private val _savingStreak = MutableStateFlow(0)
    val savingStreak: StateFlow<Int> = _savingStreak.asStateFlow()

    private val _totalSaved = MutableStateFlow(0.0)
    val totalSaved: StateFlow<Double> = _totalSaved.asStateFlow()

    fun setSelectedGoal(goalId: Int) {
        _selectedGoalId.value = goalId
        loadGoalData(goalId)
    }

    private fun loadGoalData(goalId: Int) {
        viewModelScope.launch {
            // Load savings history
            goalRepository.getSavingsHistory(goalId).collect { history ->
                _savingsHistory.value = history
            }

            // Load average daily saving
            goalRepository.getAverageDailySaving(goalId).collect { avg ->
                _averageDailySaving.value = avg ?: 0.0
            }

            // Load saving streak
            goalRepository.getSavingStreak(goalId).collect { streak ->
                _savingStreak.value = streak
            }

            // Load total saved
            goalRepository.getTotalSavedForGoal(goalId).collect { total ->
                _totalSaved.value = total ?: 0.0
            }
        }
    }

    fun addMoney(goalId: Int, amount: Double, note: String = "") {
        if (amount <= 0) {
            _uiState.value = DailySavingUiState.Error("Amount must be greater than 0")
            return
        }

        viewModelScope.launch {
            _uiState.value = DailySavingUiState.Loading
            try {
                val result = goalRepository.addMoneyToGoal(goalId, amount, note)
                result.fold(
                    onSuccess = {
                        _uiState.value = DailySavingUiState.Success("Money added successfully! 💰")
                        loadGoalData(goalId)
                    },
                    onFailure = { error ->
                        _uiState.value = DailySavingUiState.Error(
                            error.message ?: "Failed to add money"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = DailySavingUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun withdrawMoney(goalId: Int, amount: Double, note: String = "") {
        if (amount <= 0) {
            _uiState.value = DailySavingUiState.Error("Amount must be greater than 0")
            return
        }

        viewModelScope.launch {
            _uiState.value = DailySavingUiState.Loading
            try {
                val result = goalRepository.withdrawMoneyFromGoal(goalId, amount, note)
                result.fold(
                    onSuccess = {
                        _uiState.value = DailySavingUiState.Success("Money withdrawn successfully")
                        loadGoalData(goalId)
                    },
                    onFailure = { error ->
                        _uiState.value = DailySavingUiState.Error(
                            error.message ?: "Failed to withdraw money"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = DailySavingUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun getSavingsTrendData(): List<DailySavingTrend> {
        val trends = mutableMapOf<LocalDate, Double>()

        savingsHistory.value
            .filter { it.transactionType == TransactionType.DEPOSIT }
            .forEach { saving ->
                trends[saving.date] = (trends[saving.date] ?: 0.0) + saving.amount
            }

        return trends
            .map { (date, amount) -> DailySavingTrend(date, amount) }
            .sortedBy { it.date }
    }

    fun getWeeklySavingsData(): Map<String, Double> {
        val weeklyData = mutableMapOf<String, Double>()
        val now = LocalDate.now()

        repeat(7) { day ->
            val date = now.minusDays(day.toLong())
            val dayOfWeek = date.dayOfWeek.toString().take(3)
            val amount = savingsHistory.value
                .filter { it.date == date && it.transactionType == TransactionType.DEPOSIT }
                .sumOf { it.amount }
            weeklyData[dayOfWeek] = amount
        }

        return weeklyData.toSortedMap()
    }

    fun getMonthlySavingsData(): Map<String, Double> {
        val monthlyData = mutableMapOf<String, Double>()
        val now = LocalDate.now()

        repeat(30) { day ->
            val date = now.minusDays(day.toLong())
            val dayStr = date.dayOfMonth.toString()
            val amount = savingsHistory.value
                .filter { it.date == date && it.transactionType == TransactionType.DEPOSIT }
                .sumOf { it.amount }
            monthlyData[dayStr] = amount
        }

        return monthlyData.toSortedMap()
    }

    fun clearUiState() {
        _uiState.value = DailySavingUiState.Idle
    }
}

sealed class DailySavingUiState {
    object Idle : DailySavingUiState()
    object Loading : DailySavingUiState()
    data class Success(val message: String) : DailySavingUiState()
    data class Error(val message: String) : DailySavingUiState()
}

data class DailySavingTrend(
    val date: LocalDate,
    val amount: Double
)