package com.gihansgamage.coinspot.data.local.database.converters

import androidx.room.TypeConverter
import com.gihansgamage.coinspot.data.local.database.entities.BadgeCategory
import com.gihansgamage.coinspot.data.local.database.entities.TransactionType

class EnumConverters {

    @TypeConverter
    fun fromTransactionType(value: TransactionType): String {
        return value.name
    }

    @TypeConverter
    fun toTransactionType(value: String): TransactionType {
        return TransactionType.valueOf(value)
    }

    @TypeConverter
    fun fromBadgeCategory(value: BadgeCategory): String {
        return value.name
    }

    @TypeConverter
    fun toBadgeCategory(value: String): BadgeCategory {
        return BadgeCategory.valueOf(value)
    }
}