package com.techducat.ajo.data.local

import android.util.Log
import androidx.room.TypeConverter
import com.techducat.ajo.model.Member
import org.json.JSONObject

/**
 * Room TypeConverters for custom data types
 */
class DatabaseConverters {
    
    companion object {
        private const val TAG = "com.techducat.ajo.data.local.DatabaseConverters"
    }
    
    /**
     * Convert MultisigInfo object to JSON string for database storage
     */
    @TypeConverter
    fun fromMultisigInfo(multisigInfo: Member.MultisigInfo?): String? {
        if (multisigInfo == null) {
            Log.d(TAG, "fromMultisigInfo: Input is null")
            return null
        }
        
        return try {
            val json = JSONObject().apply {
                put("address", multisigInfo.address)
                put("viewKey", multisigInfo.viewKey)
                put("isReady", multisigInfo.isReady)
                put("exchangeState", multisigInfo.exchangeState)
            }.toString()
            
            Log.d(TAG, "fromMultisigInfo: Successfully converted to JSON")
            Log.d(TAG, "  address: ${multisigInfo.address.take(20)}...")
            Log.d(TAG, "  viewKey length: ${multisigInfo.viewKey.length}")
            Log.d(TAG, "  isReady: ${multisigInfo.isReady}")
            Log.d(TAG, "  exchangeState: ${multisigInfo.exchangeState}")
            Log.v(TAG, "  JSON output: $json")
            
            json
        } catch (e: Exception) {
            Log.e(TAG, "fromMultisigInfo: Failed to convert to JSON", e)
            null
        }
    }
    
    /**
     * Convert JSON string from database to MultisigInfo object
     */
    @TypeConverter
    fun toMultisigInfo(json: String?): Member.MultisigInfo? {
        if (json.isNullOrBlank()) {
            Log.d(TAG, "toMultisigInfo: Input is null or blank")
            return null
        }
        
        Log.v(TAG, "toMultisigInfo: Input JSON: $json")
        
        return try {
            val jsonObject = JSONObject(json)
            val multisigInfo = Member.MultisigInfo(
                address = jsonObject.getString("address"),
                viewKey = jsonObject.optString("viewKey", ""),
                isReady = jsonObject.getBoolean("isReady"),
                exchangeState = jsonObject.getString("exchangeState")
            )
            
            Log.d(TAG, "toMultisigInfo: Successfully converted from JSON")
            Log.d(TAG, "  address: ${multisigInfo.address.take(20)}...")
            Log.d(TAG, "  viewKey length: ${multisigInfo.viewKey.length}")
            Log.d(TAG, "  isReady: ${multisigInfo.isReady}")
            Log.d(TAG, "  exchangeState: ${multisigInfo.exchangeState}")
            
            multisigInfo
        } catch (e: Exception) {
            Log.e(TAG, "toMultisigInfo: Failed to convert from JSON", e)
            Log.e(TAG, "  Input JSON: $json")
            null
        }
    }
    
    /**
     * Convert Member.MemberStatus enum to String for database storage
     */
    @TypeConverter
    fun fromMemberStatus(status: Member.MemberStatus): String {
        val result = status.name
        Log.v(TAG, "fromMemberStatus: ${status.name} -> $result")
        return result
    }
    
    /**
     * Convert String from database to Member.MemberStatus enum
     */
    @TypeConverter
    fun toMemberStatus(value: String): Member.MemberStatus {
        return try {
            val status = Member.MemberStatus.valueOf(value)
            Log.v(TAG, "toMemberStatus: $value -> ${status.name}")
            status
        } catch (e: IllegalArgumentException) {
            Log.w(TAG, "toMemberStatus: Invalid status '$value', defaulting to ACTIVE", e)
            Member.MemberStatus.ACTIVE // Default fallback
        }
    }
}
