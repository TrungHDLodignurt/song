// androidApp/build.gradle.kts (Đã sửa)

import org.jetbrains.compose.compose

plugins {
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.multiplatform)
}

android {
    namespace = "org.company.app.android"
    compileSdk = 36

    defaultConfig {
        minSdk = 23
        targetSdk = 36

        applicationId = "org.company.app.androidApp"
        versionCode = 1
        versionName = "1.0.0"
    }

    // --- LỖI CỦA BẠN Ở ĐÂY ---
    // Khối 'buildFeatures' phải nằm BÊN TRONG khối 'android'
    buildFeatures {
        compose = true
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":sharedUI"))
    implementation(libs.androidx.activityCompose)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")

    implementation(compose.runtime)
    implementation(compose.ui)
    implementation(compose.foundation)
    implementation(compose.material3)

    // --- SỬA LỖI 1 TẠI ĐÂY ---
    implementation(compose.components.uiToolingPreview)

    debugImplementation(compose.uiTooling)

    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
}
