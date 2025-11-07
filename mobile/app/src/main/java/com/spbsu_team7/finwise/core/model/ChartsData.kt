package com.spbsu_team7.finwise.core.model

import com.spbsu_team7.finwise.core.repository.Stat

data class ChartsData (
    val lastMonthTransactions: Stat,
    val last3MonthsTransactions: Stat,
    val lastYearTransactions: Stat,
    val categoriesExpense: Map<Category, Int>
)