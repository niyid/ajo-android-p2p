package com.techducat.ajo.util

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

/**
 * Universal currency formatter for the Ajo app
 * Ensures consistent display of Monero amounts throughout the application
 */
object CurrencyFormatter {
    
    /**
     * Monero symbol - Latin Small Letter M with Hook (ɱ)
     * Unicode: U+0271
     */
    const val XMR_SYMBOL = "ɱ"
    
    /**
     * Alternative Monero symbol - Latin Capital Letter M with Hook (Ɱ)
     * Unicode: U+2133
     * Use this if lowercase doesn't render well on certain devices
     */
    const val XMR_SYMBOL_CAPITAL = "Ɱ"
    
    /**
     * Monero atomic units per XMR (piconero)
     */
    const val ATOMIC_UNITS_PER_XMR = 1_000_000_000_000L
    
    /**
     * Format Monero amount in atomic units to human-readable XMR
     * @param atomicUnits Amount in piconero (smallest Monero unit)
     * @param decimals Number of decimal places (default 6)
     * @param includeSymbol Whether to include the ɱ symbol (default true)
     * @return Formatted string like "ɱ1.234567"
     */
    fun formatXMR(
        atomicUnits: Long,
        decimals: Int = 6,
        includeSymbol: Boolean = true
    ): String {
        val xmr = atomicUnits.toDouble() / ATOMIC_UNITS_PER_XMR
        val pattern = "0.${"0".repeat(decimals)}"
        val formatter = DecimalFormat(pattern)
        val formatted = formatter.format(xmr)
        
        return if (includeSymbol) {
            "$XMR_SYMBOL$formatted"
        } else {
            formatted
        }
    }
    
    /**
     * Format Monero amount as double to human-readable XMR
     * @param xmr Amount in XMR
     * @param decimals Number of decimal places (default 6)
     * @param includeSymbol Whether to include the ɱ symbol (default true)
     * @return Formatted string like "ɱ1.234567"
     */
    fun formatXMRFromDouble(
        xmr: Double,
        decimals: Int = 6,
        includeSymbol: Boolean = true
    ): String {
        val pattern = "0.${"0".repeat(decimals)}"
        val formatter = DecimalFormat(pattern)
        val formatted = formatter.format(xmr)
        
        return if (includeSymbol) {
            "$XMR_SYMBOL$formatted"
        } else {
            formatted
        }
    }
    
    /**
     * Format fiat currency amount (USD)
     * @param amount Amount in USD
     * @param decimals Number of decimal places (default 2)
     * @return Formatted string like "$123.45"
     */
    fun formatUSD(amount: Double, decimals: Int = 2): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale.US)
        formatter.minimumFractionDigits = decimals
        formatter.maximumFractionDigits = decimals
        return formatter.format(amount)
    }
    
    /**
     * Format fiat currency amount with custom symbol
     * @param amount Amount
     * @param symbol Currency symbol (e.g., "₦", "€", "£")
     * @param decimals Number of decimal places (default 2)
     * @return Formatted string like "₦1,234.56"
     */
    fun formatCurrency(
        amount: Double,
        symbol: String,
        decimals: Int = 2
    ): String {
        val pattern = "#,##0.${"0".repeat(decimals)}"
        val formatter = DecimalFormat(pattern)
        val formatted = formatter.format(amount)
        return "$symbol$formatted"
    }
    
    /**
     * Convert XMR atomic units to double
     * @param atomicUnits Amount in piconero
     * @return Amount in XMR as double
     */
    fun atomicUnitsToXMR(atomicUnits: Long): Double {
        return atomicUnits.toDouble() / ATOMIC_UNITS_PER_XMR
    }
    
    /**
     * Convert XMR double to atomic units
     * @param xmr Amount in XMR
     * @return Amount in piconero (atomic units)
     */
    fun xmrToAtomicUnits(xmr: Double): Long {
        return (xmr * ATOMIC_UNITS_PER_XMR).toLong()
    }
    
    /**
     * Format XMR with abbreviated suffix for large amounts
     * Examples: ɱ1.23K, ɱ4.56M
     * @param atomicUnits Amount in piconero
     * @param includeSymbol Whether to include the ɱ symbol (default true)
     * @return Abbreviated formatted string
     */
    fun formatXMRAbbreviated(
        atomicUnits: Long,
        includeSymbol: Boolean = true
    ): String {
        val xmr = atomicUnits.toDouble() / ATOMIC_UNITS_PER_XMR
        
        val (value, suffix) = when {
            xmr >= 1_000_000 -> Pair(xmr / 1_000_000, "M")
            xmr >= 1_000 -> Pair(xmr / 1_000, "K")
            else -> Pair(xmr, "")
        }
        
        val formatter = DecimalFormat("0.##")
        val formatted = formatter.format(value) + suffix
        
        return if (includeSymbol) {
            "$XMR_SYMBOL$formatted"
        } else {
            formatted
        }
    }
    
    /**
     * Parse XMR string to atomic units
     * Handles strings like "1.5", "ɱ2.34", "0.000001"
     * @param xmrString String representation of XMR amount
     * @return Amount in atomic units, or null if parsing fails
     */
    fun parseXMRToAtomicUnits(xmrString: String): Long? {
        return try {
            // Remove symbol if present
            val cleaned = xmrString.replace(XMR_SYMBOL, "")
                .replace(XMR_SYMBOL_CAPITAL, "")
                .trim()
            
            val xmr = cleaned.toDoubleOrNull() ?: return null
            xmrToAtomicUnits(xmr)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Format percentage
     * @param value Percentage value (e.g., 0.856 for 85.6%)
     * @param decimals Number of decimal places (default 1)
     * @return Formatted string like "85.6%"
     */
    fun formatPercentage(value: Double, decimals: Int = 1): String {
        val pattern = "0.${"0".repeat(decimals)}"
        val formatter = DecimalFormat(pattern)
        return "${formatter.format(value * 100)}%"
    }
    
    /**
     * Get the appropriate Monero symbol based on device/locale
     * Can be extended to handle devices that don't render certain Unicode well
     * @return The best Monero symbol for the current device
     */
    fun getMoneroSymbol(): String {
        // In the future, you could add device-specific logic here
        // For now, always use the lowercase variant
        return XMR_SYMBOL
    }
}
