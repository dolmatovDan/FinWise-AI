package com.spbsu_team7.finwise.core.repository

import com.spbsu_team7.finwise.core.model.Advice
import com.spbsu_team7.finwise.core.model.Category
import com.spbsu_team7.finwise.core.model.CategoryToSend
import com.spbsu_team7.finwise.core.model.Status
import com.spbsu_team7.finwise.core.model.Transaction
import com.spbsu_team7.finwise.core.model.TransactionToSend
import com.spbsu_team7.finwise.core.model.UserColor
import com.spbsu_team7.finwise.core.model.UserIcon


interface Repository {
    suspend fun getTransactions(): List<Transaction>
    suspend fun getLastMonthsTransaction(months: Int): Pair<List<Int>, List<Int>>
    suspend fun getLastMonthsIncome(months: Int): List<Int> = getLastMonthsTransaction(months).first
    suspend fun getLastMonthsExpense(months: Int): List<Int> = getLastMonthsTransaction(months).second
    suspend fun getCategoriesExpense(): Map<Category, Int>
    suspend fun getStatus(): Status
    suspend fun getCategories(): List<Category>
    suspend fun getAdvices(): List<Advice>
    suspend fun getIcons(): List<UserIcon>
    suspend fun getColors(): List<UserColor>
    suspend fun sendTransaction(transaction: TransactionToSend)
    suspend fun sendCategory(category: CategoryToSend)
}

