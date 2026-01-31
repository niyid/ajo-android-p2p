package com.techducat.ajo.ui.sync

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.techducat.ajo.sync.ReferralCodec
import com.techducat.ajo.core.crypto.KeyManagerImpl
import com.techducat.ajo.data.local.AjoDatabase
import kotlinx.coroutines.launch

/**
 * Complete referral generation UI with QR code display
 */
class ReferralGeneratorActivity : AppCompatActivity() {
    
    private lateinit var db: AjoDatabase
    private lateinit var qrImageView: ImageView
    private lateinit var codeTextView: TextView
    private lateinit var shareButton: Button
    private lateinit var copyButton: Button
    
    private var generatedCode: String = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        db = AjoDatabase.getInstance(this)
        
        val roscaId = intent.getStringExtra("roscaId")
        if (roscaId == null) {
            finish()
            return
        }
        
        setContentView(createLayout())
        generateReferral(roscaId)
    }
    
    private fun createLayout(): ScrollView {
        return ScrollView(this).apply {
            addView(LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(48, 48, 48, 48)
                
                // Title
                addView(TextView(context).apply {
                    text = "Invite to ROSCA"
                    textSize = 28f
                    setTypeface(null, android.graphics.Typeface.BOLD)
                    setPadding(0, 0, 0, 32)
                })
                
                // QR Code
                addView(ImageView(context).apply {
                    qrImageView = this
                    layoutParams = LinearLayout.LayoutParams(600, 600).apply {
                        gravity = android.view.Gravity.CENTER_HORIZONTAL
                    }
                    setPadding(0, 0, 0, 24)
                })
                
                // Code text
                addView(TextView(context).apply {
                    codeTextView = this
                    text = "Generating..."
                    textSize = 12f
                    maxLines = 3
                    setPadding(16, 16, 16, 24)
                    setBackgroundColor(0xFFF5F5F5.toInt())
                })
                
                // Share button
                addView(Button(context).apply {
                    shareButton = this
                    text = "Share Invitation"
                    textSize = 16f
                    setPadding(32, 24, 32, 24)
                    isEnabled = false
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topMargin = 16
                    }
                    setOnClickListener { shareReferral() }
                })
                
                // Copy button
                addView(Button(context).apply {
                    copyButton = this
                    text = "Copy Code"
                    textSize = 16f
                    setPadding(32, 24, 32, 24)
                    isEnabled = false
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topMargin = 8
                    }
                    setOnClickListener { copyCode() }
                })
            })
        }
    }
    
    private fun generateReferral(roscaId: String) {
        lifecycleScope.launch {
            try {
                val rosca = db.roscaDao().getById(roscaId)
                if (rosca == null) {
                    Toast.makeText(this@ReferralGeneratorActivity, "ROSCA not found", Toast.LENGTH_SHORT).show()
                    finish()
                    return@launch
                }
                
                val localNode = KeyManagerImpl.getOrCreateLocalNode(this@ReferralGeneratorActivity)
                val privateKey = KeyManagerImpl.getPrivateKey(this@ReferralGeneratorActivity)
                
                if (privateKey == null) {
                    Toast.makeText(this@ReferralGeneratorActivity, "Failed to get private key", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                
                // Generate referral code
                generatedCode = ReferralCodec.create(
                    roscaId = rosca.id,
                    roscaName = rosca.name,
                    creatorNodeId = localNode.nodeId,
                    creatorPublicKey = localNode.publicKey,
                    creatorEndpoint = "i2p://mock.i2p",  // Will be replaced with real I2P address
                    contributionAmount = rosca.contributionAmount.toDouble(),
                    currency = "USD",
                    frequency = rosca.contributionFrequency,
                    maxMembers = rosca.totalMembers,
                    currentMembers = rosca.currentMembers,
                    privateKey = privateKey
                )
                
                // Generate QR code
                val qrBitmap = QRCodeGenerator.generate(generatedCode, 600)
                
                qrImageView.setImageBitmap(qrBitmap)
                codeTextView.text = generatedCode
                shareButton.isEnabled = true
                copyButton.isEnabled = true
                
            } catch (e: Exception) {
                Toast.makeText(this@ReferralGeneratorActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }
    
    private fun shareReferral() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Join my ROSCA")
            putExtra(Intent.EXTRA_TEXT, "Join my ROSCA with this code:\n\n$generatedCode")
        }
        startActivity(Intent.createChooser(intent, "Share Invitation"))
    }
    
    private fun copyCode() {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Referral Code", generatedCode)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Code copied to clipboard", Toast.LENGTH_SHORT).show()
    }
}
