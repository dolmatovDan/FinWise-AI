package com.spbsu_team7.finwise.app

import android.net.http.HttpException
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spbsu_team7.finwise.core.model.Advice
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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

data class Events(
    val sendCategory: (CategoryToSend) -> Unit,
    val sendTransaction: (TransactionToSend) -> Unit,
    val onRetry: () -> Unit
)


sealed interface UiState {
    data class Success(
        val transactions: List<Transaction>,
        val status: Status,
        val categories: List<Category>,
        val advices: List<Advice>,
        val chartsData: ChartsData,
        val icons: List<UserIcon>,
        val colors: List<UserColor>
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

    private fun sendTransaction(transaction: TransactionToSend) {
        viewModelScope.launch {
            try {
                repository.sendTransaction(transaction)
                updateMain()
            } catch (e: Exception) {
                Log.e("IO", "${e.message} in sendNewTransaction")
            }
        }
    }

    private fun sendCategory(category: CategoryToSend) {
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
                        advices = repository.getAdvices(),
                        chartsData = ChartsData(
                            repository.getLastMonthsTransaction(6),
                            repository.getCategoriesExpense()
                        ),
                        icons = repository.getIcons(),
                        colors = repository.getColors()
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

    fun getEvents() = Events(
        sendCategory = ::sendCategory,
        sendTransaction = ::sendTransaction,
        onRetry = ::updateMain
    )

    @Composable
    fun getState() = uiState.collectAsState().value
}