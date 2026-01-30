package com.techducat.ajo.dlt


import kotlinx.serialization.Serializable

object MockIPFSResponses {
    
    fun successfulAdd(hash: String = DLTTestUtils.generateMockIPFSHash()) = """
        {
            "Name": "test.json",
            "Hash": "$hash",
            "Size": "123"
        }
    """.trimIndent()
    
    fun successfulPin(hash: String) = """
        {
            "Pins": ["$hash"]
        }
    """.trimIndent()
    
    fun versionInfo() = """
        {
            "Version": "0.12.0",
            "Commit": "abc123",
            "Repo": "12",
            "System": "amd64/linux",
            "Golang": "go1.18"
        }
    """.trimIndent()
    
    fun statsRepo() = """
        {
            "NumObjects": 1000,
            "RepoSize": 5000000,
            "RepoPath": "/data/ipfs",
            "Version": "fs-repo@12"
        }
    """.trimIndent()
    
    fun error(message: String = "Internal error") = """
        {
            "Message": "$message",
            "Code": 0,
            "Type": "error"
        }
    """.trimIndent()
}

/**
 * Test assertions for DLT data
 */
