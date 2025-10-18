package com.spbsu_team7.finwise.core.repository

import com.spbsu_team7.finwise.core.model.Advice
import com.spbsu_team7.finwise.core.model.Category
import com.spbsu_team7.finwise.core.model.Status
import com.spbsu_team7.finwise.core.model.Transaction



interface Repository {
    suspend fun getTransactions(): List<Transaction>
    suspend fun getLastMonthsTransaction(months: Int): Pair<List<Int>, List<Int>>
    suspend fun getLastMonthsIncome(months: Int): List<Int> = getLastMonthsTransaction(months).first
    suspend fun getLastMonthsExpense(months: Int): List<Int> = getLastMonthsTransaction(months).second
    suspend fun getCategoriesExpense(): Map<Category, Int>
    suspend fun getStatus(): Status
    suspend fun getCategories(): List<Category>
    suspend fun getAdvices(): List<Advice>
    suspend fun sendTransaction(transaction: Transaction)
    suspend fun sendCategory(category: Category)
}

