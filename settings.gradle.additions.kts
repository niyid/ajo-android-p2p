// Add this to your settings.gradle.kts file

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        
        // ============ I2P Repository ============
        // I2P Android packages
        maven {
            url = uri("https://gitlab.com/api/v4/projects/6869885/packages/maven")
            name = "I2P Android"
        }
        
        // Alternative: I2P Maven repository
        maven {
            url = uri("https://maven.i2p.io/maven2/")
            name = "I2P Maven"
        }
    }
}
