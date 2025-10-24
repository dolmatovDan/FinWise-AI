package com.spbsu_team7.finwise.core.model

data class ChartsData (
    val lastSixMonthTransactions: Pair<List<Int>, List<Int>>,
    val categoriesExpense: Map<Category, Int>
)