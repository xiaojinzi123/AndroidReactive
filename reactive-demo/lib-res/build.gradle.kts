@file:Suppress("DEPRECATION")

plugins {
    id("commonLibPlugin")
}

android {
    namespace = "com.xiaojinzi.demo.lib.res"
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    api(libs.xiaojinzi.android.support.annotation)
}
