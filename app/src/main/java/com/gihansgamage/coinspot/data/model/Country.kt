package com.gihansgamage.coinspot.data.model

data class Country(
    val name: String,
    val code: String,
    val currency: String,
    val currencySymbol: String
)

object CountryData {
    val countries = listOf(
        Country("United States", "US", "USD", "$"),
        Country("United Kingdom", "UK", "GBP", "£"),
        Country("Sri Lanka", "LK", "LKR", "Rs"),
        Country("India", "IN", "INR", "₹"),
        Country("Canada", "CA", "CAD", "$"),
        Country("Australia", "AU", "AUD", "$"),
        Country("Singapore", "SG", "SGD", "$"),
        Country("Malaysia", "MY", "MYR", "RM"),
        Country("Japan", "JP", "JPY", "¥"),
        Country("China", "CN", "CNY", "¥"),
        Country("South Korea", "KR", "KRW", "₩"),
        Country("Thailand", "TH", "THB", "฿"),
        Country("Indonesia", "ID", "IDR", "Rp"),
        Country("Philippines", "PH", "PHP", "₱"),
        Country("Vietnam", "VN", "VND", "₫"),
        Country("Pakistan", "PK", "PKR", "Rs"),
        Country("Bangladesh", "BD", "BDT", "৳"),
        Country("United Arab Emirates", "AE", "AED", "د.إ"),
        Country("Saudi Arabia", "SA", "SAR", "ر.س"),
        Country("Qatar", "QA", "QAR", "ر.ق"),
        Country("Kuwait", "KW", "KWD", "د.ك"),
        Country("Bahrain", "BH", "BHD", "د.ب"),
        Country("Oman", "OM", "OMR", "ر.ع"),
        Country("New Zealand", "NZ", "NZD", "$"),
        Country("South Africa", "ZA", "ZAR", "R"),
        Country("Nigeria", "NG", "NGN", "₦"),
        Country("Kenya", "KE", "KES", "KSh"),
        Country("Egypt", "EG", "EGP", "£"),
        Country("Turkey", "TR", "TRY", "₺"),
        Country("Russia", "RU", "RUB", "₽"),
        Country("Brazil", "BR", "BRL", "R$"),
        Country("Mexico", "MX", "MXN", "$"),
        Country("Argentina", "AR", "ARS", "$"),
        Country("Chile", "CL", "CLP", "$"),
        Country("Colombia", "CO", "COP", "$"),
        Country("Peru", "PE", "PEN", "S/"),
        Country("European Union", "EU", "EUR", "€"),
        Country("Switzerland", "CH", "CHF", "Fr"),
        Country("Norway", "NO", "NOK", "kr"),
        Country("Sweden", "SE", "SEK", "kr"),
        Country("Denmark", "DK", "DKK", "kr"),
        Country("Poland", "PL", "PLN", "zł"),
        Country("Czech Republic", "CZ", "CZK", "Kč"),
        Country("Hungary", "HU", "HUF", "Ft"),
        Country("Israel", "IL", "ILS", "₪"),
    ).sortedBy { it.name }

    fun getCountryByCode(code: String): Country? {
        return countries.find { it.code == code }
    }

    fun getCountryByName(name: String): Country? {
        return countries.find { it.name == name }
    }
}