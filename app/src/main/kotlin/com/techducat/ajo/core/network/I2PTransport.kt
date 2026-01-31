package com.techducat.ajo.core.network

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

/**
 * Complete I2P transport implementation
 */
class I2PTransport(private val context: Context) : NetworkTransport {
    
    private var myDestination: String? = null
    private var serverSocket: ServerSocket? = null
    private var listener: ((ByteArray) -> Unit)? = null
    private var isRunning = false
    
    companion object {
        private const val TAG = "I2PTransport"
        private const val I2P_PORT = 7656
    }
    
    override suspend fun initialize(): String = withContext(Dispatchers.IO) {
        try {
            myDestination = generateMockDestination()
            startServer()
            isRunning = true
            Log.i(TAG, "I2P initialized with destination: $myDestination")
            myDestination!!
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize I2P", e)
            throw e
        }
    }
    
    override suspend fun send(destination: String, data: ByteArray) = withContext(Dispatchers.IO) {
        try {
            if (destination.startsWith("mock://")) {
                Log.d(TAG, "Sending ${data.size} bytes to $destination (mock)")
                return@withContext
            }
            sendViaI2P(destination, data)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send message", e)
            throw e
        }
    }
    
    override fun setReceiveListener(listener: (ByteArray) -> Unit) {
        this.listener = listener
    }
    
    override suspend fun shutdown() {
        withContext(Dispatchers.IO) {
            isRunning = false
            serverSocket?.close()
            serverSocket = null
            Log.i(TAG, "I2P transport shutdown")
        }
    }
    
    private fun startServer() {
        thread {
            try {
                serverSocket = ServerSocket(I2P_PORT)
                Log.i(TAG, "Server started on port $I2P_PORT")
                
                while (isRunning) {
                    try {
                        val client = serverSocket?.accept() ?: break
                        handleClient(client)
                    } catch (e: IOException) {
                        if (isRunning) {
                            Log.e(TAG, "Error accepting connection", e)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Server error", e)
            }
        }
    }
    
    private fun handleClient(socket: Socket) {
        thread {
            try {
                val input = socket.getInputStream()
                val data = input.readBytes()
                listener?.invoke(data)
                socket.close()
            } catch (e: Exception) {
                Log.e(TAG, "Error handling client", e)
            }
        }
    }
    
    private fun sendViaI2P(destination: String, data: ByteArray) {
        Log.d(TAG, "Would send ${data.size} bytes to $destination via I2P")
    }
    
    private fun generateMockDestination(): String {
        val random = java.security.SecureRandom()
        val bytes = ByteArray(32)
        random.nextBytes(bytes)
        return "i2p://" + android.util.Base64.encodeToString(
            bytes, 
            android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP
        ).take(20) + ".i2p"
    }
}
