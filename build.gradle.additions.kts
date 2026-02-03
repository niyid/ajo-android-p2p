// Add this to your existing app/build.gradle.kts file
// These dependencies should be added to the dependencies block

dependencies {
    // ... existing dependencies ...
    
    // ============ I2P Support ============
    
    // Option 1: If using I2P Android (recommended)
    // Add the I2P Android repository first in settings.gradle.kts:
    // maven { url = uri("https://gitlab.com/api/v4/projects/6869885/packages/maven") }
    
    // I2P Android Base Library
    implementation("net.i2p.android:client:0.9.50")
    
    // Alternative Option 2: Pure Java I2P (if I2P Android not available)
    // implementation("net.i2p:i2p:0.9.50")
    // implementation("net.i2p.client:streaming:0.9.50")
    
    // ============ Additional Network Support ============
    
    // For better socket handling
    implementation("org.apache.commons:commons-lang3:3.13.0")
    
    // For connection pooling (optional but recommended)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    
    // ============ Testing I2P ============
    
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
}
