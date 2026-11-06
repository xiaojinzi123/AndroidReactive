@file:Suppress("DEPRECATION")

plugins {
    id("com.android.application")
    id("com.xiaojinzi.kcomponent.plugin")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.xiaojinzi.reactive.demo"
    signingConfigs {
        maybeCreate("debug").apply {
            storeFile = file("./sign")
            storePassword = "123123"
            keyAlias = "123123"
            keyPassword = "123123"
            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
            enableV4Signing = true
        }
        maybeCreate("release").apply {
            storeFile = file("./sign")
            storePassword = "123123"
            keyAlias = "123123"
            keyPassword = "123123"
            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
            enableV4Signing = true
        }
    }
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        applicationId = "com.xiaojinzi.reactive.demo"
        // 仅 demo app 提升到 23：KComponent v2.3.0 的 minSdk 是 23（库模块仍保持 21）
        minSdk = maxOf(libs.versions.minSdk.get().toInt(), 23)
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
        vectorDrawables {
            useSupportLibrary = true
        }
        buildTypes {
            maybeCreate("debug").apply {
                signingConfig = signingConfigs.getByName("debug")
            }
            maybeCreate("release").apply {
                isMinifyEnabled = true
                signingConfig = signingConfigs.getByName("release")
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro",
                )
            }
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_17)
        targetCompatibility(JavaVersion.VERSION_17)
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(project(":reactive-demo:module-base"))
    implementation(project(":reactive-demo:module-user"))
}