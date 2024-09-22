plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    id("androidx.navigation.safeargs.kotlin") version "2.7.0" apply false // Update to the latest version


    // id("com.google.devtools.ksp") version "1.8.10-1.0.9" apply false

}