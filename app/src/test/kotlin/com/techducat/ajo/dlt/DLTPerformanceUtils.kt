package com.techducat.ajo.dlt


import kotlinx.serialization.Serializable

object DLTPerformanceUtils {
    
    data class PerformanceMetrics(
        val operationName: String,
        val durationMs: Long,
        val success: Boolean,
        val dataSize: Int = 0
    )
    
    inline fun <T> measurePerformance(
        operationName: String,
        block: () -> T
    ): Pair<T?, PerformanceMetrics> {
        val startTime = System.currentTimeMillis()
        var success = false
        var result: T? = null
        
        try {
            result = block()
            success = true
        } catch (e: Exception) {
            // Operation failed
        }
        
        val duration = System.currentTimeMillis() - startTime
        val metrics = PerformanceMetrics(operationName, duration, success)
        
        return Pair(result, metrics)
    }
    
    fun formatMetrics(metrics: PerformanceMetrics): String {
        return buildString {
            append("${metrics.operationName}: ")
            append("${metrics.durationMs}ms ")
            append("(${if (metrics.success) "✓" else "✗"})")
            if (metrics.dataSize > 0) {
                append(" [${metrics.dataSize} bytes]")
            }
        }
    }
    
    fun assertPerformance(
        metrics: PerformanceMetrics,
        maxDurationMs: Long,
        message: String = ""
    ) {
        val actualMessage = if (message.isEmpty()) {
            "${metrics.operationName} took ${metrics.durationMs}ms, expected <= ${maxDurationMs}ms"
        } else {
            message
        }
        
        assert(metrics.durationMs <= maxDurationMs) { actualMessage }
    }
}

/**
 * Test scenarios builder
 */
