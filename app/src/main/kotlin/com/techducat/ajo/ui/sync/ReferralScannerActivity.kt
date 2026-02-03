package com.techducat.ajo.ui.sync

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.google.zxing.integration.android.IntentIntegrator
import com.techducat.ajo.R
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
                text = getString(R.string.ReferralScanner_title)
                textSize = 28f
                setTypeface(null, android.graphics.Typeface.BOLD)
                setPadding(0, 0, 0, 32)
            })
            
            addView(TextView(context).apply {
                text = getString(R.string.ReferralScanner_instructions)
                textSize = 16f
                setPadding(0, 0, 0, 24)
            })
            
            addView(Button(context).apply {
                text = getString(R.string.ReferralScanner_btn_scan)
                textSize = 16f
                setPadding(32, 24, 32, 24)
                setOnClickListener { startScanner() }
            })
            
            addView(Button(context).apply {
                text = getString(R.string.ReferralScanner_btn_manual)
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
            setPrompt(getString(R.string.ReferralScanner_title))
            setBeepEnabled(true)
            initiateScan()
        }
    }
    
    private fun showManualEntry() {
        val editText = EditText(this).apply {
            hint = getString(R.string.ReferralScanner_manual_hint)
        }
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(getString(R.string.ReferralScanner_manual_title))
            .setView(editText)
            .setPositiveButton(getString(R.string.ReferralScanner_join)) { _, _ ->
                val code = editText.text.toString()
                processReferralCode(code)
            }
            .setNegativeButton(getString(R.string.cancel), null)
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
                    Toast.makeText(this@ReferralScannerActivity, getString(R.string.ReferralScanner_invalid_code), Toast.LENGTH_SHORT).show()
                    return@launch
                }
                
                // Verify signature
                if (!ReferralCodec.verify(referral)) {
                    Toast.makeText(this@ReferralScannerActivity, getString(R.string.ReferralScanner_invalid_signature), Toast.LENGTH_SHORT).show()
                    return@launch
                }
                
                // Check expiry
                if (!ReferralCodec.isValid(referral)) {
                    Toast.makeText(this@ReferralScannerActivity, getString(R.string.ReferralScanner_code_expired), Toast.LENGTH_SHORT).show()
                    return@launch
                }
                
                // Consume referral
                consumeReferral(referral)
                
            } catch (e: Exception) {
                Toast.makeText(this@ReferralScannerActivity, getString(R.string.ReferralScanner_error_format, e.message), Toast.LENGTH_LONG).show()
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
        
        Toast.makeText(this, getString(R.string.ReferralScanner_success_format, payload.roscaName), Toast.LENGTH_LONG).show()
        
        // ✅ Return success result to calling activity
        setResult(RESULT_OK)
        finish()
    }
}
