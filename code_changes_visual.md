# QR Scanner Integration - Code Changes

## Visual Overview

```
┌─────────────────────────────────────────────────────────────┐
│                    DASHBOARD FRAGMENT                        │
│                                                              │
│  ┌────────────────────────────────────────────────────┐    │
│  │           Invite Code Card                          │    │
│  │  ┌──────────────────────────────────────────────┐  │    │
│  │  │  📷  SCAN QR CODE  [Primary Button]         │  │    │
│  │  └──────────────────────────────────────────────┘  │    │
│  │                                                      │    │
│  │            ────────── OR ──────────                 │    │
│  │                                                      │    │
│  │  ┌──────────────────────────────────────────────┐  │    │
│  │  │  Invite Code: [________]                     │  │    │
│  │  └──────────────────────────────────────────────┘  │    │
│  │  ┌──────────────────────────────────────────────┐  │    │
│  │  │  Join with Code  [Outlined Button]          │  │    │
│  │  └──────────────────────────────────────────────┘  │    │
│  └────────────────────────────────────────────────────┘    │
│                                                              │
└─────────────────────────────────────────────────────────────┘
                            │
                            │ User clicks "Scan QR Code"
                            ↓
┌─────────────────────────────────────────────────────────────┐
│              REFERRAL SCANNER ACTIVITY                       │
│                                                              │
│  ┌────────────────────────────────────────────────────┐    │
│  │                                                      │    │
│  │           📷  CAMERA VIEWFINDER                      │    │
│  │                                                      │    │
│  │       [Scan Referral QR Code]                       │    │
│  │                                                      │    │
│  │              ┌────────────┐                          │    │
│  │              │  QR Code   │                          │    │
│  │              │  ▓▓▓▓▓▓▓▓  │  ← User scans            │    │
│  │              │  ▓▓▓▓▓▓▓▓  │                          │    │
│  │              └────────────┘                          │    │
│  │                                                      │    │
│  └────────────────────────────────────────────────────┘    │
│                                                              │
│  [Enter Code Manually]  ← Fallback option                   │
│                                                              │
└─────────────────────────────────────────────────────────────┘
                            │
                            │ Code validated & processed
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                   REFERRAL CODEC                             │
│                                                              │
│  1. Parse QR code data                                       │
│  2. Verify cryptographic signature                           │
│  3. Check expiry timestamp                                   │
│  4. Extract ROSCA information                                │
│                                                              │
└─────────────────────────────────────────────────────────────┘
                            │
                            │ Valid referral
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                   DATABASE OPERATIONS                        │
│                                                              │
│  • Add creator as peer                                       │
│  • Create ROSCA entry                                        │
│  • Add self as member                                        │
│  • Set up sync target                                        │
│                                                              │
└─────────────────────────────────────────────────────────────┘
                            │
                            │ Success
                            ↓
        Returns to Dashboard with refreshed data
```

---

## Code Flow Diagram

```
DashboardFragment.kt
├── onCreate()
│   └── qrScannerLauncher (ActivityResultLauncher)
│       └── Handles result from scanner
│
├── onViewCreated()
│   ├── setupRecyclerView()
│   ├── setupEmptyState()
│   ├── setupErrorState()
│   ├── setupSwipeRefresh()
│   ├── setupFab()
│   ├── setupLoginObservers()
│   ├── setupInviteInput()
│   └── setupQRScanner() ✅ NEW
│       └── binding.btnScanQR.setOnClickListener
│           ├── Check user login
│           └── Launch ReferralScannerActivity
│
└── qrScannerLauncher.callback()
    └── If RESULT_OK
        ├── Get scanned code
        └── processInviteCode(code)
            ├── Validate invite
            ├── Check expiry
            ├── Verify status
            ├── Check capacity
            └── joinRosca()

ReferralScannerActivity.kt
├── onCreate()
│   ├── Check camera permission
│   └── startScanner() or requestPermission()
│
├── startScanner()
│   └── IntentIntegrator
│       ├── setDesiredBarcodeFormats(QR_CODE)
│       └── initiateScan()
│
├── onActivityResult()
│   └── IntentIntegrator.parseActivityResult()
│       └── processReferralCode(scannedData)
│
├── processReferralCode(code)
│   ├── ReferralCodec.parse(code)
│   ├── ReferralCodec.verify(referral)
│   ├── ReferralCodec.isValid(referral)
│   └── consumeReferral(referral)
│
└── consumeReferral(code) ✅ UPDATED
    ├── Insert PeerEntity
    ├── Insert RoscaEntity
    ├── Insert MemberEntity
    ├── Insert SyncTargetEntity
    ├── Toast success message
    ├── setResult(RESULT_OK) ✅ NEW
    └── finish()
```

---

## Key Code Snippets

### 1. Layout Changes (fragment_dashboard.xml)

**BEFORE:**
```xml
<TextInputEditText android:id="@+id/editTextInviteCode" />
<Button android:id="@+id/btnJoinWithCode" android:text="Join ROSCA" />
```

**AFTER:**
```xml
<!-- NEW: Primary QR Scan Button -->
<Button 
    android:id="@+id/btnScanQR"
    android:text="Scan QR Code"
    app:icon="@drawable/ic_qr_code" />

<!-- NEW: Visual Divider -->
<View /> OR <View />

<!-- Existing: Manual Entry (now secondary) -->
<TextInputEditText android:id="@+id/editTextInviteCode" />
<Button 
    android:id="@+id/btnJoinWithCode" 
    android:text="Join with Code"
    style="@style/Widget.Material3.Button.OutlinedButton" />
```

### 2. DashboardFragment.kt Changes

**NEW Import:**
```kotlin
import com.techducat.ajo.ui.sync.ReferralScannerActivity
```

