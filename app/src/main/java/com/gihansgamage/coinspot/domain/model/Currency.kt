// Currency.kt
package com.gihansgamage.coinspot.domain.model

data class Currency(
    val code: String,
    val name: String,
    val symbol: String
)

object CurrencyData {
    val currencies = listOf(
        Currency("USD", "US Dollar", "$"),
        Currency("EUR", "Euro", "€"),
        Currency("GBP", "British Pound", "£"),
        Currency("LKR", "Sri Lankan Rupee", "Rs"),
        Currency("INR", "Indian Rupee", "₹"),
        Currency("JPY", "Japanese Yen", "¥"),
        Currency("CNY", "Chinese Yuan", "¥"),
        Currency("AUD", "Australian Dollar", "A$"),
        Currency("CAD", "Canadian Dollar", "C$"),
        Currency("SGD", "Singapore Dollar", "S$")
    )

    fun getSymbol(code: String): String {
        return currencies.find { it.code == code }?.symbol ?: "$"
    }
}