package org.d3if3155.MoMi.model

import java.math.BigInteger
import java.text.NumberFormat
import java.util.Locale

fun toMoneyFormat(value: Long): String {
    val localeID = Locale("in", "ID")
    val numberFormat = NumberFormat.getCurrencyInstance(localeID)
    numberFormat.minimumFractionDigits = 0
    return numberFormat.format(value)
}

fun fromMoneyFormat(value: String): String {
    // all except number
    val regex = Regex("[^0-9]")
    return value.replace(regex, "")
}