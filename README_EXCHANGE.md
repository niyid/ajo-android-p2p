# Exchange Integration Guide

## Overview
Ajo-Android integrates with ChangeNow API to allow users to buy and sell Monero directly from the app.

## Setup

### 1. Get ChangeNow API Key
Visit https://changenow.io/api/docs and register for an API key.

### 2. Configure API Key
Update `ExchangeService.kt`:
```kotlin
private const val API_KEY = "YOUR_CHANGENOW_API_KEY"
```

## Features

### Buy Monero
- Buy XMR with credit/debit card
- Supported currencies: USD, EUR, GBP, NGN, and more
- Instant rate estimates
- Status tracking

### Sell Monero
- Sell XMR for fiat currency
- Bank transfer payout
- Real-time status updates

## Alternative Exchanges

If you prefer a different exchange provider, you can integrate:
- **SimpleSwap**: https://simpleswap.io/api
- **StealthEx**: https://stealthex.io/api-docs
- **Trocador**: https://trocador.app/en/docs

## Important Notes

1. **KYC Requirements**: Some exchanges require KYC for large amounts
2. **Fees**: Exchange fees are additional to service fees
3. **Rate Volatility**: Rates are valid for 30 seconds
4. **Network Fees**: Blockchain network fees apply

## Testing

Use testnet for development:
```kotlin
private const val API_BASE_URL = "https://api-sandbox.changenow.io/v2"
```

