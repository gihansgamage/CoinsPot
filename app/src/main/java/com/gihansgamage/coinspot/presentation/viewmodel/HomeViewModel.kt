package com.gihansgamage.coinspot.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gihansgamage.coinspot.data.local.database.entities.DailySaving
import com.gihansgamage.coinspot.data.local.database.entities.SavingGoal
import com.gihansgamage.coinspot.data.local.preferences.DataStoreManager
import com.gihansgamage.coinspot.domain.repository.GoalRepository
import com.gihansgamage.coinspot.domain.repository.SavingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val goalRepository: GoalRepository,
    private val savingRepository: SavingRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            // Load user info
            dataStoreManager.userName.collect { userName ->
                _uiState.value = _uiState.value.copy(
                    userName = userName,
                    greeting = getGreeting(userName)
                )
            }
        }

        viewModelScope.launch {
            dataStoreManager.currencySymbol.collect { symbol ->
                _uiState.value = _uiState.value.copy(currencySymbol = symbol)
            }
        }

        viewModelScope.launch {
            combine(
                goalRepository.getActiveGoals(),
                goalRepository.getCompletedGoalsCount(),
                goalRepository.getTotalSavedAmount()
            ) { activeGoals, completedCount, totalSaved ->
                _uiState.value = _uiState.value.copy(
                    activeGoals = activeGoals,
                    completedGoalsCount = completedCount,
                    totalSavedAmount = totalSaved ?: 0.0,
                    totalActiveGoals = activeGoals.size,
                    totalSavingsThisMonth = calculateMonthlySavings(activeGoals),
                    isLoading = false
                )
            }.collect {}
        }

        viewModelScope.launch {
            savingRepository.getTodaySavings().collect { todaySavings ->
                _uiState.value = _uiState.value.copy(todaySavings = todaySavings)
            }
        }
    }

    private fun getGreeting(name: String): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val timeOfDay = when (hour) {
            in 0..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            else -> "Good Evening"
        }
        return if (name.isNotEmpty()) "$timeOfDay, $name" else timeOfDay
    }

    private fun calculateMonthlySavings(goals: List<SavingGoal>): Double {
        return goals.sumOf { it.dailySavingAmount } * 30 // Approximate monthly
    }

    data class HomeUiState(
        val userName: String = "",
        val greeting: String = "",
        val currencySymbol: String = "$",
        val activeGoals: List<SavingGoal> = emptyList(),
        val completedGoalsCount: Int = 0,
        val totalSavedAmount: Double = 0.0,
        val todaySavings: List<DailySaving> = emptyList(),
        val totalActiveGoals: Int = 0,
        val totalSavingsThisMonth: Double = 0.0,
        val isLoading: Boolean = true
    )
}