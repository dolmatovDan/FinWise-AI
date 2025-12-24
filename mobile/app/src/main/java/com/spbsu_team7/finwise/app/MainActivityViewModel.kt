package com.spbsu_team7.finwise.app

import android.net.http.HttpException
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import com.spbsu_team7.finwise.core.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
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
        val categories: List<Category>,
        val advices: List<Advice>,
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
    private val sessionManager: SessionManager,
    private val repository: Repository
) : ViewModel() {

//    private val repository: Repository by lazy {
//        sessionManager.getRepository()
//    }

    val uiState =
        combine(repository.transactions, repository.categories, repository.advices){
            tr, cat, adv ->

            val error = listOf(tr, cat, adv).filterIsInstance<Async.Error>().firstOrNull()?.errorMessage
            val isLoading = listOf(tr, cat, adv).filterIsInstance<Async.Loading>().isNotEmpty()
            if (error != null) {
                UiState.Error(error)
            } else if (isLoading) {
                UiState.Loading
            } else {
                UiState.Success(
                    transactions = (tr as Async.Success).data,
                    categories = (cat as Async.Success).data,
                    advices = (adv as Async.Success).data,
                    icons = repository.getIcons(),
                    colors = repository.getColors()
                )
            }

        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Loading
        )

    private fun sendTransaction(transaction: TransactionToSend) {
        viewModelScope.launch {
            try {
                repository.sendTransaction(transaction)
            } catch (e: Exception) {
                Log.e("IO", "${e.message} in sendNewTransaction")
            }
        }
    }

    private fun sendCategory(category: CategoryToSend) {
        viewModelScope.launch {
            try {
                repository.sendCategory(category)

            } catch (e: Exception) {
                Log.e("IO", "${e.message} in sendNewCategory")
            }
        }
    }

    fun getEvents() = Events(
        sendCategory = ::sendCategory,
        sendTransaction = ::sendTransaction,
        onRetry = {}
    )

    @Composable
    fun getState() = uiState.collectAsState().value

}