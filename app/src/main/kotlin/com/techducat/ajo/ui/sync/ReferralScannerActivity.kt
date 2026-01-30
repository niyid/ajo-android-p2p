package com.techducat.ajo.ui.sync

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.google.zxing.integration.android.IntentIntegrator
import com.techducat.ajo.sync.ReferralCodec
import com.techducat.ajo.data.local.AjoDatabase
import com.techducat.ajo.data.local.entity.*
import com.techducat.ajo.core.crypto.KeyManagerImpl
import kotlinx.coroutines.launch

/**
 * Complete referral scanner with camera integration
 */
class ReferralScannerActivity : AppCompatActivity() {
    
    private lateinit var db: AjoDatabase
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        db = AjoDatabase.getInstance(this)
        
        setContentView(createLayout())
        
        // Check camera permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
        } else {
            startScanner()
        }
    }
    
    private fun createLayout(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
            
            addView(TextView(context).apply {
                text = "Scan Referral Code"
                textSize = 28f
                setTypeface(null, android.graphics.Typeface.BOLD)
                setPadding(0, 0, 0, 32)
            })
            
            addView(TextView(context).apply {
                text = "Point your camera at the QR code to join a ROSCA"
                textSize = 16f
                setPadding(0, 0, 0, 24)
            })
            
            addView(Button(context).apply {
                text = "Scan QR Code"
                textSize = 16f
                setPadding(32, 24, 32, 24)
                setOnClickListener { startScanner() }
            })
            
            addView(Button(context).apply {
                text = "Enter Code Manually"
                textSize = 16f
                setPadding(32, 24, 32, 24)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 16
                }
                setOnClickListener { showManualEntry() }
            })
        }
    }
    
    private fun startScanner() {
        IntentIntegrator(this).apply {
            setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            setPrompt("Scan Referral QR Code")
            setBeepEnabled(true)
            initiateScan()
        }
    }
    
    private fun showManualEntry() {
        val editText = EditText(this).apply {
            hint = "Paste referral code here"
        }
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Enter Referral Code")
            .setView(editText)
            .setPositiveButton("Join") { _, _ ->
                val code = editText.text.toString()
                processReferralCode(code)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null && result.contents != null) {
            processReferralCode(result.contents)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
    
    private fun processReferralCode(code: String) {
        lifecycleScope.launch {
            try {
                // Parse code
                val referral = ReferralCodec.parse(code)
                if (referral == null) {
                    Toast.makeText(this@ReferralScannerActivity, "Invalid referral code", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                
                // Verify signature
                if (!ReferralCodec.verify(referral)) {
                    Toast.makeText(this@ReferralScannerActivity, "Invalid signature", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                
                // Check expiry
                if (!ReferralCodec.isValid(referral)) {
                    Toast.makeText(this@ReferralScannerActivity, "Code expired", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                
                // Consume referral
                consumeReferral(referral)
                
            } catch (e: Exception) {
                Toast.makeText(this@ReferralScannerActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }
    
    private suspend fun consumeReferral(code: com.techducat.ajo.sync.ReferralCode) {
        val payload = code.payload
        val localNode = KeyManagerImpl.getOrCreateLocalNode(this)
        
        // 1. Add creator as peer
        db.peerDao().insert(PeerEntity(
            id = "peer_${payload.creatorNodeId}",
            nodeId = payload.creatorNodeId,
            roscaId = payload.roscaId,
            publicKey = payload.creatorPublicKey,
            role = "CREATOR",
            endpoint = payload.creatorEndpoint,
            status = "ACTIVE",
            addedAt = System.currentTimeMillis()
        ))
        
        // 2. Create ROSCA entry
        db.roscaDao().insert(com.techducat.ajo.data.local.entity.RoscaEntity(
            id = payload.roscaId,
            name = payload.roscaName,
            description = "",
            creatorId = payload.creatorNodeId,
            contributionAmount = payload.contributionAmount.toLong(),
            contributionFrequency = payload.frequency,
            totalMembers = payload.maxMembers,
            currentMembers = payload.currentMembers,
            status = "FORMING",
            createdAt = System.currentTimeMillis()
        ))
        
        // 3. Add self as member
        db.memberDao().insert(MemberEntity(
            id = "member_${localNode.nodeId}",
            roscaId = payload.roscaId,
            userId = localNode.nodeId,
            name = "Me",
            moneroAddress = null,
            joinedAt = System.currentTimeMillis(),
            position = 0,
            leftAt = 0,
            leftReason = "",
            isActive = true,
            status = "pending"
        ))
        
        // 4. Set up sync target
        db.syncTargetDao().insert(SyncTargetEntity(
            id = "sync_${payload.roscaId}",
            roscaId = payload.roscaId,
            targetPeerId = "peer_${payload.creatorNodeId}",
            syncEnabled = true
        ))
        
        Toast.makeText(this, "Successfully joined ${payload.roscaName}!", Toast.LENGTH_LONG).show()
        finish()
    }
}
