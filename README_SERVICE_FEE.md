# Ajo-Android Service Fee System

## Overview
The app implements a **5% service fee** on all ROSCA distributions to support development and maintenance.

## How It Works

### Fee Calculation
- **Rate**: 5% of each distribution amount
- **Deduction**: Automatic on every payout
- **Example**: 
  - Gross payout: 100 XMR
  - Service fee (5%): 5 XMR
  - Net to recipient: 95 XMR

### Service Wallet Addresses

The following Monero addresses are **hardcoded** into the application:

```
Primary: 48ay6EFadeFiKSLPLLpFTLgYQQfmGCAGbBm3S7rg8mYfCG8KZhqBhV1p8KRnhQcD8bQZNJq5JBQfZQkNZJqPjqYR3LKr
Backup:  45ttEikQEZWN1m7VxaVN9rjQkpSdmpGZ83RYvBH2PZG1bQY4V4C5YJqBhV1p8KRnhQcD8bQZNJq5JBQfZQkNZJqPjqYR3LKr
```

### Network Consensus Protection

The service wallet addresses are protected by **network consensus**:

1. **IPFS Registry**: Canonical list of authorized wallets stored on IPFS
2. **P2P Validation**: All nodes verify service wallet addresses
3. **Consensus Threshold**: 67% of nodes must approve a wallet
4. **Fork Detection**: Modified wallets create separate networks

## Transparency

All service fees are publicly auditable on the Monero blockchain.

## License

MIT License - Service fee system is part of the core protocol.

