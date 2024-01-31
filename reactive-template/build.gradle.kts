import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("commonModulePlugin")
    id("androidLibraryPublishPlugin")
}

android {
    namespace = "com.xiaojinzi.reactive.template"
}

dependencies {
    implementation(kotlin("reflect"))
    api(project(":reactive"))
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