package com.techducat.ajo.core.network

import android.content.Context
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.*
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.ByteBuffer
import java.security.SecureRandom
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

/**
 * Complete I2P Transport Implementation using SAM (Simple Anonymous Messaging) v3.1
 * 
 * This implementation provides:
 * - I2P destination creation and management
 * - Sending messages through I2P network
 * - Receiving messages via streaming connections
 * - Connection pooling and retry logic
 * - Graceful error handling and recovery
 * 
 * Prerequisites:
 * - I2P router running on Android (via I2P Android app)
 * - SAM bridge enabled in I2P router (default port 7656)
 * 
 * @param context Android application context
 */
class I2PTransport(private val context: Context) : NetworkTransport {
    
    companion object {
        private const val TAG = "I2PTransport"
        
        // SAM Bridge Configuration
        private const val SAM_HOST = "127.0.0.1"
        private const val SAM_PORT = 7656
        private const val SAM_VERSION = "3.1"
        
        // Connection Configuration
        private const val CONNECT_TIMEOUT_MS = 30000
        private const val READ_TIMEOUT_MS = 60000
        private const val MAX_MESSAGE_SIZE = 32 * 1024 // 32 KB
        private const val MAX_RETRY_ATTEMPTS = 3
        private const val RETRY_DELAY_MS = 5000L
        
        // Session Configuration
        private const val SESSION_STYLE = "STREAM" // STREAM, DATAGRAM, or RAW
        private const val DESTINATION_SIGNATURE_TYPE = "EdDSA_SHA512_Ed25519"
        
        // Message Protocol
        private const val PROTOCOL_VERSION: Byte = 0x01
        private const val MESSAGE_TYPE_DATA: Byte = 0x01
        private const val MESSAGE_TYPE_ACK: Byte = 0x02
        private const val MESSAGE_TYPE_PING: Byte = 0x03
    }
    
    // Connection State
    private var myDestination: String? = null
    private var samSession: SAMSession? = null
    private val isInitialized = AtomicBoolean(false)
    private val isRunning = AtomicBoolean(false)
    
    // Message Handling
    private var messageListener: ((ByteArray) -> Unit)? = null
    private val pendingConnections = ConcurrentHashMap<String, Socket>()
    
    // Coroutine Management
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val serverJob: Job? = null
    private val connectionMutex = Mutex()
    
    // Statistics
    private var messagesSent = 0L
    private var messagesReceived = 0L
    private var connectionErrors = 0L
    
    /**
     * Initialize I2P transport
     * - Connects to SAM bridge
     * - Creates I2P destination
     * - Starts listening for incoming connections
     * 
     * @return The I2P destination string (base64 encoded)
     */
    override suspend fun initialize(): String = withContext(Dispatchers.IO) {
        if (isInitialized.get()) {
            Log.i(TAG, "I2P transport already initialized")
            return@withContext myDestination!!
        }
        
        try {
            Log.i(TAG, "Initializing I2P transport...")
            
            // Step 1: Test SAM bridge connection
            if (!testSAMConnection()) {
                throw IOException("Cannot connect to SAM bridge. Is I2P router running?")
            }
            
            // Step 2: Create SAM session and destination
            val session = createSAMSession()
            samSession = session
            myDestination = session.destination
            
            // Step 3: Start listening for incoming connections
            startListening()
            
            isInitialized.set(true)
            isRunning.set(true)
            
            Log.i(TAG, "I2P transport initialized successfully")
            Log.i(TAG, "My I2P destination: ${myDestination?.take(50)}...")
            
            myDestination!!
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize I2P transport", e)
            isInitialized.set(false)
            throw IOException("I2P initialization failed: ${e.message}", e)
        }
    }
    
