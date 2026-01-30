package com.techducat.ajo.util

import android.util.Log

object Logger {
    private const val TAG = "com.techducat.ajo.util.Logger"
    
    fun d(message: String) {
        Log.d(TAG, message)
    }
    
    fun i(message: String) {
        Log.i(TAG, message)
    }
    
    fun w(message: String) {
        Log.w(TAG, message)
    }
    
    fun e(message: String) {
        Log.e(TAG, message)
    }
    
    fun e(message: String, throwable: Throwable) {
        Log.e(TAG, message, throwable)
    }
}
