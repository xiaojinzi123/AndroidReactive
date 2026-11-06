plugins {
    id("commonDemoModulePlugin")
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.xiaojinzi.demo.module.user"
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}
