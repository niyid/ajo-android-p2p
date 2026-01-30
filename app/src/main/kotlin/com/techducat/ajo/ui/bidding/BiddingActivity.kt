package com.techducat.ajo.ui.bidding

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.techducat.ajo.R
import com.techducat.ajo.databinding.ActivityBiddingBinding
import com.techducat.ajo.service.RoscaManager
import com.techducat.ajo.model.Bid
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import com.techducat.ajo.wallet.WalletSuite
import java.text.SimpleDateFormat
import java.util.*

class BiddingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBiddingBinding
    private val roscaManager: RoscaManager by inject()
    private val walletSuite: WalletSuite by inject()
    
    private var roscaId: String? = null
    private var roundNumber: Int = 0
    private var currentMemberId: String? = null
    private var poolAmount: Long = 0L
    
    companion object {
        private const val TAG = "BiddingActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBiddingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Get ROSCA info from intent
        roscaId = intent.getStringExtra("ROSCA_ID")
        roundNumber = intent.getIntExtra("ROUND_NUMBER", 0)
        
        if (roscaId == null) {
            Toast.makeText(
                this,
                R.string.BiddingActivity_invalid_rosca,
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }
        
        setupUI()
        loadBiddingInfo()
    }
    
    private fun setupUI() {
        binding.apply {
            // Back button
            toolbar.setNavigationOnClickListener { finish() }
            
            // Submit bid button
            btnSubmitBid.setOnClickListener {
                submitBid()
            }
            
            // Bid amount slider
            sliderBidAmount.addOnChangeListener { _, value, fromUser ->
                if (fromUser) {
                    updateBidPreview(value.toLong())
                }
            }
        }
    }
    
    private fun loadBiddingInfo() {
        lifecycleScope.launch {
            try {
                val rosca = roscaManager.repository.getRoscaById(roscaId!!)
                val round = roscaManager.repository.getRoundByNumber(roscaId!!, roundNumber)
                
                if (rosca == null || round == null) {
                    Toast.makeText(
                        this@BiddingActivity,
                        R.string.BiddingActivity_rosca_round_not_found,
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                    return@launch
                }
                
                // Get current user's member ID
                val userAddress = walletSuite.cachedAddress
                if (userAddress.isNullOrEmpty()) {
                    Toast.makeText(
                        this@BiddingActivity,
                        R.string.BiddingActivity_wallet_not_initialized,
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                    return@launch
                }
                
                val members = roscaManager.repository.getMembersByRoscaId(roscaId!!)
                val currentMember = members.find { it.walletAddress == userAddress }
                
                if (currentMember == null) {
                    Toast.makeText(
                        this@BiddingActivity,
                        R.string.BiddingActivity_not_member,
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                    return@launch
                }
                
                currentMemberId = currentMember.id
                
                binding.apply {
                    // Display ROSCA info
                    tvRoscaName.text = rosca.name
                    tvRoundNumber.text = getString(
                        R.string.BiddingActivity_round_number,
                        roundNumber
                    )
                    
                    // Display pool amount
                    poolAmount = rosca.contributionAmount * rosca.totalMembers
                    tvPoolAmount.text = getString(
                        R.string.BiddingActivity_pool_amount_xmr,
                        String.format("%.6f", poolAmount / 1e12)
                    )
                    
                    // Display deadline
                    val deadline = round.biddingDeadline ?: System.currentTimeMillis()
                    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                    tvBiddingDeadline.text = getString(
                        R.string.BiddingActivity_closes_format,
                        dateFormat.format(Date(deadline))
                    )
                    
                    // Configure slider (bid amount should be less than pool)
                    sliderBidAmount.valueFrom = 0f
                    sliderBidAmount.valueTo = poolAmount.toFloat()
                    sliderBidAmount.value = 0f
                    
                    // Load existing bids
                    loadExistingBids(round.id)
                    
                    // Check if user already bid
                    checkExistingUserBid(round.id, currentMember.id)
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Error loading bidding info", e)
                Toast.makeText(
                    this@BiddingActivity,
                    getString(R.string.BiddingActivity_error_loading_info, e.message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    private suspend fun loadExistingBids(roundId: String) {
        try {
            val bids = roscaManager.repository.getBidsByRoundId(roundId)
            
            binding.apply {
                tvTotalBids.text = getString(
                    R.string.BiddingActivity_total_bids,
                    bids.size
                )
                
                if (bids.isNotEmpty()) {
                    val highestBid = bids.maxByOrNull { it.bidAmount }
                    if (highestBid != null) {
                        tvHighestBid.text = getString(
                            R.string.BiddingActivity_highest_bid,
                            String.format("%.6f", highestBid.bidAmount / 1e12)
                        )
                    }
                } else {
                    tvHighestBid.text = getString(R.string.BiddingActivity_no_bids_yet)
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error loading existing bids", e)
        }
    }
    
    private suspend fun checkExistingUserBid(roundId: String, memberId: String) {
        try {
            val existingBid = roscaManager.repository.getBidByMemberAndRound(
                roundId,
                memberId
            )
            
            if (existingBid != null) {
                binding.apply {
                    sliderBidAmount.value = existingBid.bidAmount.toFloat()
                    tvYourBid.text = getString(
                        R.string.BiddingActivity_current_bid,
                        String.format("%.6f", existingBid.bidAmount / 1e12)
                    )
                    btnSubmitBid.text = getString(R.string.BiddingActivity_update_bid)
                }
                updateBidPreview(existingBid.bidAmount)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error checking existing bid", e)
        }
    }
    
    private fun updateBidPreview(bidAmount: Long) {
        try {
            if (poolAmount <= 0) {
                Log.w(TAG, "Pool amount not yet loaded")
                return
            }
            
            val netPayout = poolAmount - bidAmount
            
            lifecycleScope.launch {
                val rosca = roscaManager.repository.getRoscaById(roscaId!!)
                if (rosca != null) {
                    val dividend = bidAmount / rosca.totalMembers
                    
                    binding.apply {
                        tvBidAmount.text = getString(
                            R.string.BiddingActivity_bid_amount_xmr,
                            String.format("%.6f", bidAmount / 1e12)
                        )
                        
                        tvNetPayout.text = getString(
                            R.string.BiddingActivity_net_payout,
                            String.format("%.6f", netPayout / 1e12)
                        )
                        
                        tvDividend.text = getString(
                            R.string.BiddingActivity_dividend_per_member,
                            String.format("%.6f", dividend / 1e12)
                        )
                        
                        // Calculate effective interest rate
                        val interestRate = if (netPayout > 0) {
                            ((bidAmount.toDouble() / netPayout) * 100)
                        } else 0.0
                        
                        tvEffectiveRate.text = getString(
                            R.string.BiddingActivity_effective_rate,
                            interestRate
                        )
                    }
                } else {
                    binding.apply {
                        tvBidAmount.text = getString(
                            R.string.BiddingActivity_bid_amount_xmr,
                            String.format("%.6f", bidAmount / 1e12)
                        )
                        
                        tvNetPayout.text = getString(
                            R.string.BiddingActivity_net_payout,
                            String.format("%.6f", netPayout / 1e12)
                        )
                        
                        tvDividend.text = getString(R.string.BiddingActivity_calculating)
                        tvEffectiveRate.text = getString(R.string.BiddingActivity_calculating)
                    }
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error updating preview", e)
        }
    }
    
    private fun submitBid() {
        val bidAmount = binding.sliderBidAmount.value.toLong()
        
        if (bidAmount <= 0) {
            Toast.makeText(
                this,
                R.string.BiddingActivity_enter_bid_amount,
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        
        if (bidAmount >= poolAmount) {
            Toast.makeText(
                this,
                R.string.BiddingActivity_bid_less_than_pool,
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        
        if (currentMemberId == null) {
            Toast.makeText(
                this,
                R.string.BiddingActivity_member_id_not_found,
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        
        binding.btnSubmitBid.isEnabled = false
        binding.progressBar.visibility = android.view.View.VISIBLE
        
        lifecycleScope.launch {
            try {
                val round = roscaManager.repository.getRoundByNumber(roscaId!!, roundNumber)
                if (round == null) {
                    Toast.makeText(
                        this@BiddingActivity,
                        R.string.BiddingActivity_round_not_found,
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.btnSubmitBid.isEnabled = true
                    binding.progressBar.visibility = android.view.View.GONE
                    return@launch
                }
                
                // Check if bid already exists
                val existingBid = roscaManager.repository.getBidByMemberAndRound(
                    round.id,
                    currentMemberId!!
                )
                
                if (existingBid != null) {
                    // Update existing bid
                    val updatedBid = existingBid.copy(
                        bidAmount = bidAmount,
                        timestamp = System.currentTimeMillis()
                    )
                    // Fix: Pass only the Bid object
                    roscaManager.repository.updateBid(updatedBid)
                    
                    Toast.makeText(
                        this@BiddingActivity,
                        R.string.BiddingActivity_bid_updated,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Create new bid
                    val newBid = Bid(
                        id = UUID.randomUUID().toString(),
                        roscaId = roscaId!!, // Fix: Added roscaId
                        roundNumber = roundNumber, // Fix: Added roundNumber
                        roundId = round.id,
                        memberId = currentMemberId!!,
                        bidAmount = bidAmount,
                        timestamp = System.currentTimeMillis()
                    )
                    roscaManager.repository.insertBid(newBid)
                    
                    Toast.makeText(
                        this@BiddingActivity,
                        R.string.BiddingActivity_bid_submitted,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                
                finish()
                
            } catch (e: Exception) {
                Log.e(TAG, "Error submitting bid", e)
                Toast.makeText(
                    this@BiddingActivity,
                    getString(R.string.BiddingActivity_error_submitting_bid, e.message),
                    Toast.LENGTH_LONG
                ).show()
                binding.btnSubmitBid.isEnabled = true
            } finally {
                binding.progressBar.visibility = android.view.View.GONE
            }
        }
    }
}
