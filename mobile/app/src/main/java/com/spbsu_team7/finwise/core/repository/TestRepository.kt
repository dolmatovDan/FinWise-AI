package com.spbsu_team7.finwise.core.repository

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Money
import androidx.compose.ui.graphics.Color
import com.spbsu_team7.finwise.core.model.Advice
import com.spbsu_team7.finwise.core.model.Category
import com.spbsu_team7.finwise.core.model.Transaction
import java.time.Instant
import com.spbsu_team7.finwise.core.model.Status

class TestRepository : Repository {
    override suspend fun getTransactions(): List<Transaction> =
        listOf(
            Transaction(
                Instant.now(),
                10000,
                Category("Стипендия", Icons.Default.Money, Color(0xFFFFFFFF)),
                ""
            )
        )
    override suspend fun getStatus(): Status = Status(20000, 18000, 2000)
    override suspend fun getCategories(): List<Category> =
        listOf(
            Category("Стипендия", Icons.Default.Money, Color(0xFFFFFFFF)),
        )
    override suspend fun getAdvices(): List<Advice> =
        listOf(
            Advice("Меньше надо есть!")
        )
}


