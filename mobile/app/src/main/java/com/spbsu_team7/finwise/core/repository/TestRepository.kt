package com.spbsu_team7.finwise.core.repository

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiFoodBeverage
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Train
import androidx.compose.ui.graphics.Color
import com.spbsu_team7.finwise.core.model.Advice
import com.spbsu_team7.finwise.core.model.Category
import com.spbsu_team7.finwise.core.model.Transaction
import java.time.Instant
import com.spbsu_team7.finwise.core.model.Status
import java.time.ZoneId

class TestRepository : Repository {
    override suspend fun getTransactions(): List<Transaction> =
        listOf(
            Transaction(
                "Стипендия",
                Instant.now(),
                20000,
                Category("Стипендия", Icons.Default.Money, Color(0xFF009e42))
            ),
            Transaction(
                "Кафе",
                Instant.now(),
                -359,
                Category("Питание", Icons.Default.EmojiFoodBeverage, Color(0xFFffc929))
            ),
            Transaction(
                "Пополнение проездного",
                Instant.now(),
                -1100,
                Category("Транспорт", Icons.Default.Train, Color(0xFF070070))
            )
        )

    override suspend fun getStatus(): Status = Status(20000, 18000, 2000)
    override suspend fun getCategories(): List<Category> =
        listOf(
            Category("Стипендия", Icons.Default.Money, Color(0xFF009e42)),
            Category("Питание", Icons.Default.EmojiFoodBeverage, Color(0xFFffc929)),
            Category("Пополнение проездного", Icons.Default.Train, Color(0xFF070070)),
        )

    override suspend fun getAdvices(): List<Advice> =
        listOf(
            Advice("Меньше надо есть!")
        )

    override suspend fun sendTransaction(transaction: Transaction) {
        TODO("Not yet implemented")
    }

    override suspend fun sendCategory(category: Category) {
        TODO("Not yet implemented")
    }

    override suspend fun getLastMonthsTransaction(months: Int): Pair<List<Int>, List<Int>> {
        val allTransactions = getTransactions().groupBy {
            it.date.atZone(ZoneId.systemDefault()).year * 12 +
                    it.date.atZone(ZoneId.systemDefault()).monthValue
        }
            .mapValues { it.value.map { it.amount } }
        val currentTime = Instant.now().atZone(ZoneId.systemDefault())
        val currentMonth = currentTime.year * 12 + currentTime.monthValue
        val periodTransactions = allTransactions.filter { currentMonth - it.key in 0..months }
        val incomeResults = periodTransactions.mapValues {
            it.value.sumOf { maxOf(it, 0) }
        }
        val expenseResults = periodTransactions.mapValues {
            it.value.sumOf { -minOf(it, 0) }
        }
        return Pair(
            List(months) { incomeResults.getOrDefault(currentMonth + it, 0) },
            List(months) { expenseResults.getOrDefault(currentMonth + it, 0) }
        )
    }

    override suspend fun getCategoriesExpense(): Map<Category, Int> {
        return getTransactions().filter { it.amount < 0 }.groupBy {
            it.category
        }.mapValues { it.value.sumOf { -it.amount } }
    }
}


