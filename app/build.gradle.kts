import org.bouncycastle.oer.its.etsi102941.Url

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21"
    id("com.google.gms.google-services")
}

android {
    namespace = "com.samkit.costcircle"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.samkit.costcircle"
        minSdk = 26
        targetSdk = 36
        versionCode = 3
        versionName = "1.1.0-beta"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false // Turns on R8
            isShrinkResources = false // Removes unused images/layouts
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }

}

dependencies {
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.animation.core)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.material3)

    //charts
//    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    // Vico for Bar/Line Charts
//    implementation(libs.vico.compose.m3)
//    implementation(libs.compose.charts)
//    implementation("com.patrykandpatryk.vico:core:2.1.2")
    implementation("com.patrykandpatryk.vico:compose:1.16.1")
    implementation("com.patrykandpatryk.vico:compose-m3:1.16.1")


    implementation(libs.androidx.biometric)
    //firebase
    implementation(platform("com.google.firebase:firebase-bom:34.7.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-messaging:23.4.0")
    //splash
    implementation("androidx.core:core-splashscreen:1.2.0")
    //lottie
    implementation("com.airbnb.android:lottie-compose:6.7.1")
    //coil
    implementation("io.coil-kt:coil-compose:2.7.0")
    //google
    // Google Credential Manager (The modern way to login)
    implementation("androidx.credentials:credentials:1.5.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.5.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
    //retrofit
    val retrofit_version = "2.9.0" // Check for the latest version
    val okhttp_version = "4.12.0" // Check for the latest version
    implementation("com.squareup.retrofit2:retrofit:${retrofit_version}")
    implementation("com.squareup.okhttp3:okhttp:${okhttp_version}")
    implementation("com.squareup.retrofit2:converter-gson:${retrofit_version}")
    implementation("com.squareup.okhttp3:logging-interceptor:${okhttp_version}")
//    icons
    implementation("androidx.compose.material:material-icons-extended")
    //koin
    // Koin core
    // Core Koin
    implementation("io.insert-koin:koin-android:3.5.6")
    implementation("io.insert-koin:koin-androidx-compose:3.5.6")

    //navigation 3
    implementation("androidx.navigation3:navigation3-runtime:1.1.0-alpha01")
    implementation("androidx.navigation3:navigation3-ui:1.1.0-alpha01")

    //serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("androidx.compose.material3:material3:1.3.1")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}