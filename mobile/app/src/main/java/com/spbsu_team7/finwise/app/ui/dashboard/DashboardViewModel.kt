package com.spbsu_team7.finwise.app.ui.dashboard

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spbsu_team7.finwise.app.ui.dashboard.FilterTypes.LAST_MONTH
import com.spbsu_team7.finwise.app.ui.dashboard.FilterTypes.LAST_3MONTHS
import com.spbsu_team7.finwise.app.ui.dashboard.FilterTypes.LAST_YEAR
import com.spbsu_team7.finwise.app.Events
import com.spbsu_team7.finwise.app.UiState
import com.spbsu_team7.finwise.app.ui.util.WhileUiSubscribed
import com.spbsu_team7.finwise.core.model.Advice
import com.spbsu_team7.finwise.core.model.Async
import com.spbsu_team7.finwise.core.model.Category
import com.spbsu_team7.finwise.core.model.CategoryToSend
import com.spbsu_team7.finwise.core.model.ChartsData
import com.spbsu_team7.finwise.core.model.Status
import com.spbsu_team7.finwise.core.model.Transaction
import com.spbsu_team7.finwise.core.model.TransactionToSend
import com.spbsu_team7.finwise.core.model.UserColor
import com.spbsu_team7.finwise.core.model.UserIcon
import com.spbsu_team7.finwise.core.repository.Repository
import com.spbsu_team7.finwise.core.repository.Stat
import com.spbsu_team7.finwise.core.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject




data class DashboardEvents(
    val onRetry: () -> Unit
)

sealed interface DashboardUiState {
    data class Success(
        val status: Async<Status>,
        val balanceData: Async<Stat>,
        val categoryData: Async<Map<Category, Int>>,
        val filter: FilterTypes
    ) : DashboardUiState

    data class Error(
        val error: String
    ) : DashboardUiState

    object Loading : DashboardUiState
}

@HiltViewModel
class DashboardViewModel @Inject constructor (
    private val sessionManager: SessionManager,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val filterType =  savedStateHandle.getStateFlow(TRANSACTIONS_FILTER_SAVED_STATE_KEY, LAST_3MONTHS)
    private val repository: Repository by lazy {
        sessionManager.getRepository()
    }
    private val filteredIncomeExpense =
        combine(
            repository.lastMonth,
            repository.last3Months,
            repository.lastYear,
            filterType
        ) {
            month, months3, year, type ->
            when (type) {
                LAST_MONTH -> month
                LAST_3MONTHS -> months3
                LAST_YEAR -> year
            }
        }

    private val filteredCategories =
        combine(
            repository.lastMonthCatExp,
            repository.last3MonthsCatExp,
            repository.lastYearCatExp,
            filterType
        ) {
                month, months3, year, type ->
            when (type) {
                LAST_MONTH -> month
                LAST_3MONTHS -> month
                LAST_YEAR -> month
            }
        }
    val uiState = combine(filteredCategories, filteredIncomeExpense, repository.status)
        { categoryData, balanceData, status ->
                DashboardUiState.Success(
                    status = status,
                    balanceData = balanceData,
                    categoryData = categoryData,
                    filter = filterType.value
                )
        }.stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = DashboardUiState.Loading
        )

    fun changeFilter(requestType: FilterTypes) {
        savedStateHandle[TRANSACTIONS_FILTER_SAVED_STATE_KEY] = requestType
    }

    fun getEvents() = DashboardEvents(
        onRetry = {}
    )

    @Composable
    fun getState() = uiState.collectAsState().value

}

val TRANSACTIONS_FILTER_SAVED_STATE_KEY = "TRANSACTIONS_FILTER_SAVED_STATE_KEY"