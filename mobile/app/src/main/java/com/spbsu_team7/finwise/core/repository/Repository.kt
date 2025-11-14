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
    val lastMonth: StateFlow<Async<Stat>>
    val last3Months: StateFlow<Async<Stat>>
    val lastYear: StateFlow<Async<Stat>>
    val lastMonthCatExp: StateFlow<Async<Map<Category, Int>>>
    val last3MonthsCatExp: StateFlow<Async<Map<Category, Int>>>
    val lastYearCatExp: StateFlow<Async<Map<Category, Int>>>
    val status: StateFlow<Async<Status>>

    suspend fun getIcons(): List<UserIcon>
    suspend fun getColors(): List<UserColor>
    suspend fun sendTransaction(transaction: TransactionToSend)
    suspend fun sendCategory(category: CategoryToSend)
}

data class Stat(val income: List<Int> = emptyList(), val expense: List<Int> = emptyList())