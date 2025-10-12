package com.spbsu_team7.finwise.app

import android.net.http.HttpException
import android.util.Log
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
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

data class Events(
    val onRetry: () -> Unit
)

interface UiState {
    data class Success(
        val transactions: List<Transaction>,
        val status: Status,
        val categories: List<Category>,
        val advices: List<Advice>
    ) : UiState

    object Error : UiState

    object Loading : UiState
}

@HiltViewModel
class ViewModel @Inject constructor (
    private val repository: Repository
) : ViewModel() {
    private var uiState: UiState by mutableStateOf(UiState.Loading)

    init {
        updateMain()
    }

    private fun updateMain() {
        viewModelScope.launch {
            uiState =
                try {
                    UiState.Success(
                        transactions = repository.getTransactions(),
                        status = repository.getStatus(),
                        categories = repository.getCategories(),
                        advices = repository.getAdvices()
                    )
                } catch (e: IOException) {
                    Log.e("IO", "${e.message} in updateMain")
                    UiState.Error
                } catch (e: HttpException) {
                    Log.e("HTTP", "${e.message} in updateMain")
                    UiState.Error
                }
        }
    }

    fun getState() = uiState
}