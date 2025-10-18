package com.spbsu_team7.finwise.core.repository

import com.spbsu_team7.finwise.core.model.Advice
import com.spbsu_team7.finwise.core.model.Category
import com.spbsu_team7.finwise.core.model.Status
import com.spbsu_team7.finwise.core.model.Transaction



interface Repository {
     fun getTransactions(): List<Transaction>
     fun getStatus(): Status
     fun getCategories(): List<Category>
     fun getAdvices(): List<Advice>
    fun sendTransaction(transaction: Transaction)
     fun sendCategory(category: Category)
}

