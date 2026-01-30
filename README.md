# Àjọ - Monero ROSCA Application

A decentralized Rotating Savings and Credit Association (ROSCA) application built on Monero blockchain.

## Features

- **Decentralized ROSCAs**: Create and manage savings circles using Monero
- **Multisig Security**: (n-1)-of-n multisig wallets for fund protection
- **Multiple Distribution Methods**: 
  - Predetermined order
  - Random lottery
  - Bidding system
- **DLT Integration**: On-chain metadata storage using Monero tx_extra
- **Privacy**: Leverages Monero's privacy features

## Architecture

- **Language**: Kotlin
- **Blockchain**: Monero (via Monerujo library)
- **Storage**: Room database + On-chain DLT
- **DI**: Koin
- **UI**: Material Design 3

## Project Structure

```
ajo-android/
├── app/
│   ├── src/main/kotlin/com/ajo/monero/
│   │   ├── model/              # Data models (Rosca, Member, etc.)
│   │   ├── core/
│   │   │   ├── crypto/         # Multisig coordination
│   │   │   ├── network/        # P2P messaging
│   │   │   └── security/       # Key management
│   │   ├── dlt/                # DLT providers (Monero, IPFS)
│   │   ├── service/            # Business logic
│   │   ├── repository/         # Data access
│   │   ├── ui/                 # Android UI components
│   │   └── util/               # Utilities
│   └── libs/                   # Monerujo JAR/AAR files
```

## Setup

1. **Prerequisites**:
   - Android Studio Hedgehog or later
   - JDK 17
   - Android SDK 34

2. **Monero Library**:
   - Place Monerujo `monerujo.jar` and `monerujo.aar` in `app/libs/`
   - Place native libraries (`.so` files) in `app/src/main/jniLibs/`

3. **Build**:
   ```bash
   ./gradlew assembleDebug
   ```

4. **Run**:
   ```bash
   ./gradlew installDebug
   ```

## Configuration

Edit `Constants.kt` to configure:
- Monero daemon endpoints
- Network type (mainnet/stagenet/testnet)
- Confirmation thresholds
- Timeout values

## Usage

### Creating a ROSCA

1. Tap "Create ROSCA" on dashboard
2. Fill in:
   - Name
   - Number of members (3-20)
   - Contribution amount (XMR)
   - Frequency (days)
   - Distribution method
3. Share invitation with members

### Joining a ROSCA

1. Receive ROSCA invitation
2. Review terms
3. Join ROSCA
4. Complete multisig setup when full

### Contributing

1. When round starts, tap "Contribute"
2. Confirm transaction
3. Wait for confirmations

### Receiving Payout

1. When selected as recipient, multisig transaction is created
2. Other members sign the transaction
3. Funds are automatically distributed

## Security Considerations

- **Multisig**: Funds require n-1 signatures to move
- **Privacy**: Monero ring signatures hide transaction origins
- **Key Management**: Encrypted wallet passwords in Android Keystore
- **Backup**: Regular backup of multisig wallet data required

## Testing

Run unit tests:
```bash
./gradlew test
```

Run instrumentation tests:
```bash
./gradlew connectedAndroidTest
```

## Contributing

1. Fork the repository
2. Create feature branch
3. Commit changes
4. Push to branch
5. Create Pull Request

## License

MIT License - See LICENSE file for details

## Disclaimer

This is experimental software. Use at your own risk. Always test on stagenet first.

## Support

For issues and questions, please file an issue on GitHub.
