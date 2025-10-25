package com.spbsu_team7.finwise.core.repository

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirlineSeatIndividualSuite
import androidx.compose.material.icons.filled.Dining
import androidx.compose.material.icons.filled.EmojiFoodBeverage
import androidx.compose.material.icons.filled.LocalMovies
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.filled.StackedBarChart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.ui.graphics.Color
import com.spbsu_team7.finwise.core.model.Advice
import com.spbsu_team7.finwise.core.model.Category
import com.spbsu_team7.finwise.core.model.CategoryToSend
import com.spbsu_team7.finwise.core.model.Transaction
import java.time.Instant
import com.spbsu_team7.finwise.core.model.Status
import com.spbsu_team7.finwise.core.model.TransactionToSend
import com.spbsu_team7.finwise.core.model.UserColor
import com.spbsu_team7.finwise.core.model.UserIcon
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import kotlin.math.absoluteValue

class TestRepository : Repository {

    val categoryList = mutableListOf(
        CategoryToSend(0, "Стипендия", 3, 0),
        CategoryToSend(1, "Питание", 2, 3),
        CategoryToSend(2, "Пополнение проездного", 1, 8),
        CategoryToSend(3, "Продукты", 7, 6),
    )

    val transactionList = mutableListOf(
        TransactionToSend(
            0,
            "Стипендия",
            Instant.parse("2025-10-02T10:00:00.000Z"),
            20000,
            0
        ),
        TransactionToSend(
            1,
            "Кафе",
            Instant.parse("2025-10-20T10:00:00.000Z"),
            -359,
            1
        ),
        TransactionToSend(
            2,
            "Кафе",
            Instant.parse("2025-10-18T10:00:00.000Z"),
            -330,
            1
        ),
        TransactionToSend(
            3,
            "Кафе",
            Instant.parse("2025-10-15T10:00:00.000Z"),
            -305,
            1
        ),
        TransactionToSend(
            4,
            "Пополнение проездного",
            Instant.parse("2025-10-05T10:00:00.000Z"),
            -1100,
            2
        ),
        TransactionToSend(
            5,
            "Магазин",
            Instant.parse("2025-10-20T10:00:00.000Z"),
            -2000,
            3
        ),
    )

    val adviceList = mutableListOf(
        Advice(
            name = "Оптимизация расходов на продукты",
            description = "За последний месяц траты на продукты выросли на 23%. Ограничьте свои траты в этой категории",
            economy = 2500,
            priority = 2,
            icon = Icons.Default.Savings
            ),
        Advice(
            name = "Слишком часто питаешься вне дома",
            description = "За последний месяц траты на питание выросли на 10%. Ограничьте свои траты в этой категории",
            economy = 3000,
            priority = 1,
            icon = Icons.Default.Savings
        )
    )

    override suspend fun getTransactions(): List<Transaction> {
        val categories = getCategories()
        return transactionList.map { tr -> Transaction(
            id = tr.id,
            name = tr.name,
            date = tr.date,
            amount = tr.amount,
            category = categories.get(tr.categoryId)
        ) }
    }


    override suspend fun getStatus(): Status =
        Status(
            transactionList.filter { it.amount >= 0 }.sumOf { it.amount },
            transactionList.filter { it.amount < 0 }.sumOf { it.amount.absoluteValue },
            transactionList.sumOf { it.amount }
        )


    override suspend fun getCategories(): List<Category> {
        val icons = getIcons()
        val colors = getColors()
        return categoryList.map { cat -> Category(
            id = cat.id,
            name = cat.name,
            icon = icons.get(cat.iconId).imageVector,
            color = colors.get(cat.colorId).color
        ) }
    }

    override suspend fun getAdvices(): List<Advice> = adviceList

    override suspend fun getIcons(): List<UserIcon> = CollectIcons()

    override suspend fun getColors(): List<UserColor> = CollectColors()

    override suspend fun sendTransaction(transaction: TransactionToSend) {
        transactionList.add(transaction.copy(id = transactionList.size))
    }

    override suspend fun sendCategory(category: CategoryToSend) {
        categoryList.add(category.copy(id = categoryList.size))
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

fun CollectIcons() = listOf(
    UserIcon(0, Icons.Default.VideogameAsset),
    UserIcon(1, Icons.Default.Train),
    UserIcon(2, Icons.Default.Dining),
    UserIcon(3, Icons.Default.Money),
    UserIcon(4, Icons.Default.LocalMovies),
    UserIcon(5, Icons.Default.StackedBarChart),
    UserIcon(6, Icons.Default.Star),
    UserIcon(7, Icons.Default.ShoppingBasket),
    UserIcon(8, Icons.Default.AirlineSeatIndividualSuite)
)

fun CollectColors() = listOf(
    UserColor(0, Color(0xFF4CAF50)),
    UserColor(1, Color(0xFF03A9F4)),
    UserColor(2, Color(0xFF9C27B0)),
    UserColor(3, Color(0xFFFF9800)),
    UserColor(4, Color(0xFFF44336)),
    UserColor(5, Color(0xFFE91E63)),
    UserColor(6, Color(0xFF009688)),
    UserColor(7, Color(0xFF3F51B5)),
    UserColor(8, Color(0xFF673AB7)),
    UserColor(9, Color(0xFFCE67D5))
)



