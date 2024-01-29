plugins {
    id("commonModulePlugin")
}

android {
    namespace = "com.xiaojinzi.reactive.template"
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    implementation(kotlin("reflect"))
    api(project(":reactive"))
    api(libs.lottie.compose)
}