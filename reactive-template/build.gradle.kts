import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("commonLibPlugin")
    id("androidLibraryPublishPlugin")
}

android {
    namespace = "com.xiaojinzi.reactive.template"
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(kotlin("reflect"))
    api(project(":reactive"))
    api(libs.xiaojinzi.android.support.compose)
    api(libs.compose.runtime)
    api(libs.compose.runtime.android)
    api(libs.compose.ui.android)
    api(libs.compose.foundation.android)
    api(libs.lifecycle.viewmodel.compose)
    api(libs.lottie.compose)
}

archivesName.set("reactive-template")

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.xiaojinzi123"
                artifactId = "android-reactive-template"
                version = "0.0.1"
            }
        }
    }
}