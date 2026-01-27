package com.gihansgamage.coinspot.data.local.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "daily_savings",
    foreignKeys = [
        ForeignKey(
            entity = SavingGoal::class,
            parentColumns = ["id"],
            childColumns = ["goalId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("goalId"), Index("date")]
)
data class DailySaving(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val goalId: Int,
    val amount: Double,
    val date: LocalDate,
    val note: String = "",
    val transactionType: TransactionType = TransactionType.DEPOSIT,
    val createdAt: Long = System.currentTimeMillis()
)

enum class TransactionType {
    DEPOSIT,    // Adding money to goal
    WITHDRAWAL  // Taking money back from goal
}