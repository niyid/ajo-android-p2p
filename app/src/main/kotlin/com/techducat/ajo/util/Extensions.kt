package com.techducat.ajo.util

import java.text.SimpleDateFormat
import java.util.*

fun Long.toXMR(): Double {
    return this.toDouble() / Constants.MONERO_ATOMIC_UNITS
}

fun Double.toAtomicUnits(): Long {
    return (this * Constants.MONERO_ATOMIC_UNITS).toLong()
}

fun Long.formatXMR(): String {
    val xmr = this.toXMR()
    return String.format("%.12f XMR", xmr)
}

fun Long.formatDate(): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(this))
}

fun Long.formatDateTime(): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(this))
}

fun String.truncateAddress(): String {
    return if (this.length > 16) {
        "${this.substring(0, 8)}...${this.substring(this.length - 8)}"
    } else {
        this
    }
}