**NEW Launcher:**
```kotlin
private val qrScannerLauncher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
) { result ->
    if (result.resultCode == AppCompatActivity.RESULT_OK) {
        val scannedCode = result.data?.getStringExtra("referral_code")
        if (!scannedCode.isNullOrEmpty()) {
            Log.d(TAG, "✓ QR Code scanned: $scannedCode")
            binding.editTextInviteCode.setText(scannedCode)
            processInviteCode(scannedCode)
        } else {
            checkLoginAndLoadData()
        }
    }
}
```

**NEW Setup Method:**
```kotlin
private fun setupQRScanner() {
    binding.btnScanQR.setOnClickListener {
        val userId = getUserId()
        if (userId.isNullOrEmpty()) {
            Toast.makeText(requireContext(), 
                getString(R.string.Dashboard_please_log_first), 
                Toast.LENGTH_SHORT).show()
            return@setOnClickListener
        }
        
        try {
            val intent = Intent(requireContext(), ReferralScannerActivity::class.java)
            qrScannerLauncher.launch(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error launching QR scanner", e)
            Toast.makeText(requireContext(), 
                "Unable to open QR scanner. Please enter code manually.", 
                Toast.LENGTH_SHORT).show()
        }
    }
}
```

**NEW Call in onViewCreated():**
```kotlin
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    // ... existing setup calls ...
    setupQRScanner()  // ✅ NEW
}
```

### 3. ReferralScannerActivity.kt Changes

**BEFORE:**
```kotlin
private suspend fun consumeReferral(code: com.techducat.ajo.sync.ReferralCode) {
    // ... database operations ...
    Toast.makeText(this, "Successfully joined ${payload.roscaName}!", Toast.LENGTH_LONG).show()
    finish()
}
```

**AFTER:**
```kotlin
private suspend fun consumeReferral(code: com.techducat.ajo.sync.ReferralCode) {
    // ... database operations ...
    Toast.makeText(this, "Successfully joined ${payload.roscaName}!", Toast.LENGTH_LONG).show()
    setResult(RESULT_OK)  // ✅ NEW: Notify caller of success
    finish()
}
```

### 4. AndroidManifest.xml Changes

**NEW Permissions:**
```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature android:name="android.hardware.camera" android:required="false" />
<uses-feature android:name="android.hardware.camera.autofocus" android:required="false" />
```

**NEW Activity Registration:**
```xml
<activity
    android:name=".ui.sync.ReferralScannerActivity"
    android:exported="false"
    android:screenOrientation="portrait"
    android:parentActivityName=".ui.MainActivity" />
```

### 5. strings.xml Changes

**NEW Strings:**
```xml
<string name="Dashboard_scan_qr_code">Scan QR Code</string>
<string name="Dashboard_qr_scanner_error">Unable to open QR scanner</string>
<string name="Dashboard_enter_code_manually">Enter code manually</string>
```

---

## Interaction Sequence

```
User Action                  System Response                  Result
───────────────────────────────────────────────────────────────────────
Opens Dashboard        →     Displays invite card       →    Sees "Scan QR" button
                             with scan button

Clicks "Scan QR Code"  →     Checks login status        →    Login validated
                             
                      →     Requests camera            →    Permission granted
                             permission (if needed)

Camera opens          →     Shows scanner UI           →    Viewfinder active
                             with QR overlay

Points at QR code     →     ZXing detects code         →    Code captured
                             
                      →     Parses referral data       →    Data extracted

                      →     Verifies signature         →    Signature valid

                      →     Checks expiry              →    Not expired

                      →     Validates invite           →    Invite valid

                      →     Joins ROSCA                →    Member added

                      →     Returns RESULT_OK          →    Dashboard notified

Dashboard receives    →     Processes result           →    Refreshes list
result

                      →     Shows success toast        →    "Successfully joined!"

User sees updated     →     ROSCA appears in list      →    ✅ Complete
dashboard
```

---

## Error Handling

### Scenario 1: User Not Logged In
```
Click "Scan QR" → Check getUserId() → null → Toast: "Please log in first"
```

### Scenario 2: Camera Permission Denied
```
Launch Scanner → Request Permission → Denied → Fall back to manual entry option
```

### Scenario 3: Invalid QR Code
```
Scan Code → Parse fails → Toast: "Invalid referral code" → Stay in scanner
```

### Scenario 4: Expired Invite
```
Scan Code → Verify succeeds → Check expiry → Expired → Toast: "Code expired"
```

### Scenario 5: ROSCA Full
```
Scan Code → All checks pass → Check capacity → Full → Toast: "ROSCA is full"
```

### Scenario 6: Scanner Launch Fails
```
Click Button → Intent fails → Catch exception → Toast: "Enter code manually"
```

---

## Testing Commands

### Manual Testing:
```bash
# 1. Check camera permission in manifest
grep "CAMERA" app/src/main/AndroidManifest.xml

# 2. Verify scanner activity registered
grep "ReferralScannerActivity" app/src/main/AndroidManifest.xml

# 3. Check ZXing dependency
grep "zxing" app/build.gradle.kts

# 4. Verify layout has scan button
grep "btnScanQR" app/src/main/res/layout/fragment_dashboard.xml

# 5. Confirm integration in fragment
grep "setupQRScanner" app/src/main/kotlin/com/techducat/ajo/ui/dashboard/DashboardFragment.kt
```

### Build Commands:
```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Install on device
./gradlew installDebug

# Run instrumentation tests
./gradlew connectedAndroidTest
```

---

## Summary

✅ **5 files modified**
✅ **Camera permissions added**
✅ **QR scanner integrated**
✅ **Manual entry preserved**
✅ **Error handling complete**
✅ **Ready for testing**
