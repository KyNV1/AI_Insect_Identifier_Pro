import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

// Release signing lives in key.properties (gitignored) — never in tracked source.
val keyPropertiesFile = rootProject.file("key.properties")
val keyProperties = Properties().apply {
    if (keyPropertiesFile.exists()) {
        FileInputStream(keyPropertiesFile).use { load(it) }
    }
}

android {
    namespace = "com.kynv1.aiinsectidentifierpro"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.kynv1.aiinsectidentifierpro"
        minSdk = 24
        targetSdk = 36
        versionCode = 4
        versionName = "1.0.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            if (keyPropertiesFile.exists()) {
                storeFile = file(keyProperties.getProperty("storeFile"))
                storePassword = keyProperties.getProperty("storePassword")
                keyAlias = keyProperties.getProperty("keyAlias")
                keyPassword = keyProperties.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    testOptions {
        unitTests {
            // ViewModels under test touch Android classes (Bundle, Uri) indirectly via
            // AnalyticsHelper/BitmapFactory; without this, unstubbed calls to the stub
            // android.jar throw instead of returning a harmless default.
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    // ProcessLifecycleOwner, for re-checking Play Billing purchases on every app resume
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Navigation & Image Loading
    implementation(libs.androidx.navigation.compose)
    implementation(libs.coil.compose)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)

    // Firebase SDKs
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    // Firebase AI Logic (Gemini) — replaces the raw-API-key generativeai SDK; auth is via
    // the linked Firebase project + App Check, no key embedded in the app.
    implementation(libs.firebase.ai)
    implementation(libs.guava) // required by firebase-ai for one-shot (non-streaming) calls
    implementation(libs.firebase.appcheck.playintegrity)
    // Not debugImplementation: AIInsectIdentifierApp references DebugAppCheckProviderFactory
    // directly (branched on isDebug at runtime), so it must resolve in release compiles too —
    // the branch is simply never taken there.
    implementation(libs.firebase.appcheck.debug)

    // Hilt DI
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    // Google Mobile Ads SDK
    implementation(libs.play.services.ads)
    // Google Play Billing (subscriptions)
    implementation(libs.billing.ktx)
    compileOnly(libs.error.prone.annotations)

    // Logging
    implementation(libs.timber)

    // TensorFlow Lite Audio SDK (Excluded duplicate classes provided by Gemini LiteRT)
    implementation(libs.tensorflow.lite.task.audio) {
        exclude(group = "org.tensorflow", module = "tensorflow-lite-api")
        exclude(group = "org.tensorflow", module = "tensorflow-lite-support")
    }

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    // Real org.json impl for unit tests — the android.jar stub's JSONArray/JSONObject
    // return null from toString() etc. under isReturnDefaultValues, unlike on-device.
    testImplementation(libs.org.json)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}