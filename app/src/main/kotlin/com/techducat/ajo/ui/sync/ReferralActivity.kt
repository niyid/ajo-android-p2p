package com.techducat.ajo.ui.sync

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.techducat.ajo.sync.ReferralCodec
import com.techducat.ajo.core.crypto.KeyManagerImpl
import com.techducat.ajo.data.local.AjoDatabase
import kotlinx.coroutines.launch

class ReferralActivity : AppCompatActivity() {
    
    private lateinit var db: AjoDatabase
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        db = AjoDatabase.getInstance(this)
        
        setContentView(createUI())
        
        val roscaId = intent.getStringExtra("roscaId") ?: return
        generateReferral(roscaId)
    }
    
    private fun createUI(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
            
            addView(TextView(context).apply {
                text = "Invite to ROSCA"
                textSize = 24f
                setPadding(0, 0, 0, 24)
            })
            
            addView(ImageView(context).apply {
                id = android.R.id.icon
                setPadding(0, 0, 0, 16)
            })
            
            addView(TextView(context).apply {
                id = android.R.id.text1
                text = "Generating..."
                textSize = 12f
            })
            
            addView(Button(context).apply {
                text = "Share Code"
                setOnClickListener {
                    val code = findViewById<TextView>(android.R.id.text1).text.toString()
                    shareReferral(code)
                }
            })
        }
    }
    
    private fun generateReferral(roscaId: String) {
        lifecycleScope.launch {
            val rosca = db.roscaDao().getById(roscaId) ?: return@launch
            val localNode = KeyManagerImpl.getOrCreateLocalNode(this@ReferralActivity)
            val privateKey = KeyManagerImpl.getPrivateKey(this@ReferralActivity) ?: return@launch
            
            val code = ReferralCodec.create(
                roscaId = rosca.id,
                roscaName = rosca.name,
                creatorNodeId = localNode.nodeId,
                creatorPublicKey = localNode.publicKey,
                creatorEndpoint = "mock://localhost",
                contributionAmount = rosca.contributionAmount.toDouble(),
                currency = "USD",
                frequency = rosca.contributionFrequency,
                maxMembers = rosca.totalMembers,
                currentMembers = rosca.currentMembers,
                privateKey = privateKey
            )
            
            // Generate QR - FIXED METHOD NAME
            val qr = QRCodeGenerator.generate(code)
            
            findViewById<ImageView>(android.R.id.icon).setImageBitmap(qr)
            findViewById<TextView>(android.R.id.text1).text = code
        }
    }
    
    private fun shareReferral(code: String) {
        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(android.content.Intent.EXTRA_TEXT, code)
        }
        startActivity(android.content.Intent.createChooser(intent, "Share Referral"))
    }
}
