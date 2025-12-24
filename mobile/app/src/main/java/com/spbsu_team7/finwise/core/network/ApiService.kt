package com.spbsu_team7.finwise.core.network

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.ui.graphics.Color
import com.google.gson.annotations.SerializedName
import com.spbsu_team7.finwise.core.model.Category
import com.spbsu_team7.finwise.core.model.Transaction
import com.spbsu_team7.finwise.core.model.UserIcon
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.time.format.DateTimeFormatter
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.time.toJavaInstant

interface ApiService {
    @GET("/api/v1/transactions/")
    suspend fun userTransactions (
        @Query("type") type: String,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 10
    ) : Response<TransactionsPage>

    @POST("/api/v1/transactions")
    suspend fun sendTransaction (
        @Body data: NewTransaction
    ) : Response<NewTransaction>
}

data class TransactionsPage (
    val page: Int,
    val page_size: Int,
    val total: Int,
    val transactions: List<TransactionNetwork>
)

data class TransactionNetwork (
    val id: String,
    val amount : Float,
    val category: String,
    val description: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

data class NewTransaction (
    val user_id: Int,
    val amount : Float,
    val category: String,
    val description: String,
    val type: String
)

val common = Category(0, "common", UserIcon(0, Icons.Default.VideogameAsset).imageVector, Color.Magenta)
@OptIn(ExperimentalTime::class)
fun TransactionNetwork.toModel() = Transaction(
        0, description, Instant.parse(createdAt).toJavaInstant(), amount.toInt(), common
    )

