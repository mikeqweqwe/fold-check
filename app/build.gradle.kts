plugins {
    id("com.android.application")
}

android {
    namespace = "dev.hau.foldcheck"
    compileSdk = 36

    defaultConfig {
        applicationId = "dev.hau.foldcheck"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.1"
    }

    val debugKeystoreFile = rootProject.file("keystore/debug.keystore")
    val hasDebugKeystore = debugKeystoreFile.exists()
    if (!hasDebugKeystore) {
        logger.warn("keystore/debug.keystore 不存在，debug 改用預設簽名（換機安裝會簽章不符）")
    }

    signingConfigs {
        if (hasDebugKeystore) {
            getByName("debug") {
                storeFile = debugKeystoreFile
                storePassword = "foldcheck"
                keyAlias = "foldcheck"
                keyPassword = "foldcheck"
            }
        }
    }

    buildTypes {
        getByName("debug") {
            if (hasDebugKeystore) {
                signingConfig = signingConfigs.getByName("debug")
            }
        }
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation("androidx.window:window:1.5.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")
    implementation("androidx.activity:activity-ktx:1.13.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.11.0")
}
