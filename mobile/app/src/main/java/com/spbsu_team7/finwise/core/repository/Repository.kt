package com.spbsu_team7.finwise.core.repository

import com.spbsu_team7.finwise.core.model.Advice
import com.spbsu_team7.finwise.core.model.Async
import com.spbsu_team7.finwise.core.model.Category
import com.spbsu_team7.finwise.core.model.CategoryToSend
import com.spbsu_team7.finwise.core.model.Status
import com.spbsu_team7.finwise.core.model.Transaction
import com.spbsu_team7.finwise.core.model.TransactionToSend
import com.spbsu_team7.finwise.core.model.UserColor
import com.spbsu_team7.finwise.core.model.UserIcon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


interface Repository {
    val transactions: StateFlow<Async<List<Transaction>>>
    val categories: StateFlow<Async<List<Category>>>
    val advices: StateFlow<Async<List<Advice>>>
    suspend fun getStatus(): Async<Status>
    suspend fun getLastMonth(): Async<Stat>
    suspend fun getLast3Months(): Async<Stat>
    suspend fun getLastYear(): Async<Stat>
    suspend fun getCategoriesExpense(): Async<Map<Category, Int>>
    suspend fun getIcons(): List<UserIcon>
    suspend fun getColors(): List<UserColor>
    suspend fun sendTransaction(transaction: TransactionToSend)
    suspend fun sendCategory(category: CategoryToSend)
}

interface AuthRepository {

}


data class Stat(val income: List<Int>, val expense: List<Int>)