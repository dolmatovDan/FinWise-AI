package com.spbsu_team7.finwise.core.model

import java.time.Instant

data class Transaction(val date: Instant, val amount: Int, val category: Category, val note: String)
