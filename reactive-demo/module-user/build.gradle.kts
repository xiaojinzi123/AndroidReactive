plugins {
    id("commonDemoModulePlugin")
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.xiaojinzi.demo.module.user"
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}