package com.spbsu_team7.finwise.app.ui.dashboard

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
        val status: Status,
        val chartsData: ChartsData,
        val filter: FilterTypes
    ) : DashboardUiState

    data class Error(
        val error: String
    ) : DashboardUiState

    object Loading : DashboardUiState
}

@HiltViewModel
class DashboardViewModel @Inject constructor (
    private val repository: Repository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val filterType =  savedStateHandle.getStateFlow(TRANSACTIONS_FILTER_SAVED_STATE_KEY, LAST_3MONTHS)
    val filteredTransactions = combine(repository.transactions, filterType) {
        transactions, type ->
        if (transactions is Async.Loading) return@combine Async.Loading
        if (transactions is Async.Error) return@combine Async.Error(transactions.errorMessage)
        return@combine when (type) {
            LAST_MONTH -> Async.Success(ChartsData(
                (repository.getLastMonth() as Async.Success).data,
                (repository.getCategoriesExpense() as Async.Success).data
            ))
            LAST_3MONTHS -> Async.Success(ChartsData(
                (repository.getLast3Months() as Async.Success).data,
                (repository.getCategoriesExpense() as Async.Success).data
            ))
            LAST_YEAR -> Async.Success(ChartsData(
                (repository.getLastYear() as Async.Success).data,
                (repository.getCategoriesExpense() as Async.Success).data
            ))
        }
    }
    val uiState =
        filteredTransactions.map {
                tr ->
            if (tr is Async.Error) {
                DashboardUiState.Error(tr.errorMessage)
            } else if (tr is Async.Loading) {
                DashboardUiState.Loading
            } else {
                DashboardUiState.Success(
                    status = (repository.getStatus() as Async.Success).data,
                    chartsData = (tr as Async.Success).data,
                    filter = filterType.value
                )
            }

        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
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