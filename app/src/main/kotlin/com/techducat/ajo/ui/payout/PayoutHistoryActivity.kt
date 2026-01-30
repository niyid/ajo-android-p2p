package com.techducat.ajo.ui.payout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.techducat.ajo.model.Payout
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class PayoutHistoryActivity : ComponentActivity() {
    
    private val viewModel: PayoutHistoryViewModel by viewModel()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val roscaId = intent.getStringExtra(EXTRA_ROSCA_ID)
        val memberId = intent.getStringExtra(EXTRA_MEMBER_ID)
        
        setContent {
            MaterialTheme {
                PayoutHistoryScreen(
                    viewModel = viewModel,
                    roscaId = roscaId,
                    memberId = memberId,
                    onBack = { finish() }
                )
            }
        }
    }
    
    companion object {
        const val EXTRA_ROSCA_ID = "rosca_id"
        const val EXTRA_MEMBER_ID = "member_id"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayoutHistoryScreen(
    viewModel: PayoutHistoryViewModel,
    roscaId: String?,
    memberId: String?,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(roscaId, memberId) {
        when {
            roscaId != null -> viewModel.loadPayoutsByRosca(roscaId)
            memberId != null -> viewModel.loadPayoutsByMember(memberId)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payout History") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is PayoutHistoryUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            is PayoutHistoryUiState.Success -> {
                PayoutList(
                    payouts = state.payouts,
                    totalPaid = state.totalPaid,
                    totalPenalties = state.totalPenalties,
                    modifier = Modifier.padding(padding)
                )
            }
            
            is PayoutHistoryUiState.Error -> {
                ErrorView(state.message, { viewModel.refresh() }, Modifier.padding(padding))
            }
        }
    }
}

@Composable
fun PayoutList(
    payouts: List<Payout>,
    totalPaid: Long,
    totalPenalties: Long,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                "Total Paid: ${formatXMR(totalPaid)}",
                style = MaterialTheme.typography.titleMedium
            )
        }
        
        items(payouts) { payout ->
            PayoutCard(payout)
        }
    }
}

@Composable
fun PayoutCard(payout: Payout) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = payout.payoutType.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text("Amount: ${formatXMR(payout.netAmount)}")
            Text("Status: ${payout.status.name}")
        }
    }
}

@Composable
fun ErrorView(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(message)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}

fun formatXMR(amount: Long): String {
    val xmr = amount / 1_000_000.0
    return String.format("%.4f XMR", xmr)
}
