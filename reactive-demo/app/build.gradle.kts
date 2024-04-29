plugins {
    id("com.android.application")
    id("com.xiaojinzi.kcomponent.plugin")
    id("org.jetbrains.kotlin.android")
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
        minSdk = libs.versions.minSdk.get().toInt()
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

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.kotlinCompilerExtensionVersion.get()
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

}

dependencies {
    implementation(project(":reactive-demo:module-base"))
    implementation(project(":reactive-demo:module-user"))
}