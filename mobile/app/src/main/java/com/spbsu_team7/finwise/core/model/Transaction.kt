package com.spbsu_team7.finwise.core.model

import java.time.Instant

data class Transaction(val id: Int, val name: String, val date: Instant, val amount: Int, val category: Category)

data class TransactionToSend(val id: Int, val name: String, val date: Instant, val amount: Int, val categoryId: Int)
