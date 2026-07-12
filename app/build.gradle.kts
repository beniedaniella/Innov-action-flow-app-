plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace  = "com.innovaction.finance"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.innovaction.finance"
        minSdk        = 26
        targetSdk     = 35
        versionCode   = 1
        versionName   = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // ── Signature Release ──────────────────────────────────────────────────
    // Les clés sont lues depuis local.properties (jamais dans le code source)
    signingConfigs {
        create("release") {
            val props = java.util.Properties()
            val localFile = rootProject.file("local.properties")
            if (localFile.exists()) props.load(localFile.inputStream())
            storeFile     = props.getProperty("KEYSTORE_PATH")?.let { file(it) }
            storePassword = props.getProperty("KEYSTORE_PASSWORD") ?: ""
            keyAlias      = props.getProperty("KEY_ALIAS") ?: ""
            keyPassword   = props.getProperty("KEY_PASSWORD") ?: ""
        }
    }

    buildTypes {
        release {
            isMinifyEnabled     = true
            isShrinkResources   = true
            signingConfig       = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable        = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true }

    ksp {
        arg("room.schemaLocation",   "$projectDir/schemas")
        arg("room.incremental",      "true")
        arg("room.generateKotlin",   "true")
    }

    // Configuration tests
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.splash)

    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)

    implementation(libs.navigation.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.runtime.compose)

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.navigation.compose)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    implementation(libs.coroutines.android)
    implementation(libs.datastore.preferences)

    implementation(libs.workmanager.ktx)
    implementation(libs.hilt.workmanager)
    ksp(libs.hilt.workmanager.compiler)

    implementation(libs.biometric)
    implementation(libs.security.crypto)
    implementation(libs.paging.runtime)
    implementation(libs.paging.compose)

    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso)
    androidTestImplementation(composeBom)
    androidTestImplementation(libs.compose.ui.test.junit4)
}
