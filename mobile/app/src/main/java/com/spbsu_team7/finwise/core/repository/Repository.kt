package com.spbsu_team7.finwise.core.repository

import com.spbsu_team7.finwise.core.model.Advice
import com.spbsu_team7.finwise.core.model.Category
import com.spbsu_team7.finwise.core.model.Status
import com.spbsu_team7.finwise.core.model.Transaction



interface Repository {
    suspend fun getTransactions(): List<Transaction>
    suspend fun getStatus(): Status
    suspend fun getCategories(): List<Category>
    suspend fun getAdvices(): List<Advice>
}

