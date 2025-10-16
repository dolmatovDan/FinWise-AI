package com.spbsu_team7.finwise.app

import android.net.http.HttpException
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spbsu_team7.finwise.core.model.Advice
import com.spbsu_team7.finwise.core.model.Category
import com.spbsu_team7.finwise.core.model.Status
import com.spbsu_team7.finwise.core.model.Transaction
import com.spbsu_team7.finwise.core.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

data class Events(
    val sendNewCategory: (Category) -> Unit,
    val sendNewTransaction: (Transaction) -> Unit,
    val onRetry: () -> Unit
)

sealed interface UiState {
    data class Success(
        val transactions: List<Transaction>,
        val status: Status,
        val categories: List<Category>,
        val advices: List<Advice>
    ) : UiState

    data class Error(
        val error: String
    ) : UiState

    object Loading : UiState
}

@HiltViewModel
class ViewModel @Inject constructor (
    private val repository: Repository
) : ViewModel() {
    private var _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        updateMain()
    }

    private fun sendTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                repository.sendTransaction(transaction)
                updateMain()
            } catch (e: Exception) {
                Log.e("IO", "${e.message} in sendNewTransaction")
            }
        }
    }

    private fun sendCategory(category: Category) {
        viewModelScope.launch {
            try {
                repository.sendCategory(category)
                updateMain()
            } catch (e: Exception) {
                Log.e("IO", "${e.message} in sendNewCategory")
            }
        }
    }

    private fun updateMain() {
        viewModelScope.launch {
            _uiState.value =
                try {
                    UiState.Success(
                        transactions = repository.getTransactions(),
                        status = repository.getStatus(),
                        categories = repository.getCategories(),
                        advices = repository.getAdvices()
                    )
                } catch (e: IOException) {
                    Log.e("IO", "${e.message} in updateMain")
                    UiState.Error("Ошибка при обновлении, проверьте соединение")
                } catch (e: HttpException) {
                    Log.e("HTTP", "${e.message} in updateMain")
                    UiState.Error("Ошибка при обновлении, проверьте соединение")
                }
        }
    }

    fun getEvents() =
        Events(
            sendNewCategory = ::sendCategory,
            sendNewTransaction = ::sendTransaction,
            onRetry = ::updateMain
        )

    @Composable
    fun getState() = uiState.collectAsState().value
}