    /**
     * Send data to an I2P destination
     * 
     * @param destination The recipient's I2P destination (base64 string)
     * @param data The data to send
     */
    override suspend fun send(destination: String, data: ByteArray) = withContext(Dispatchers.IO) {
        if (!isInitialized.get()) {
            throw IllegalStateException("I2P transport not initialized")
        }
        
        if (data.size > MAX_MESSAGE_SIZE) {
            throw IllegalArgumentException("Message size ${data.size} exceeds maximum $MAX_MESSAGE_SIZE")
        }
        
        val cleanDestination = cleanDestination(destination)
        
        Log.d(TAG, "Sending ${data.size} bytes to ${cleanDestination.take(50)}...")
        
        var lastException: Exception? = null
        
        // Retry logic
        repeat(MAX_RETRY_ATTEMPTS) { attempt ->
            try {
                sendWithConnection(cleanDestination, data)
                messagesSent++
                Log.d(TAG, "Message sent successfully (attempt ${attempt + 1})")
                return@withContext
                
            } catch (e: Exception) {
                lastException = e
                connectionErrors++
                Log.w(TAG, "Send attempt ${attempt + 1} failed: ${e.message}")
                
                if (attempt < MAX_RETRY_ATTEMPTS - 1) {
                    delay(RETRY_DELAY_MS)
                }
            }
        }
        
        throw IOException("Failed to send message after $MAX_RETRY_ATTEMPTS attempts", lastException)
    }
    
    /**
     * Set listener for incoming messages
     */
    override fun setReceiveListener(listener: (ByteArray) -> Unit) {
        this.messageListener = listener
        Log.d(TAG, "Message listener set")
    }
    
    /**
     * Shutdown I2P transport
     */
    override suspend fun shutdown() {
        withContext(Dispatchers.IO) {
            if (!isInitialized.get()) {
                return@withContext
            }
            
            Log.i(TAG, "Shutting down I2P transport...")
            isRunning.set(false)
            
            // Close all pending connections
            pendingConnections.values.forEach { socket ->
                try {
                    socket.close()
                } catch (e: Exception) {
                    Log.w(TAG, "Error closing connection: ${e.message}")
                }
            }
            pendingConnections.clear()
            
            // Close SAM session
            samSession?.close()
            samSession = null
            
            // Cancel coroutines
            scope.cancel()
            
            isInitialized.set(false)
            myDestination = null
            
            Log.i(TAG, "I2P transport shutdown complete")
            Log.i(TAG, "Statistics - Sent: $messagesSent, Received: $messagesReceived, Errors: $connectionErrors")
        }
    }
    
    // ==================== Private Implementation ====================
    
