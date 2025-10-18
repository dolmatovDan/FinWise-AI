package com.spbsu_team7.finwise.core.repository

import android.util.Log
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
import kotlin.math.absoluteValue

class TestRepository : Repository {
    val transactionList = mutableListOf(
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

    val categoryList = mutableListOf(
        Category("Стипендия", Icons.Default.Money, Color(0xFF009e42)),
        Category("Питание", Icons.Default.EmojiFoodBeverage, Color(0xFFffc929)),
        Category("Пополнение проездного", Icons.Default.Train, Color(0xFF070070)),
    )

    val adviceList = mutableListOf(
        Advice("Меньше надо есть!")
    )

    override  fun getTransactions(): List<Transaction> =
        {Log.d("print", transactionList.map { it.name }.toString())
            transactionList
        }()

    override  fun getStatus(): Status = {
        Log.d("status", "status")
        Status(
            transactionList.filter { it.amount >= 0 }.sumOf { it.amount },
            transactionList.filter { it.amount < 0 }.sumOf { it.amount.absoluteValue },
            transactionList.sumOf { it.amount }
        )}()

    override  fun getCategories(): List<Category> = categoryList

    override  fun getAdvices(): List<Advice> = adviceList

    override fun sendTransaction(transaction: Transaction) {
        Log.d("add", "add")
        transactionList.add(transaction)
    }

    override  fun sendCategory(category: Category) {
        categoryList.add(category)
    }
}


