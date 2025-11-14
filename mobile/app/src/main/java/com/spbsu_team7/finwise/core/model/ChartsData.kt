package com.spbsu_team7.finwise.core.model

import com.spbsu_team7.finwise.core.repository.Stat

data class ChartsData (
    val transactions: Stat,
    val categoriesExpense: Map<Category, Int>
)