    /**
     * Test if SAM bridge is accessible
     */
    private fun testSAMConnection(): Boolean {
        return try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(SAM_HOST, SAM_PORT), 5000)
                true
            }
        } catch (e: Exception) {
            Log.e(TAG, "SAM bridge not accessible: ${e.message}")
            false
        }
    }
    
    /**
     * Create SAM session with I2P destination
     */
    private suspend fun createSAMSession(): SAMSession {
        return connectionMutex.withLock {
            val socket = Socket()
            
            try {
                socket.connect(InetSocketAddress(SAM_HOST, SAM_PORT), CONNECT_TIMEOUT_MS)
                socket.soTimeout = READ_TIMEOUT_MS
                
                val input = BufferedReader(InputStreamReader(socket.getInputStream()))
                val output = PrintWriter(socket.getOutputStream(), true)
                
                // HELLO command
                output.println("HELLO VERSION MIN=$SAM_VERSION MAX=$SAM_VERSION")
                val helloResponse = input.readLine()
                
                if (!helloResponse.startsWith("HELLO REPLY RESULT=OK")) {
                    throw IOException("SAM HELLO failed: $helloResponse")
                }
                
                Log.d(TAG, "SAM HELLO successful: $helloResponse")
                
                // Generate session ID
                val sessionId = generateSessionId()
                
                // SESSION CREATE command with TRANSIENT destination
                val sessionCommand = buildString {
                    append("SESSION CREATE ")
                    append("STYLE=$SESSION_STYLE ")
                    append("ID=$sessionId ")
                    append("DESTINATION=TRANSIENT ")
                    append("SIGNATURE_TYPE=$DESTINATION_SIGNATURE_TYPE")
                }
                
                output.println(sessionCommand)
                val sessionResponse = input.readLine()
                
                if (!sessionResponse.startsWith("SESSION STATUS RESULT=OK")) {
                    throw IOException("SAM SESSION CREATE failed: $sessionResponse")
                }
                
                // Extract destination from response
                val destination = extractDestination(sessionResponse)
                    ?: throw IOException("Failed to extract destination from response")
                
                Log.d(TAG, "SAM session created: $sessionId")
                Log.d(TAG, "Destination: ${destination.take(50)}...")
                
                SAMSession(
                    sessionId = sessionId,
                    destination = destination,
                    controlSocket = socket,
                    input = input,
                    output = output
                )
                
            } catch (e: Exception) {
                socket.close()
                throw e
            }
        }
    }
    
    /**
     * Start listening for incoming connections
     */
    private fun startListening() {
        scope.launch {
            Log.i(TAG, "Starting I2P listener...")
            
            while (isRunning.get()) {
                try {
                    acceptIncomingConnection()
                } catch (e: Exception) {
                    if (isRunning.get()) {
                        Log.e(TAG, "Error in listener loop: ${e.message}")
                        delay(RETRY_DELAY_MS)
                    }
                }
            }
            
            Log.i(TAG, "I2P listener stopped")
        }
    }
    
    /**
     * Accept and handle incoming connection
     */
    private suspend fun acceptIncomingConnection() {
        val session = samSession ?: return
        
        withContext(Dispatchers.IO) {
            // Use STREAM ACCEPT to wait for incoming connection
            session.output.println("STREAM ACCEPT ID=${session.sessionId} SILENT=false")
            
            val response = session.input.readLine() ?: return@withContext
            
            if (response.startsWith("STREAM STATUS RESULT=OK")) {
                // Connection accepted, create new socket for this connection
                val clientSocket = createStreamSocket(session.sessionId)
                
                if (clientSocket != null) {
                    launch {
                        handleIncomingMessage(clientSocket)
                    }
                }
            }
        }
    }
    
    /**
     * Create streaming socket for incoming connection
     */
    private fun createStreamSocket(sessionId: String): Socket? {
        return try {
            val socket = Socket()
            socket.connect(InetSocketAddress(SAM_HOST, SAM_PORT), CONNECT_TIMEOUT_MS)
            socket.soTimeout = READ_TIMEOUT_MS
            socket
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create stream socket: ${e.message}")
            null
        }
    }
    
    /**
     * Handle incoming message from socket
     */
    private suspend fun handleIncomingMessage(socket: Socket) {
        withContext(Dispatchers.IO) {
            try {
                val input = DataInputStream(socket.getInputStream())
                
                // Read message length (4 bytes)
                val messageLength = input.readInt()
                
                if (messageLength <= 0 || messageLength > MAX_MESSAGE_SIZE) {
                    Log.w(TAG, "Invalid message length: $messageLength")
                    return@withContext
                }
                
                // Read message data
                val messageData = ByteArray(messageLength)
                input.readFully(messageData)
                
                // Process message
                val unwrappedData = unwrapMessage(messageData)
                
                if (unwrappedData != null) {
                    messagesReceived++
                    messageListener?.invoke(unwrappedData)
                    Log.d(TAG, "Message received: ${unwrappedData.size} bytes")
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Error handling incoming message: ${e.message}")
            } finally {
                socket.close()
            }
        }
    }
    
    /**
     * Send data with connection management
     */
    private suspend fun sendWithConnection(destination: String, data: ByteArray) {
        withContext(Dispatchers.IO) {
            val session = samSession ?: throw IllegalStateException("No SAM session")
            
            // Create new socket for this connection
            val socket = Socket()
            
            try {
                socket.connect(InetSocketAddress(SAM_HOST, SAM_PORT), CONNECT_TIMEOUT_MS)
                socket.soTimeout = READ_TIMEOUT_MS
                
                val input = BufferedReader(InputStreamReader(socket.getInputStream()))
                val output = PrintWriter(socket.getOutputStream(), true)
                
                // HELLO
                output.println("HELLO VERSION MIN=$SAM_VERSION MAX=$SAM_VERSION")
                val helloResponse = input.readLine()
                
                if (!helloResponse.startsWith("HELLO REPLY RESULT=OK")) {
                    throw IOException("Connection HELLO failed: $helloResponse")
                }
                
                // STREAM CONNECT
                output.println("STREAM CONNECT ID=${session.sessionId} DESTINATION=$destination SILENT=false")
                val connectResponse = input.readLine()
                
                if (!connectResponse.startsWith("STREAM STATUS RESULT=OK")) {
                    throw IOException("STREAM CONNECT failed: $connectResponse")
                }
                
                // Send message data
                val wrappedData = wrapMessage(data)
                val dataOutput = DataOutputStream(socket.getOutputStream())
                
                // Send length prefix
                dataOutput.writeInt(wrappedData.size)
                // Send data
                dataOutput.write(wrappedData)
                dataOutput.flush()
                
                Log.d(TAG, "Data sent successfully")
                
            } finally {
                socket.close()
            }
        }
    }
    
    /**
     * Wrap message with protocol header
     */
    private fun wrapMessage(data: ByteArray): ByteArray {
        val buffer = ByteBuffer.allocate(2 + data.size)
        buffer.put(PROTOCOL_VERSION)
        buffer.put(MESSAGE_TYPE_DATA)
        buffer.put(data)
        return buffer.array()
    }
    
    /**
     * Unwrap message and validate protocol
     */
    private fun unwrapMessage(wrappedData: ByteArray): ByteArray? {
        if (wrappedData.size < 2) {
            Log.w(TAG, "Message too short")
            return null
        }
        
        val buffer = ByteBuffer.wrap(wrappedData)
        val version = buffer.get()
        val messageType = buffer.get()
        
        if (version != PROTOCOL_VERSION) {
            Log.w(TAG, "Unsupported protocol version: $version")
            return null
        }
        
        if (messageType != MESSAGE_TYPE_DATA) {
            Log.d(TAG, "Non-data message type: $messageType")
            return null
        }
        
        val data = ByteArray(wrappedData.size - 2)
        buffer.get(data)
        
        return data
    }
    
    /**
     * Generate unique session ID
     */
    private fun generateSessionId(): String {
        val random = SecureRandom()
        val bytes = ByteArray(8)
        random.nextBytes(bytes)
        return "ajo_" + bytes.joinToString("") { "%02x".format(it) }
    }
    
    /**
     * Extract destination from SAM response
     */
    private fun extractDestination(response: String): String? {
        val pattern = "DESTINATION=([A-Za-z0-9+/=~-]+)".toRegex()
        val match = pattern.find(response)
        return match?.groupValues?.get(1)
    }
    
    /**
     * Clean destination string (remove prefixes like "i2p://")
     */
    private fun cleanDestination(destination: String): String {
        return destination
            .removePrefix("i2p://")
            .removeSuffix(".i2p")
            .trim()
    }
    
    /**
     * Get connection statistics
     */
    fun getStatistics(): Map<String, Any> {
        return mapOf(
            "initialized" to isInitialized.get(),
            "running" to isRunning.get(),
            "messagesSent" to messagesSent,
            "messagesReceived" to messagesReceived,
            "connectionErrors" to connectionErrors,
            "pendingConnections" to pendingConnections.size,
            "myDestination" to (myDestination ?: "none")
        )
    }
    
    // ==================== Inner Classes ====================
    
    /**
     * Represents an active SAM session
     */
    private data class SAMSession(
        val sessionId: String,
        val destination: String,
        val controlSocket: Socket,
        val input: BufferedReader,
        val output: PrintWriter
    ) {
        fun close() {
            try {
                output.println("SESSION REMOVE ID=$sessionId")
                controlSocket.close()
            } catch (e: Exception) {
                Log.w(TAG, "Error closing SAM session: ${e.message}")
            }
        }
    }
}
