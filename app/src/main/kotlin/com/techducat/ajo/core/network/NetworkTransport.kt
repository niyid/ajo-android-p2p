package com.techducat.ajo.core.network

/**
 * Transport abstraction - allows swapping I2P/Tor/WebRTC/TCP
 */
interface NetworkTransport {
    /**
     * Initialize network layer and return our address
     */
    suspend fun initialize(): String
    
    /**
     * Send bytes to destination
     */
    suspend fun send(destination: String, data: ByteArray)
    
    /**
     * Set listener for incoming messages
     */
    fun setReceiveListener(listener: (ByteArray) -> Unit)
    
    /**
     * Clean up resources
     */
    suspend fun shutdown()
}
