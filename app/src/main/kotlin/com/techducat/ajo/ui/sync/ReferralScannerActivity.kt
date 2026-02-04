package com.techducat.ajo.ui.sync

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
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

class ReferralScannerActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "com.techducat.ajo.ui.sync.ReferralScannerActivity"
        private const val CAMERA_PERMISSION_REQUEST = 100
    }  
    
    private lateinit var db: AjoDatabase
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Log.d(TAG, "=== ReferralScannerActivity Created ===")
        
        db = AjoDatabase.getInstance(this)
        
        setContentView(createLayout())
        
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "Camera permission not granted, requesting...")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST)
        } else {
            Log.d(TAG, "Camera permission already granted, starting scanner")
            startScanner()
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "✓ Camera permission granted by user")
                startScanner()
            } else {
                Log.w(TAG, "✗ Camera permission denied by user")
                Toast.makeText(
                    this,
                    getString(R.string.ReferralScanner_camera_permission_required),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    
    private fun createLayout(): LinearLayout {
        Log.d(TAG, "Creating scanner layout")
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
                setOnClickListener { 
                    Log.d(TAG, "User clicked Scan QR Code button")
                    startScanner() 
                }
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
                setOnClickListener { 
                    Log.d(TAG, "User clicked Enter Code Manually button")
                    showManualEntry() 
                }
            })
        }
    }
    
    private fun startScanner() {
        Log.d(TAG, "Starting QR code scanner...")
        try {
            IntentIntegrator(this).apply {
                setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                setPrompt(getString(R.string.ReferralScanner_title))
                setBeepEnabled(true)
                initiateScan()
            }
            Log.d(TAG, "QR scanner initiated successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start QR scanner", e)
            Toast.makeText(
                this,
                getString(R.string.ReferralScanner_scanner_failed, e.message),
                Toast.LENGTH_LONG
            ).show()
        }
    }
    
    private fun showManualEntry() {
        Log.d(TAG, "Showing manual code entry dialog")
        val editText = EditText(this).apply {
            hint = getString(R.string.ReferralScanner_manual_hint)
        }
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(getString(R.string.ReferralScanner_manual_title))
            .setView(editText)
            .setPositiveButton(getString(R.string.ReferralScanner_join)) { _, _ ->
                val code = editText.text.toString()
                Log.d(TAG, "User entered code manually: ${code.take(4)}... (length: ${code.length})")
                processReferralCode(code)
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                Log.d(TAG, "User cancelled manual entry")
                dialog.dismiss()
            }
            .setOnCancelListener {
                Log.d(TAG, "Manual entry dialog cancelled")
            }
            .show()
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        Log.d(TAG, "onActivityResult: requestCode=$requestCode, resultCode=$resultCode")
        
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null && result.contents != null) {
            val scannedCode = result.contents
            Log.d(TAG, "✓ QR code scanned successfully")
            Log.d(TAG, "  Scanned code: ${scannedCode.take(10)}... (length: ${scannedCode.length})")
            processReferralCode(scannedCode)
        } else {
            Log.d(TAG, "QR scan cancelled or failed (no result)")
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun processReferralCode(scannedCode: String) {
        Log.d(TAG, "=== Processing Referral Code ===")
        Log.d(TAG, "Scanned code length: ${scannedCode.length}")
        Log.d(TAG, "Scanned code preview: ${scannedCode.take(50)}...")
        
        lifecycleScope.launch {
            try {
                // ════════════════════════════════════════════════════════
                // STEP 1: PARSE DEEP LINK OR QR CODE
                // ════════════════════════════════════════════════════════
                
                Log.d(TAG, "Step 1: Parsing scanned code...")
                
                val referralCode: String
                val roscaIdFromLink: String?
                
                // Try parsing as deep link first
                if (scannedCode.startsWith("ajo://") || scannedCode.startsWith("http")) {
                    // Parse as deep link: ajo://join?code=ABC12345&rosca=rosca_123
                    val uri = android.net.Uri.parse(scannedCode)
                    referralCode = uri.getQueryParameter("code") 
                        ?: run {
                            Log.e(TAG, "✗ Deep link missing 'code' parameter")
                            Toast.makeText(
                                this@ReferralScannerActivity,
                                getString(R.string.ReferralScanner_invalid_code),
                                Toast.LENGTH_SHORT
                            ).show()
                            return@launch
                        }
                    roscaIdFromLink = uri.getQueryParameter("rosca")
                    Log.d(TAG, "✓ Parsed deep link")
                    Log.d(TAG, "  Referral code: $referralCode")
                    Log.d(TAG, "  ROSCA ID: ${roscaIdFromLink ?: "not specified"}")
                } else {
                    // Could be either:
                    // 1. Simple referral code: "ABC12345"
                    // 2. Base64-encoded ReferralCode with full ROSCA info
                    referralCode = scannedCode.trim()
                    roscaIdFromLink = null
                    Log.d(TAG, "Processing as referral code or encoded payload")
                }
                
                if (referralCode.isBlank()) {
                    Log.e(TAG, "✗ Referral code is empty")
                    Toast.makeText(
                        this@ReferralScannerActivity,
                        getString(R.string.ReferralScanner_invalid_code),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }
                
                // ════════════════════════════════════════════════════════
                // STEP 2: CHECK IF IT'S A FULL REFERRAL PAYLOAD (P2P SYNC)
                // ════════════════════════════════════════════════════════
                
                Log.d(TAG, "Step 2: Attempting to parse as ReferralCodec payload...")
                
                val parsedReferralCode = ReferralCodec.parse(referralCode)
                
                if (parsedReferralCode != null) {
                    // ════════════════════════════════════════════════════
                    // PATH A: FULL REFERRAL CODE WITH ROSCA INFO
                    // ════════════════════════════════════════════════════
                    
                    Log.d(TAG, "✓ Parsed as ReferralCodec payload")
                    Log.d(TAG, "  ROSCA ID: ${parsedReferralCode.payload.roscaId}")
                    Log.d(TAG, "  ROSCA Name: ${parsedReferralCode.payload.roscaName}")
                    
                    // Verify signature
                    Log.d(TAG, "Step 2a: Verifying signature...")
                    if (!ReferralCodec.verify(parsedReferralCode)) {
                        Log.w(TAG, "✗ Signature verification failed")
                        Toast.makeText(
                            this@ReferralScannerActivity,
                            getString(R.string.ReferralScanner_invalid_signature),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@launch
                    }
                    Log.d(TAG, "✓ Signature verified")
                    
                    // Check expiry
                    Log.d(TAG, "Step 2b: Checking expiry...")
                    if (!ReferralCodec.isValid(parsedReferralCode)) {
                        Log.w(TAG, "✗ Code has expired")
                        Toast.makeText(
                            this@ReferralScannerActivity,
                            getString(R.string.ReferralScanner_code_expired),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@launch
                    }
                    Log.d(TAG, "✓ Code is valid and not expired")
                    
                    // Extract ROSCA info from payload
                    val roscaId = parsedReferralCode.payload.roscaId
                    val roscaName = parsedReferralCode.payload.roscaName
                    
                    // Check if invite exists in local database
                    Log.d(TAG, "Step 2c: Checking if invite already synced to local DB...")
                    
                    // Try to find any pending invite for this ROSCA
                    val allInvites = db.inviteDao().getAllInvites()
                    val invite = allInvites.firstOrNull { 
                        it.roscaId == roscaId && it.status == InviteEntity.STATUS_PENDING 
                    }
                    
                    if (invite != null) {
                        // Invite already synced - use it
                        Log.d(TAG, "✓ Invite found in local database")
                        Log.d(TAG, "  Invite ID: ${invite.id}")
                        
                        returnResult(invite.referralCode, invite.id, invite.roscaId)
                        return@launch
                    }
                    
                    // Invite not synced yet - return ROSCA info so app can handle joining
                    Log.d(TAG, "⚠️ Invite not yet synced to local database")
                    Log.d(TAG, "Returning ROSCA info from QR code payload")
                    
                    val resultIntent = android.content.Intent().apply {
                        putExtra("referral_code", referralCode)  // Full encoded payload
                        putExtra("rosca_id", roscaId)
                        putExtra("rosca_name", roscaName)
                        putExtra("creator_node_id", parsedReferralCode.payload.creatorNodeId)
                        putExtra("creator_endpoint", parsedReferralCode.payload.creatorEndpoint)
                        putExtra("contribution_amount", parsedReferralCode.payload.contributionAmount)
                        putExtra("currency", parsedReferralCode.payload.currency)
                        putExtra("max_members", parsedReferralCode.payload.maxMembers)
                        putExtra("current_members", parsedReferralCode.payload.currentMembers)
                        putExtra("requires_sync", true)  // Flag that invite needs to be fetched
                    }
                    
                    Toast.makeText(
                        this@ReferralScannerActivity,
                        getString(R.string.ReferralScanner_code_verified),
                        Toast.LENGTH_SHORT
                    ).show()
                    
                    Log.d(TAG, "✓ Returning ROSCA info (invite will be synced on join)")
                    setResult(RESULT_OK, resultIntent)
                    finish()
                    return@launch
                }
                
                // ════════════════════════════════════════════════════════
                // PATH B: SIMPLE REFERRAL CODE (INVITE ALREADY SYNCED)
                // ════════════════════════════════════════════════════════
                
                Log.d(TAG, "Not a ReferralCodec payload - treating as simple referral code")
                Log.d(TAG, "Step 3: Looking up invite in local database...")
                Log.d(TAG, "  Searching for referral code: $referralCode")
                
                val invite = db.inviteDao().getInviteByReferralCode(referralCode)
                
                if (invite == null) {
                    Log.w(TAG, "✗ Invite not found in database")
                    Log.w(TAG, "  This could mean:")
                    Log.w(TAG, "  1. Invalid referral code")
                    Log.w(TAG, "  2. Invite hasn't synced yet")
                    Log.w(TAG, "  3. Full QR code (with ROSCA info) should be used instead")
                    Toast.makeText(
                        this@ReferralScannerActivity,
                        getString(R.string.ReferralScanner_not_found),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }
                
                Log.d(TAG, "✓ Invite found in database")
                Log.d(TAG, "  Invite ID: ${invite.id}")
                Log.d(TAG, "  ROSCA ID: ${invite.roscaId}")
                Log.d(TAG, "  Status: ${invite.status}")
                
                // Validate ROSCA ID matches if provided in deep link
                if (roscaIdFromLink != null && roscaIdFromLink != invite.roscaId) {
                    Log.w(TAG, "✗ ROSCA ID mismatch")
                    Log.w(TAG, "  Expected: $roscaIdFromLink")
                    Log.w(TAG, "  Got: ${invite.roscaId}")
                    Toast.makeText(
                        this@ReferralScannerActivity,
                        getString(R.string.ReferralScanner_invalid_code),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }
                
                // Check invite status
                Log.d(TAG, "Step 4: Checking invite status...")
                if (invite.status != InviteEntity.STATUS_PENDING) {
                    Log.w(TAG, "✗ Invite is not pending (status: ${invite.status})")
                    Toast.makeText(
                        this@ReferralScannerActivity,
                        getString(R.string.ReferralScanner_already_used),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }
                Log.d(TAG, "✓ Invite is pending and can be accepted")
                
                // Return result
                returnResult(referralCode, invite.id, invite.roscaId)
                
            } catch (e: Exception) {
                Log.e(TAG, "✗ Error processing referral code", e)
                Log.e(TAG, "  Error type: ${e.javaClass.simpleName}")
                Log.e(TAG, "  Error message: ${e.message}")
                
                Toast.makeText(
                    this@ReferralScannerActivity,
                    getString(R.string.ReferralScanner_error_format, e.message),
                    Toast.LENGTH_LONG
                ).show()
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Helper function to return successful scan result
     */
    private fun returnResult(referralCode: String, inviteId: String, roscaId: String) {
        val resultIntent = android.content.Intent().apply {
            putExtra("referral_code", referralCode)
            putExtra("invite_id", inviteId)
            putExtra("rosca_id", roscaId)
            putExtra("requires_sync", false)  // Invite already in local DB
        }
        
        Toast.makeText(
            this@ReferralScannerActivity,
            getString(R.string.ReferralScanner_code_verified),
            Toast.LENGTH_SHORT
        ).show()
        
        Log.d(TAG, "✓ Code verification complete - returning to caller")
        setResult(RESULT_OK, resultIntent)
        finish()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "ReferralScannerActivity destroyed")
    }
}
