package com.spbsu_team7.finwise.core.repository

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirlineSeatIndividualSuite
import androidx.compose.material.icons.filled.Dining
import androidx.compose.material.icons.filled.EmojiFoodBeverage
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.LocalMovies
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.filled.StackedBarChart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.ui.graphics.Color
import com.spbsu_team7.finwise.app.ui.util.WhileUiSubscribed
import com.spbsu_team7.finwise.core.model.Advice
import com.spbsu_team7.finwise.core.model.Async
import com.spbsu_team7.finwise.core.model.Category
import com.spbsu_team7.finwise.core.model.CategoryToSend
import com.spbsu_team7.finwise.core.model.Transaction
import java.time.Instant
import com.spbsu_team7.finwise.core.model.Status
import com.spbsu_team7.finwise.core.model.TransactionToSend
import com.spbsu_team7.finwise.core.model.UserColor
import com.spbsu_team7.finwise.core.model.UserIcon
import com.spbsu_team7.finwise.core.repository.di.ApplicationScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import javax.inject.Inject
import kotlin.collections.sortedBy
import kotlin.math.absoluteValue

class TestRepository @Inject constructor(
    @ApplicationScope private val coroutineScope: CoroutineScope
) : Repository {

    val categoryList = mutableListOf(
        CategoryToSend(0, "Стипендия", 3, 0),
        CategoryToSend(1, "Питание", 2, 3),
        CategoryToSend(2, "Пополнение проездного", 1, 8),
        CategoryToSend(3, "Продукты", 7, 6),
        CategoryToSend(4, "Жильё", 9, 7),
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
        TransactionToSend(
            6,
            "Оплата общежития",
            Instant.parse("2025-10-25T10:00:00.000Z"),
            -10000,
            4
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

    private val _transactions = MutableStateFlow<Async<List<Transaction>>>(Async.Success(emptyList<Transaction>()))
    private val _categories = MutableStateFlow<Async<List<Category>>>(Async.Success(emptyList<Category>()))
    private val _advices = MutableStateFlow<Async<List<Advice>>>(Async.Success(emptyList<Advice>()))
    private val _lastMonth = MutableStateFlow<Async<Stat>>(Async.Success(Stat()))
    private val _last3Months = MutableStateFlow<Async<Stat>>(Async.Success(Stat()))
    private val _lastYear = MutableStateFlow<Async<Stat>>(Async.Success(Stat()))
    private val _lastMonthCatExp = MutableStateFlow<Async<Map<Category, Int>>>(Async.Success(emptyMap()))
    private val _last3MonthsCatExp = MutableStateFlow<Async<Map<Category, Int>>>(Async.Success(emptyMap()))
    private val _lastYearCatExp = MutableStateFlow<Async<Map<Category, Int>>>(Async.Success(emptyMap()))

    override val transactions: StateFlow<Async<List<Transaction>>> = _transactions
    override val categories: StateFlow<Async<List<Category>>> = _categories
    override val advices: StateFlow<Async<List<Advice>>> = _advices
    override val lastMonth: StateFlow<Async<Stat>> = _lastMonth
    override val last3Months: StateFlow<Async<Stat>> = _last3Months
    override val lastYear: StateFlow<Async<Stat>> = _lastYear
    override val lastMonthCatExp: StateFlow<Async<Map<Category, Int>>> = _lastMonthCatExp
    override val last3MonthsCatExp: StateFlow<Async<Map<Category, Int>>> = _last3MonthsCatExp
    override val lastYearCatExp: StateFlow<Async<Map<Category, Int>>> = _lastYearCatExp
    override val status: StateFlow<Async<Status>> = transactions.map {
        transactions ->
        when (transactions) {
            is Async.Error -> Async.Error(transactions.errorMessage)
            is Async.Loading -> Async.Loading
            else -> Async.Success(Status(
                (transactions as Async.Success<List<Transaction>>).data.filter { it.amount >= 0 }.sumOf { it.amount },
                (transactions as Async.Success<List<Transaction>>).data.filter { it.amount < 0 }.sumOf { it.amount.absoluteValue },
                (transactions as Async.Success<List<Transaction>>).data.sumOf { it.amount }
            )
            )
        }
    }.stateIn(scope = coroutineScope, started = WhileUiSubscribed, initialValue = Async.Loading)

    init {
        coroutineScope.launch {
            refreshCategories()
            refreshTransactions()
            refreshAdvices()
            refreshLastMonth()
            refreshLast3Months()
            refreshLastYear()
            refreshCategoriesExpense()
        }
    }

    suspend fun refreshCategories() {
        _categories.value = try {
            val icons = CollectIcons()
            val colors = CollectColors()
            Async.Success(categoryList.map { cat ->
                    Category(
                        id = cat.id,
                        name = cat.name,
                        icon = icons.get(cat.iconId).imageVector,
                        color = colors.get(cat.colorId).color
                    )
                }
            )
        } catch(e: Exception) {
            Async.Error(e.message ?: "")
        }
    }

    suspend fun refreshTransactions() {
        _transactions.value = when (categories.value) {
            is Async.Error -> Async.Error((categories.value as Async.Error).errorMessage)
            is Async.Loading -> Async.Loading
            else -> try {
                Async.Success(transactionList.map { tr ->
                    Transaction(
                        id = tr.id,
                        name = tr.name,
                        date = tr.date,
                        amount = tr.amount,
                        category = (categories.value as Async.Success<List<Category>>).data.get(tr.categoryId)
                    )
                }
                )
            } catch(e: Exception) {
                Async.Error(e.message ?: "")
            }
        }
    }

    suspend fun refreshAdvices() {
        _advices.value = try {
            Async.Success(adviceList)
        } catch(e: Exception) {
            Async.Error(e.message ?: "")
        }
    }

    override suspend fun getIcons(): List<UserIcon> = CollectIcons()

    override suspend fun getColors(): List<UserColor> = CollectColors()

    override suspend fun sendTransaction(transaction: TransactionToSend) {
        transactionList.add(transaction.copy(id = transactionList.size))
        refreshTransactions()
        refreshLastMonth()
        refreshLast3Months()
        refreshLastYear()
        refreshCategoriesExpense()
    }

    override suspend fun sendCategory(category: CategoryToSend) {
        categoryList.add(category.copy(id = categoryList.size))
        refreshCategories()
    }

    suspend fun refreshLastMonth() {
        _lastMonth.value = when (transactions.value) {
            is Async.Error -> Async.Error((transactions.value as Async.Error).errorMessage)
            is Async.Loading -> Async.Loading
            is Async.Success -> {
                val transactions = (transactions.value as Async.Success).data.sortedBy { it.date.toEpochMilli() }
                val incomeByDay = transactions.filter {
                    (it.amount >= 0) && (it.date.atZone(ZoneId.systemDefault()).month.value == LocalDate.now().month.value)
                }.associate {
                    it.date.atZone(ZoneId.systemDefault()).dayOfMonth to it.amount
                }
                val income = List(31) { incomeByDay.getOrDefault(it, 0) }.runningReduce { acc, value -> acc + value }

                val expenseByDay = transactions.filter {
                    (it.amount < 0) && (it.date.atZone(ZoneId.systemDefault()).month.value == LocalDate.now().month.value)
                }.associate {
                    it.date.atZone(ZoneId.systemDefault()).dayOfMonth to -it.amount
                }
                val expense = List(31) { expenseByDay.getOrDefault(it, 0) }.runningReduce { acc, value -> acc + value }
                Async.Success(Stat(income, expense))
            }
        }
    }

    suspend fun refreshLast3Months() {
        _last3Months.value = when (transactions.value) {
            is Async.Error -> Async.Error((transactions.value as Async.Error).errorMessage)
            is Async.Loading -> Async.Loading
            is Async.Success -> {
                val transactions =
                    (transactions.value as Async.Success).data.sortedBy { it.date.toEpochMilli() }
                val incomeByDay = transactions.filter {
                    (it.amount >= 0) && ((LocalDate.now().month.value - it.date.atZone(ZoneId.systemDefault()).month.value) % 12 < 3)
                }.associate {
                    (((LocalDate.now().month.value + 3 - it.date.atZone(ZoneId.systemDefault()).month.value) % 12)) * 31 + it.date.atZone(
                        ZoneId.systemDefault()
                    ).dayOfMonth to it.amount
                }

                val income = List(93) {
                    incomeByDay.getOrDefault(
                        it,
                        0
                    )
                }.runningReduce { acc, value -> acc + value }

                val expenseByDay = transactions.filter {
                    (it.amount < 0) && ((LocalDate.now().month.value - it.date.atZone(ZoneId.systemDefault()).month.value) % 12 < 3)
                }.associate {
                    ((((LocalDate.now().month.value - it.date.atZone(ZoneId.systemDefault()).month.value) % 12)) * 31 + it.date.atZone(
                        ZoneId.systemDefault()
                    ).dayOfMonth) to -it.amount
                }

                Log.e("test", transactions.filter {
                    (it.amount < 0) && ((LocalDate.now().month.value - it.date.atZone(ZoneId.systemDefault()).month.value) % 12 < 3)
                }.map {
                    ((((LocalDate.now().month.value - it.date.atZone(ZoneId.systemDefault()).month.value) % 12)) * 31 + it.date.atZone(
                    ZoneId.systemDefault()
                ).dayOfMonth
                    )
                }. toString())

                val expense = List(93) {
                    expenseByDay.getOrDefault(
                        it,
                        0
                    )
                }.runningReduce { acc, value -> acc + value }.also { Log.e("Expense 3 Months", it.toString()) }

                Async.Success(Stat(income, expense))
            }
        }
    }


    suspend fun refreshLastYear() {
        _lastYear.value = when (transactions.value) {
            is Async.Error -> Async.Error((transactions.value as Async.Error).errorMessage)
            is Async.Loading -> Async.Loading
            is Async.Success -> {
                val transactions =
                    (transactions.value as Async.Success).data.sortedBy { it.date.toEpochMilli() }

                val incomeByDay = transactions.filter {
                    (it.amount >= 0) && (it.date.atZone(ZoneId.systemDefault()).month.value == LocalDate.now().month.value)
                }.associate {
                    it.date.atZone(ZoneId.systemDefault()).dayOfMonth to it.amount
                }

                val income = List(31) {
                    incomeByDay.getOrDefault(
                        it,
                        0
                    )
                }.runningReduce { acc, value -> acc + value }

                val expenseByDay = transactions.filter {
                    (it.amount < 0) && (it.date.atZone(ZoneId.systemDefault()).month.value == LocalDate.now().month.value)
                }.associate {
                    it.date.atZone(ZoneId.systemDefault()).dayOfMonth to -it.amount
                }

                val expense = List(31) {
                    incomeByDay.getOrDefault(
                        it,
                        0
                    )
                }.runningReduce { acc, value -> acc + value }

                Async.Success(Stat(income, expense))
            }
        }
    }

     suspend fun refreshCategoriesExpense() {
         _lastMonthCatExp.value = when (transactions.value) {
            is Async.Error -> Async.Error((transactions.value as Async.Error).errorMessage)
            is Async.Loading -> Async.Loading
            is Async.Success ->
                Async.Success((transactions.value as Async.Success).data.filter { it.amount < 0 }.groupBy {
                    it.category
                }.mapValues { it.value.sumOf { -it.amount } })

        }
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
    UserIcon(8, Icons.Default.AirlineSeatIndividualSuite),
    UserIcon(9, Icons.Default.House),
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


