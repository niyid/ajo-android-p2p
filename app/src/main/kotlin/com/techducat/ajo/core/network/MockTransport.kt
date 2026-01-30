package com.techducat.ajo.core.network

import kotlinx.coroutines.delay
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Mock transport for testing (no actual network)
 */
class MockTransport : NetworkTransport {
    private val messageQueue = ConcurrentLinkedQueue<Pair<String, ByteArray>>()
    private var listener: ((ByteArray) -> Unit)? = null
    
    override suspend fun initialize(): String {
        return "mock://test-node"
    }
    
    override suspend fun send(destination: String, data: ByteArray) {
        messageQueue.offer(destination to data)
    }
    
    override fun setReceiveListener(listener: (ByteArray) -> Unit) {
        this.listener = listener
    }
    
    override suspend fun shutdown() {
        messageQueue.clear()
    }
    
    /**
     * Test helper - deliver all queued messages
     */
    fun deliverMessages() {
        while (messageQueue.isNotEmpty()) {
            val (_, data) = messageQueue.poll()
            listener?.invoke(data)
        }
    }
}
