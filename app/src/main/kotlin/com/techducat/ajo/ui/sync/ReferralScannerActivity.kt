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

    private fun processReferralCode(code: String) {
        Log.d(TAG, "=== Processing Referral Code ===")
        Log.d(TAG, "Code length: ${code.length}")
        Log.d(TAG, "Code preview: ${code.take(20)}...")
        
        lifecycleScope.launch {
            try {
                // Step 1: Parse code
                Log.d(TAG, "Step 1: Parsing referral code...")
                val referral = ReferralCodec.parse(code)
                if (referral == null) {
                    Log.w(TAG, "✗ Failed to parse referral code - invalid format")
                    Toast.makeText(
                        this@ReferralScannerActivity, 
                        getString(R.string.ReferralScanner_invalid_code), 
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }
                Log.d(TAG, "✓ Code parsed successfully")
                
                // Step 2: Verify signature
                Log.d(TAG, "Step 2: Verifying signature...")
                if (!ReferralCodec.verify(referral)) {
                    Log.w(TAG, "✗ Signature verification failed")
                    Toast.makeText(
                        this@ReferralScannerActivity, 
                        getString(R.string.ReferralScanner_invalid_signature), 
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }
                Log.d(TAG, "✓ Signature verified")
                
                // Step 3: Check expiry
                Log.d(TAG, "Step 3: Checking code validity/expiry...")
                if (!ReferralCodec.isValid(referral)) {
                    Log.w(TAG, "✗ Code has expired or is invalid")
                    Toast.makeText(
                        this@ReferralScannerActivity, 
                        getString(R.string.ReferralScanner_code_expired), 
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }
                Log.d(TAG, "✓ Code is valid and not expired")
                
                // Step 4: Return code to calling activity
                Log.d(TAG, "Step 4: Returning verified code to caller...")
                val resultIntent = android.content.Intent().apply {
                    putExtra("referral_code", code)
                }
                
                Toast.makeText(
                    this@ReferralScannerActivity,
                    getString(R.string.ReferralScanner_code_verified),
                    Toast.LENGTH_SHORT
                ).show()
                
                Log.d(TAG, "✓ Code verification complete - returning to DashboardFragment")
                setResult(RESULT_OK, resultIntent)
                finish()
                
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
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "ReferralScannerActivity destroyed")
    }
}
